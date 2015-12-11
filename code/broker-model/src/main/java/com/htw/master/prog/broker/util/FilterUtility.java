package com.htw.master.prog.broker.util;

import com.htw.master.prog.broker.model.Auction;


public final class FilterUtility {
    private FilterUtility() {
    }

    public static Auction filterClosed(Auction auction, Boolean closed) {
        if (auction == null) {
            return null;
        }

        if (closed == null) {
            return auction;
        } else {
            if (closed) {
                //return only closed auction
                return auction.isClosed() ? auction : null;
            } else {
                //return only open auction
                return auction.isClosed() ? null : auction;
            }

        }
    }
}
