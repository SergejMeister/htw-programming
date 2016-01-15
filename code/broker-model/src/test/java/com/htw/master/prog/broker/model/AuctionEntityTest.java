package com.htw.master.prog.broker.model;

import com.htw.master.prog.broker.dao.AuctionDao;
import com.htw.master.prog.broker.dao.PersonDao;
import com.htw.master.prog.broker.dao.beans.AuctionDaoBean;
import com.htw.master.prog.broker.dao.beans.PersonDaoBean;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import java.util.Set;

@Ignore
public class AuctionEntityTest extends EntityTest {

    public static ConstraintViolation<Auction> findByField(Set<ConstraintViolation<Auction>> constraintViolations,
        String field) {
        for (ConstraintViolation constraintViolation : constraintViolations) {
            String notValidField = constraintViolation.getPropertyPath().toString();
            if (notValidField.equals(field)) {
                return constraintViolation;
            }
        }
        return null;
    }

    @Test
    public void testConstraints() {
        Auction emptyAuction = new Auction();

        Set<ConstraintViolation<Auction>> constraintViolations = getEntityValidatorFactory().getValidator()
            .validate(emptyAuction);

        Assert.assertEquals(2, constraintViolations.size());

        ConstraintViolation<Auction> titleNullConstraint = findByField(constraintViolations, "title");
        Assert.assertNotNull(titleNullConstraint);
        Assert.assertEquals("may not be null", titleNullConstraint.getMessage());

        ConstraintViolation<Auction> descriptionNullConstraint = findByField(constraintViolations, "description");
        Assert.assertNotNull(descriptionNullConstraint);
        Assert.assertEquals("may not be null", descriptionNullConstraint.getMessage());

//        ConstraintViolation<Auction> sellerNullConstraint = findByField(constraintViolations, "seller");
//        Assert.assertNotNull(sellerNullConstraint);
//        Assert.assertEquals("may not be null", sellerNullConstraint.getMessage());

        emptyAuction = EntityTestUtility.createDefaultAction();
        constraintViolations.clear();
        constraintViolations = getEntityValidatorFactory().getValidator().validate(emptyAuction);

        Assert.assertEquals(0, constraintViolations.size());
    }

    @Test
    public void testLifeCycle() {
        EntityManager entityManager = getEntityManagerFactory().createEntityManager();
        AuctionDao auctionDao = new AuctionDaoBean(entityManager);
        PersonDao personDao = new PersonDaoBean(entityManager);

        //init test

        // create person
        Person testPerson = EntityTestUtility.createDefaultPerson();
        personDao.create(testPerson);
        getWasteBasket().add(testPerson.getIdentity());

        Auction testAuction = EntityTestUtility.createDefaultAction(testPerson);
        auctionDao.create(testAuction);
        Assert.assertNotNull(testAuction.getIdentity());
        getWasteBasket().add(testAuction.getIdentity());

        //Find created auction
        Auction findAuction = auctionDao.findOne(testAuction.getIdentity());
        Assert.assertNotNull(findAuction);
        Assert.assertTrue(testAuction.compareTo(findAuction) == 0);
        Assert.assertTrue(testAuction.getSeller().compareTo(testPerson) == 0);
        Assert.assertEquals(testAuction.getTitle(), findAuction.getTitle());
        Assert.assertEquals(testAuction.getDescription(), findAuction.getDescription());
        Assert.assertEquals(1, findAuction.getUnitCount());
        Assert.assertFalse("Auction should be open", findAuction.isClosed());

        //update Auction
        testAuction.setUnitCount(2);
        auctionDao.update(testAuction);
        Auction findAuctionWithNewUnitCount = auctionDao.findOne(testAuction.getIdentity());
        Assert.assertEquals("Auction unitCount should be 2",2,findAuctionWithNewUnitCount.getUnitCount());
    }
}
