package com.htw.master.prog.broker.model;

import com.htw.master.prog.broker.dao.AuctionDao;
import com.htw.master.prog.broker.dao.BidDao;
import com.htw.master.prog.broker.dao.PersonDao;
import com.htw.master.prog.broker.dao.beans.AuctionDaoBean;
import com.htw.master.prog.broker.dao.beans.BidDaoBean;
import com.htw.master.prog.broker.dao.beans.PersonDaoBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import java.util.Set;

@Ignore
public class BidEntityTest extends EntityTest {

    private static AuctionDao auctionDao;

    private static PersonDao personDao;

    private static BidDao bidDao;

    @Before
    public void before() {
        EntityManager entityManager = getEntityManagerFactory().createEntityManager();

        personDao = new PersonDaoBean(entityManager);
        auctionDao = new AuctionDaoBean(entityManager);
        bidDao = new BidDaoBean(entityManager);
    }

    @Test
    public void testActionAndBidderConstraints() {
        Bid emptyEntity = new Bid();
        Set<ConstraintViolation<Bid>> constraintViolations = getEntityValidatorFactory().getValidator()
            .validate(emptyEntity);
        ConstraintViolation<Bid> auctionNullConstraint = findByField(constraintViolations, "auction");
        Assert.assertNotNull(auctionNullConstraint);
        Assert.assertEquals("may not be null", auctionNullConstraint.getMessage());

        ConstraintViolation<Bid> bidderNullConstraint = findByField(constraintViolations, "bidder");
        Assert.assertNotNull(bidderNullConstraint);
        Assert.assertEquals("may not be null", bidderNullConstraint.getMessage());
    }

    @Test
    public void testPriceConstraints() {
        Bid emptyEntity = new Bid();
        emptyEntity.setPrice(0);
        Set<ConstraintViolation<Bid>> constraintViolations = getEntityValidatorFactory().getValidator()
            .validate(emptyEntity);
        ConstraintViolation<Bid> auctionNullConstraint = findByField(constraintViolations, "price");
        Assert.assertNotNull(auctionNullConstraint);
        Assert.assertEquals("must be greater than or equal to 1", auctionNullConstraint.getMessage());

        constraintViolations.clear();
        emptyEntity.setPrice(1);
        constraintViolations = getEntityValidatorFactory().getValidator().validate(emptyEntity);
        auctionNullConstraint = findByField(constraintViolations, "price");
        Assert.assertNull("Price should be valid!", auctionNullConstraint);

    }

    @Test
    public void testLifeCycle() {
        Person seller = EntityTestUtility.createDefaultPerson();
        personDao.create(seller);
        getWasteBasket().add(seller.getIdentity());

        Auction auction = EntityTestUtility.createDefaultAction(seller);
        auctionDao.create(auction);
        getWasteBasket().add(auction.getIdentity());

        Bid notValidBid = new Bid(auction, seller);
        try {
            bidDao.create(notValidBid);
            Assert.fail("Should be an exception because Bidder and Seller is the same person!");
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }

        Person bidder = personDao.findOne(1);
        Bid validBid = new Bid(auction,bidder);
        Assert.assertNull("Identity should be null!", validBid.getIdentity());
        bidDao.create(validBid);
        Assert.assertNotNull("Identity should not be null!", validBid.getIdentity());
        getWasteBasket().add(validBid.getIdentity());

        Bid findBid = bidDao.findOne(validBid.getIdentity());
        Assert.assertTrue("Bidder is not valid!", findBid.getBidder().compareTo(bidder) == 0);
        Assert.assertTrue("Auction is not valid!", findBid.getAuction().compareTo(auction) == 0);
    }

    public static ConstraintViolation<Bid> findByField(Set<ConstraintViolation<Bid>> constraintViolations,
        String field) {
        for (ConstraintViolation constraintViolation : constraintViolations) {
            String notValidField = constraintViolation.getPropertyPath().toString();
            if (notValidField.equals(field)) {
                return constraintViolation;
            }
        }
        return null;
    }
}
