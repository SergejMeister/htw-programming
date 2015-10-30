package com.htw.master.prog.broker.model;

import de.sb.java.validation.Inequal;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Bid entity.
 */
@Table(schema = "broker", name = "Bid", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "auctionReference,bidderReference" }) })
@Entity
@PrimaryKeyJoinColumn(name = "bidIdentity")
@Inequal(operator = Inequal.Operator.NOT_EQUAL, leftAccessPath = { "bidder", "identity" }, rightAccessPath = {
    "auction", "seller", "identity" })
//@Inequal(operator=Inequal.Operator.GREATER_EQUAL, leftAccessPath = { "price" }, rightAccessPath = {"auction", "askingPrice"})
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

    protected Bid() {
        this(null, null);
    }

    public Bid(Auction auction, Person bidder) {
        this(1.0, auction, bidder);
    }

    public Bid(Double price, Auction auction, Person bidder) {
        setPrice(price);
        setAuction(auction);
        setBidder(bidder);
    }

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
        return auction == null ? 0 : auction.getIdentity();
    }

    public Person getBidder() {
        return bidder;
    }

    public void setBidder(Person bidder) {
        this.bidder = bidder;
    }

    public long getBidderReference() {
        return bidder == null ? 0 : this.bidder.getIdentity();
    }
}
