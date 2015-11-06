package com.htw.master.prog.broker.dao.beans;

import com.htw.master.prog.broker.dao.BidDao;
import com.htw.master.prog.broker.model.Bid;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

@Stateless
public class BidDaoBean extends GenericDaoBean<Bid> implements BidDao {

    public BidDaoBean() {
        super(Bid.class);
    }

    public BidDaoBean(EntityManager entityManager) {
        super(entityManager, Bid.class);
    }
}
