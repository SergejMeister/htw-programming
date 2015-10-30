package com.htw.master.prog.broker.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Bid entity.
 */
@Table(name = "Bid")
@Entity
@PrimaryKeyJoinColumn(name = "bidIdentity")
public class Bid extends BaseEntity {

    @NotNull
    @Min(0)
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auctionReference")
    @NotNull
    private Auction auction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidderReference")
    @NotNull
    private Person bidder;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public long getAuctionReference() {
        return auction.getIdentity();
    }

    public Person getBidder() {
        return bidder;
    }

    public void setBidder(Person bidder) {
        this.bidder = bidder;
    }

    public long getBidderReference() {
        return this.bidder.getIdentity();
    }
}
