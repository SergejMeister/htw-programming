package com.htw.master.prog.broker.rest;

import com.htw.master.prog.broker.model.Auction;
import com.htw.master.prog.broker.model.Bid;
import com.htw.master.prog.broker.model.Person;
import com.htw.master.prog.broker.util.FilterUtility;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

@Path("auctions")
public class AuctionService {

    static final String AUCTION_CRITERIA = new StringBuilder()
            .append("select a.identity from Auction as a ")
            .append("JOIN a.seller as s ")
            .append("where (:titleFragment is null or a.title like :titleFragment) ")
            .append("and (:lowerUnitCount is null or a.unitCount >= :lowerUnitCount) ")
            .append("and (:upperUnitCount is null or a.unitCount <= :upperUnitCount) ")
            .append("and (:lowerAskingPrice is null or a.askingPrice >= :lowerAskingPrice) ")
            .append("and (:upperAskingPrice is null or a.askingPrice <= :upperAskingPrice) ")
            .append("and (:lowerClosureTimestamp is null or a.closureTimestamp >= :lowerClosureTimestamp) ")
            .append("and (:upperClosureTimestamp is null or a.closureTimestamp <= :upperClosureTimestamp) ")
            .append("and (:lowerCreationTimestamp is null or a.creationTimestamp >= :lowerCreationTimestamp) ")
            .append("and (:upperCreationTimestamp is null or a.creationTimestamp <= :upperCreationTimestamp) ")
            .append("and (:description is null or a.description = :description) ")
            .append("and (:sellerReference is null or s.identity = :sellerReference)")
            .toString();

    @GET
    @Path("healthcheck")
    @Produces(MediaType.TEXT_PLAIN)
    public String healthCheck() {
        return "OK";
    }

    @SuppressWarnings("unchecked")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Auction.XmlSellerAsReferenceFilter
    public Response getAuctions(
            @HeaderParam("Authorization") final String authentication,
            @DefaultValue("-1") @QueryParam("resultLength") int resultLength,
            @DefaultValue("-1") @QueryParam("resultOffset") int resultOffset,
            @QueryParam("titleFragment") String titleFragment,
            @QueryParam("lowerUnitCount") Short lowerUnitCount,
            @QueryParam("upperUnitCount") Short upperUnitCount,
            @QueryParam("lowerAskingPrice") Double lowerAskingPrice,
            @QueryParam("upperAskingPrice") Double upperAskingPrice,
            @QueryParam("lowerClosureTimestamp") Long lowerClosureTimestamp,
            @QueryParam("upperClosureTimestamp") Long upperClosureTimestamp,
            @QueryParam("lowerCreationTimestamp") Long lowerCreationTimestamp,
            @QueryParam("upperCreationTimestamp") Long upperCreationTimestamp,
            @QueryParam("description") String description,
            @QueryParam("sellerReference") Long sellerReference,
            @QueryParam("closed") Boolean closed) {
        Person requester = LifeCycleProvider.authenticate(authentication);
        final EntityManager em = LifeCycleProvider.brokerManager();
        Query query = em.createQuery(AUCTION_CRITERIA);
        query.setParameter("titleFragment", titleFragment);
        query.setParameter("lowerUnitCount", lowerUnitCount);
        query.setParameter("upperUnitCount", upperUnitCount);
        query.setParameter("lowerAskingPrice", lowerAskingPrice);
        query.setParameter("upperAskingPrice", upperAskingPrice);
        query.setParameter("lowerClosureTimestamp", lowerClosureTimestamp);
        query.setParameter("upperClosureTimestamp", upperClosureTimestamp);
        query.setParameter("lowerCreationTimestamp", lowerCreationTimestamp);
        query.setParameter("upperCreationTimestamp", upperCreationTimestamp);
        query.setParameter("description", description);
        query.setParameter("sellerReference", sellerReference);
        if (resultOffset > 0) {
            query.setFirstResult(resultOffset);
        }
        if (resultLength > 0) {
            query.setMaxResults(resultLength);
        }
        Collection<Long> identities = query.getResultList();
        Collection<Auction> result = new TreeSet<>(Comparator.comparing(Auction::getIdentity));
        for (Long identity : identities) {
            Auction auction = em.find(Auction.class, identity);
            auction = FilterUtility.filterClosed(auction, closed);
            if (auction != null) {
                boolean isNotSeller = auction.getSeller().compareTo(requester) != 0;
                if (isNotSeller) {
                    auction.setOwnerBidPrice(0L);
                    Bid requesterBid = auction.getBid(requester);
                    if (requesterBid != null) {
                        auction.setOwnerBidPrice(requesterBid.getPrice());
                    }
                }

                result.add(auction);
            }
        }

        GenericEntity<?> wrapper = new GenericEntity<Collection<Auction>>(result) {
        };
        Annotation[] filterAnnotations = new Annotation[]{
                new Auction.XmlSellerAsEntityFilter.Literal()
        };
        return Response.ok().entity(wrapper, filterAnnotations).build();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.TEXT_PLAIN)
    @Valid
    public long createOrUpdateAuction(@HeaderParam("Authorization") final String authentication,
                                      @NotNull @Valid final Auction template) {
        Person authorizedPerson = LifeCycleProvider.authenticate(authentication);
        if (template.getTitle() == null || template.getTitle().isEmpty() ||
                template.getDescription() == null || template.getDescription().isEmpty() ||
                template.getAskingPrice() <= 0) {
            throw new ClientErrorException(Response.Status.CONFLICT);
        }

        boolean persistMode = template.getIdentity() == null;
        final EntityManager em = LifeCycleProvider.brokerManager();
        final Person requester = LifeCycleProvider.authenticate(authentication);

        try {
            Auction auction;
            if (persistMode) {
                auction = new Auction(requester);
            } else {
                auction = em.find(Auction.class, template.getIdentity());
                if (auction == null) {
                    throw new NotFoundException();
                }

                if (authorizedPerson.compareTo(auction.getSeller()) != 0) {
                    throw new ClientErrorException(Response.Status.FORBIDDEN);
                }

                if (auction.isSealed() || auction.isClosed()) {
                    throw new ClientErrorException(Response.Status.CONFLICT);
                }
            }

            auction.setAskingPrice(template.getAskingPrice());
            auction.setCreationTimestamp(template.getCreationTimestamp());
            auction.setClosureTimestamp(template.getClosureTimestamp());
            auction.setDescription(template.getDescription());
            auction.setTitle(template.getTitle());
            auction.setUnitCount(template.getUnitCount());
            auction.setVersion(template.getVersion());

            if (persistMode) {
                em.persist(auction);
            }
            try {
                em.getTransaction().commit();
            } finally {
                em.getTransaction().begin();
            }
            if (!persistMode) {
                em.getEntityManagerFactory().getCache().evict(Auction.class, template.getIdentity());
            }

            return auction.getIdentity();
        } catch (final EntityNotFoundException exception) {
            throw new ClientErrorException(Response.Status.NOT_FOUND);
        } catch (final RollbackException exception) {
            throw new ClientErrorException(Response.Status.CONFLICT);
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("{identity}")
    @Bid.XmlAuctionAsReferenceFilter
    @Bid.XmlBidderAsReferenceFilter
    public Auction getAuction(@HeaderParam("Authorization") final String authentication,
                              @PathParam("identity") final long identity) {
        LifeCycleProvider.authenticate(authentication);
        final EntityManager em = LifeCycleProvider.brokerManager();
        try {
            Auction auction = em.find(Auction.class, identity);
            return auction;
        } catch (final EntityNotFoundException exception) {
            throw new ClientErrorException(Response.Status.NOT_FOUND);
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("{identity}/bid")
    @Bid.XmlBidderAsReferenceFilter
    @Bid.XmlAuctionAsReferenceFilter
    public Bid getBidOfRequester(@HeaderParam("Authorization") final String authentication,
                                 @PathParam("identity") final long identity) {
        Person requester = LifeCycleProvider.authenticate(authentication);
        final EntityManager em = LifeCycleProvider.brokerManager();
        try {
            Auction auction = em.find(Auction.class, identity);
            Bid bid = requester.getBid(auction);
            if (bid == null) {
                throw new ClientErrorException(Response.Status.NOT_FOUND);
            }

            return bid;
        } catch (final EntityNotFoundException exception) {
            throw new ClientErrorException(Response.Status.NOT_FOUND);
        }
    }

    @POST
    @Consumes({MediaType.TEXT_PLAIN})
    @Path("{identity}/bid")
    public void updateBid(@HeaderParam("Authorization") final String authentication,
                          @PathParam("identity") final long identity,
                          @Min(0) long price) {
        Person requester = LifeCycleProvider.authenticate(authentication);
        if (price <= 0) {
            throw new ClientErrorException(Response.Status.CONFLICT);
        }

        boolean isRemoveMode = price == 0;
        final EntityManager em = LifeCycleProvider.brokerManager();
        Auction auction = em.find(Auction.class, identity);
        if (auction == null) {
            throw new NotFoundException();
        }
        if (auction.isClosed()) {
            throw new ClientErrorException(Response.Status.CONFLICT);
        }

        if (auction.getSeller().compareTo(requester) == 0) {
            //requester can not bid own auction.
            throw new ClientErrorException(Response.Status.CONFLICT);
        }

        if (!isRemoveMode && auction.getAskingPrice() >= price) {
            throw new ClientErrorException(Response.Status.CONFLICT);
        }

        Bid bid = auction.getBid(requester);
        boolean persistMode = bid == null;

        if (persistMode) {
            em.persist(new Bid(auction, requester, price));
        } else {
            //get Bid from second level cache, if not exist, than find in db.
            bid = em.find(Bid.class, bid.getIdentity());
            if (bid == null) {
                throw new ClientErrorException(Response.Status.NOT_FOUND);
            }
            if (isRemoveMode) {
                em.remove(bid);
            } else {
                //update
                bid.setPrice(price);
            }
        }

        try {
            em.getTransaction().commit();
        } finally {
            em.getTransaction().begin();
        }

        if (persistMode || isRemoveMode) {
            em.getEntityManagerFactory().getCache().evict(Person.class, requester.getIdentity());
            em.getEntityManagerFactory().getCache().evict(Auction.class, auction.getIdentity());
        }
    }
}
