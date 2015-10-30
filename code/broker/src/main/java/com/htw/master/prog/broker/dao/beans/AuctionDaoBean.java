package com.htw.master.prog.broker.dao.beans;

import com.htw.master.prog.broker.dao.AuctionDao;
import com.htw.master.prog.broker.model.Auction;

import javax.ejb.Stateless;

@Stateless
public class AuctionDaoBean extends GenericDaoBean<Auction> implements AuctionDao {

    public AuctionDaoBean() {
        super(Auction.class);
    }
}
