package com.htw.master.prog.broker.rest;


import com.htw.master.prog.broker.enums.Group;
import com.htw.master.prog.broker.model.Auction;
import com.htw.master.prog.broker.model.Bid;
import com.htw.master.prog.broker.model.Person;
import com.htw.master.prog.broker.util.FilterUtility;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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


@Path("people")
public class PersonService {

    static final String PERSON_CRITERIA = new StringBuilder()
            .append("select p.identity from Person as p where ")
            .append("(:group is null or p.group = :group) ")
            .append("and (:alias is null or p.alias = :alias) ")
            .append("and (:family is null or p.name.family = :family) ")
            .append("and (:given is null or p.name.given = :given) ")
            .append("and (:street is null or p.address.street = :street) ")
            .append("and (:postCode is null or p.address.postCode = :postCode) ")
            .append("and (:city is null or p.address.city = :city) ")
            .append("and (:email is null or p.contact.email = :email) ")
            .append("and (:phone is null or p.contact.phone = :phone) ")
            .append("and (:lowerCreationTimestamp is null or p.creationTimestamp >= :lowerCreationTimestamp) ")
            .append("and (:upperCreationTimestamp is null or p.creationTimestamp <= :upperCreationTimestamp)")
            .toString();

    @GET
    @Path("healthcheck")
    @Produces(MediaType.TEXT_PLAIN)
    public String healthCheck() {
        return "OK!";
    }

    @SuppressWarnings("unchecked")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Collection<Person> getPersons(
            @HeaderParam("Authorization") final String authentication,
            @DefaultValue("-1") @QueryParam("resultLength") int resultLength,
            @DefaultValue("-1") @QueryParam("resultOffset") int resultOffset,
            @QueryParam("group") String group,
            @QueryParam("alias") String alias,
            @QueryParam("family") String family,
            @QueryParam("given") String given,
            @QueryParam("street") String street,
            @QueryParam("postCode") String postCode,
            @QueryParam("city") String city,
            @QueryParam("email") String email,
            @QueryParam("phone") String phone,
            @QueryParam("lowerCreationTimestamp") Long lowerCreationTimestamp,
            @QueryParam("upperCreationTimestamp") Long upperCreationTimestamp) {

        LifeCycleProvider.authenticate(authentication);

        final EntityManager em = LifeCycleProvider.brokerManager();
        Query query = em.createQuery(PERSON_CRITERIA);
        if (group == null) {
            query.setParameter("group", group);
        } else {
            Group groupEnum = Group.valueOf(group.toUpperCase());
            query.setParameter("group", groupEnum);
        }
        query.setParameter("alias", alias);
        query.setParameter("family", family);
        query.setParameter("given", given);
        query.setParameter("street", street);
        query.setParameter("postCode", postCode);
        query.setParameter("city", city);
        query.setParameter("email", email);
        query.setParameter("phone", phone);
        query.setParameter("lowerCreationTimestamp", lowerCreationTimestamp);
        query.setParameter("upperCreationTimestamp", upperCreationTimestamp);
        if (resultOffset > 0) {
            query.setFirstResult(resultOffset);
        }
        if (resultLength > 0) {
            query.setMaxResults(resultLength);
        }
        Collection<Long> resultIdentities = query.getResultList();
        Collection<Person> resultPeople = new TreeSet<>(Comparator.comparing(Person::getAlias));
        for (Long identity : resultIdentities) {
            Person person = em.find(Person.class, identity);
            if (person != null) {
                resultPeople.add(person);
            }
        }

        return resultPeople;
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.TEXT_PLAIN)
    public long createOrUpdatePerson(@HeaderParam("Authorization") final String authentication,
                                     @HeaderParam("SET-password") final String password,
                                     @NotNull @Valid final Person template) {
        boolean persistMode = template.getIdentity() == null;
        final EntityManager em = LifeCycleProvider.brokerManager();
        try {
            Person person;
            if (persistMode) {
                person = new Person(template.getGroup());
            } else {
                Person authorizedPerson = LifeCycleProvider.authenticate(authentication);
                person = em.find(Person.class, template.getIdentity());
                if (person == null) {
                    throw new ClientErrorException(Response.Status.UNAUTHORIZED);
                }

                Boolean isAdmin = Group.ADMIN.equals(authorizedPerson.getGroup());
                if (person.compareTo(authorizedPerson) != 0) {
                    //the requester would like to alert data of other person.
                    if (!isAdmin) {
                        throw new ClientErrorException(Response.Status.FORBIDDEN);
                    }
                } else {
                    if (Group.ADMIN.equals(template.getGroup()) && !isAdmin) {
                        //only admin have permissions to set own group to ADMIN
                        throw new ClientErrorException(Response.Status.FORBIDDEN);
                    }
                }
            }

            person.setAlias(template.getAlias());
            if (password != null && !password.isEmpty()) {
                byte[] passwordHash = Person.passwordHash(password);
                person.setPasswordHash(passwordHash);
            }
            person.getName().setFamily(template.getName().getFamily());
            person.getName().setGiven(template.getName().getGiven());
            person.getAddress().setCity(template.getAddress().getCity());
            person.getAddress().setPostCode(template.getAddress().getPostCode());
            person.getAddress().setStreet(template.getAddress().getStreet());
            person.getContact().setEmail(template.getContact().getEmail());
            person.getContact().setPhone(template.getContact().getPhone());
            person.setVersion(template.getVersion());
            person.setGroup(template.getGroup());

            if (persistMode) {
                em.persist(person);
            }
            try {
                em.getTransaction().commit();
            } finally {
                em.getTransaction().begin();
            }
            if (!persistMode) {
                em.getEntityManagerFactory().getCache().evict(Person.class, template.getIdentity());
            }

            return person.getIdentity();
        } catch (final EntityNotFoundException exception) {
            throw new ClientErrorException(Response.Status.NOT_FOUND);
        } catch (final RollbackException exception) {
            throw new ClientErrorException(Response.Status.CONFLICT);
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("{identity}")
    public Person getPerson(@HeaderParam("Authorization") final String authentication,
                            @PathParam("identity") final long identity) {
        LifeCycleProvider.authenticate(authentication);
        final EntityManager em = LifeCycleProvider.brokerManager();
        Person person = em.find(Person.class, identity);
        if (person != null) {
            return person;
        }

        throw new ClientErrorException(Response.Status.NOT_FOUND);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("requester")
    public Person getRequester(@HeaderParam("Authorization") final String authentication) {
        return LifeCycleProvider.authenticate(authentication);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("{identity}/auctions")
    public Response getAuctions(@HeaderParam("Authorization") final String authentication,
                                @PathParam("identity") final long identity,
                                @QueryParam("closed") Boolean closed,
                                @QueryParam("seller") Boolean seller) {
        LifeCycleProvider.authenticate(authentication);
        final EntityManager em = LifeCycleProvider.brokerManager();

        String personCriteria = createSelectQuery(seller);
        Query query = em.createQuery(personCriteria);
        query.setParameter("personIdentity", identity);

        Collection<Long> identities = query.getResultList();
        Collection<Auction> auctions = new TreeSet<>(Comparator.comparing(Auction::getIdentity));
        for (Long auctionIdentity : identities) {
            Auction auction = em.find(Auction.class, auctionIdentity);
            auction = FilterUtility.filterClosed(auction, closed);
            if (auction != null) {
                auctions.add(auction);
            }
        }

        GenericEntity<?> wrapper = new GenericEntity<Collection<Auction>>(auctions) {
        };
        if (closed == null || !closed) {
            if (seller) {
                Annotation[] filterAnnotations = new Annotation[]{new Auction.XmlSellerAsReferenceFilter.Literal()};
                return Response.ok().entity(wrapper, filterAnnotations).build();
            } else {
                return Response.ok().entity(wrapper).build();
            }
        }

        Annotation[] filterAnnotations =
                new Annotation[]{new Auction.XmlBidsAsEntityFilter.Literal(),
                        new Bid.XmlBidderAsEntityFilter.Literal(), new Bid.XmlAuctionAsReferenceFilter.Literal()};
        return Response.ok().entity(wrapper, filterAnnotations).build();
    }

    private String createSelectQuery(Boolean seller) {
        if (seller == null) {
            return new StringBuilder()
                    .append("select a.identity from Auction as a ")
                    .append("left join a.bids  as b ")
                    .append("where a.seller.identity = :personIdentity ")
                    .append("or b.bidder.identity = :personIdentity")
                    .toString();
        } else {
            if (seller) {
                //only auction where person identity is seller
                return new StringBuilder()
                        .append("select a.identity from Auction as a ")
                        .append("where a.seller.identity = :personIdentity")
                        .toString();
            } else {
                //only auction where person identity is bidder
                return new StringBuilder()
                        .append("select a.identity from Auction as a ")
                        .append("left join a.bids as b ")
                        .append("where b.bidder.identity = :personIdentity ")
                        .toString();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("{identity}/bids")
    @Bid.XmlBidderAsReferenceFilter
    @Bid.XmlAuctionAsReferenceFilter
    public Response getBids(@HeaderParam("Authorization") final String authentication,
                            @PathParam("identity") final long identity) {
        Person requester = LifeCycleProvider.authenticate(authentication);
        final EntityManager em = LifeCycleProvider.brokerManager();
        String personCriteria = new StringBuilder()
                .append("select b.identity from Bid as b where ")
                .append("b.bidder.identity = :personIdentity")
                .toString();

        Query query = em.createQuery(personCriteria);
        query.setParameter("personIdentity", identity);

        boolean isSamePerson = requester.getIdentity() == identity;
        Collection<Long> identities = query.getResultList();
        Collection<Bid> bids = new TreeSet<>(Comparator.comparing(Bid::getIdentity));
        for (Long bidIdentity : identities) {
            Bid bid = em.find(Bid.class, bidIdentity);
            if (bid != null) {
                if (isSamePerson) {
                    bids.add(bid);
                } else {
                    if (bid.getAuction().isClosed()) {
                        bids.add(bid);
                    }
                }
            }
        }

        GenericEntity<?> wrapper = new GenericEntity<Collection<Bid>>(bids) {
        };
        Annotation[] filterAnnotations =
                new Annotation[]{new Bid.XmlAuctionAsEntityFilter.Literal(), new Bid.XmlBidderAsEntityFilter.Literal()};
        return Response.ok().entity(wrapper, filterAnnotations).build();
    }
}
