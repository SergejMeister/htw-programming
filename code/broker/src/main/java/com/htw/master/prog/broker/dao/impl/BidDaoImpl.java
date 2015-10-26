package com.htw.master.prog.broker.dao.impl;

import com.htw.master.prog.broker.dao.BidDao;
import com.htw.master.prog.broker.model.Bid;

public class BidDaoImpl extends GenericDaoImpl<Bid> implements BidDao {

    public BidDaoImpl() {
        super(Bid.class);
    }
}
