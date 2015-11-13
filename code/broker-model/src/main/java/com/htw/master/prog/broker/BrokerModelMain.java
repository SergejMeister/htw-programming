package com.htw.master.prog.broker;

import com.htw.master.prog.broker.dao.AuctionDao;
import com.htw.master.prog.broker.dao.BidDao;
import com.htw.master.prog.broker.dao.PersonDao;
import com.htw.master.prog.broker.dao.beans.AuctionDaoBean;
import com.htw.master.prog.broker.dao.beans.BidDaoBean;
import com.htw.master.prog.broker.dao.beans.PersonDaoBean;
import com.htw.master.prog.broker.model.Auction;
import com.htw.master.prog.broker.model.Bid;
import com.htw.master.prog.broker.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class BrokerModelMain {

    private static final String PERSISTENCE_UNIT_NAME = "broker";
    private final Logger LOG = LoggerFactory.getLogger(BrokerModelMain.class);

    public BrokerModelMain() {
    }

    public static void main(String[] args) {
        new BrokerModelMain().run();
    }

    public void run() {
        LOG.info("Start broker model ...");

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        BidDao bidDao = new BidDaoBean(entityManager);

        List<Bid> bids = bidDao.findAll();
        for (Bid bid : bids) {
            LOG.info("Bid identity: " + bid.getIdentity());
            LOG.info("Bid person: " + bid.getBidder().getName());
        }

        PersonDao personDao = new PersonDaoBean(entityManager);
        List<Person> persons = personDao.findAll();
        for (Person person : persons) {
            LOG.info("Person identity: " + person.getIdentity());
            LOG.info("Bid person: " + person.getName());
            for (Bid personBid : person.getBids()) {
                LOG.info("Bid identity: " + personBid.getIdentity());
                LOG.info("Bid price: " + personBid.getPrice());
            }
        }

        LOG.info("Create a new Person");
        Person smPerson = new Person();
        smPerson.setAlias("SM");
        smPerson.getName().setFamily("Meister");
        smPerson.getName().setGiven("Sergej");
        smPerson.getAddress().setCity("Berlin");
        smPerson.getContact().setEmail("sergej.meister@mail.de");
        personDao.create(smPerson);

        Person findCreatedPerson = personDao.findOne(smPerson.getIdentity());
        LOG.info("Sergej Person identity: " + findCreatedPerson.getIdentity());
        LOG.info("Sergej person name: " + findCreatedPerson.getName());

        LOG.info("Delete a created Person");
        personDao.delete(findCreatedPerson);

        AuctionDao auctionDao = new AuctionDaoBean(entityManager);
        List<Auction> auctions = auctionDao.findAll();
        for (Auction auction : auctions) {
            LOG.info("Auction identity: " + auction.getIdentity());
            LOG.info("Auction title: " + auction.getTitle());
            LOG.info("Auction closure timestamp: " + auction.getClosureTimestamp());
        }

        LOG.info("Finish broker model");
    }
}
