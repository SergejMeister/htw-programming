package com.htw.master.prog.broker;

import com.htw.master.prog.broker.model.Auction;
import com.htw.master.prog.broker.model.Bid;
import com.htw.master.prog.broker.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
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
        CriteriaQuery<Bid> bidCriteriaQuery = entityManager.getCriteriaBuilder().createQuery(Bid.class);
        List<Bid> bids = entityManager.createQuery(bidCriteriaQuery).getResultList();
        for (Bid bid : bids) {
            LOG.info("Bid identity: " + bid.getIdentity());
            LOG.info("Bid person: " + bid.getBidder().getName());
        }

        CriteriaQuery<Person> personCriteriaQuery = entityManager.getCriteriaBuilder().createQuery(Person.class);
        List<Person> persons = entityManager.createQuery(personCriteriaQuery).getResultList();
        for (Person person : persons) {
            LOG.info("Person identity: " + person.getIdentity());
            LOG.info("Bid person: " + person.getName());
            for (Bid personBid : person.getBids()) {
                LOG.info("Bid identity: " + personBid.getIdentity());
                LOG.info("Bid price: " + personBid.getPrice());
            }
        }

        CriteriaQuery<Auction> auctionCriteriaQuery = entityManager.getCriteriaBuilder().createQuery(Auction.class);
        List<Auction> auctions = entityManager.createQuery(auctionCriteriaQuery).getResultList();
        for (Auction auction : auctions) {
            LOG.info("Auction identity: " + auction.getIdentity());
            LOG.info("Auction title: " + auction.getTitle());
            LOG.info("Auction closure timestamp: " + auction.getClosureTimestamp());
        }

        LOG.info("Finish broker model");
    }

}
