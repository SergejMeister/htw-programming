package com.htw.master.prog.broker.dao.impl;

import com.htw.master.prog.broker.dao.AuctionDao;
import com.htw.master.prog.broker.model.Auction;

public class AuctionDaoImpl extends GenericDaoImpl<Auction> implements AuctionDao {

    public AuctionDaoImpl() {
        super(Auction.class);
    }
}
