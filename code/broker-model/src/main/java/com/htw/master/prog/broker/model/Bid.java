package com.htw.master.prog.broker.model;

import de.sb.java.validation.Inequal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;

/**
 * Bid entity.
 */
@Table(name = "Bid", uniqueConstraints = {@UniqueConstraint(columnNames = {"auctionReference,bidderReference"})})
@Entity
@PrimaryKeyJoinColumn(name = "bidIdentity")
@Inequal(operator = Inequal.Operator.NOT_EQUAL, leftAccessPath = {"bidder", "identity"}, rightAccessPath = {
        "auction", "seller", "identity"})
public class Bid extends BaseEntity {

    @XmlElement
    @Min(1)
    @Column(nullable = false, updatable = true)
    private double price;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auctionReference", nullable = false, updatable = false, insertable = true)
    @NotNull
    private Auction auction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bidderReference", nullable = false, updatable = false, insertable = true)
    @NotNull
    private Person bidder;

    protected Bid() {
        this(null, null);
    }

    public Bid(Auction auction, Person bidder) {
        setPrice(1.0);
        this.auction = auction;
        this.bidder = bidder;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Auction getAuction() {
        return auction;
    }

    public long getAuctionReference() {
        return auction == null ? 0 : auction.getIdentity();
    }

    public Person getBidder() {
        return bidder;
    }

    public long getBidderReference() {
        return bidder == null ? 0 : this.bidder.getIdentity();
    }
}
