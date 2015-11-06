package com.htw.master.prog.broker.model;

import de.sb.java.validation.Inequal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity Auction.
 */
@Table(schema = "broker", name = "Auction")
@Entity
@PrimaryKeyJoinColumn(name = "auctionIdentity")
@Inequal(operator = Inequal.Operator.GREATER, leftAccessPath = { "closureTimestamp" }, rightAccessPath = {
    "creationTimestamp" })
public class Auction extends BaseEntity {

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL)
    Set<Bid> bids;

    @NotNull
    @Column(nullable = false, updatable = true)
    @Size(min = 1, max = 255)
    private String title;

    @Min(0)
    @Column(nullable = false, updatable = true)
    private int unitCount;

    @Min(0)
    @Column(nullable = false, updatable = true)
    private double askingPrice;

    @NotNull
    //@Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = true)
    private Long closureTimestamp;

    @NotNull
    @Column(nullable = false, updatable = true)
    @Size(min = 1, max = 8189)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sellerReference", nullable = false, updatable = false, insertable = true)
    @NotNull
    private Person seller;

    public Auction() {
        this(null);
    }

    public Auction(Person seller) {
        this.bids = new HashSet<>();
        this.seller = seller;
        setUnitCount(1);
        setAskingPrice(1.0);
        Instant tomorrow = LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant();
        setClosureTimestamp(Date.from(tomorrow));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUnitCount() {
        return unitCount;
    }

    public void setUnitCount(int unitCount) {
        this.unitCount = unitCount;
    }

    public double getAskingPrice() {
        return askingPrice;
    }

    public void setAskingPrice(double askingPrice) {
        this.askingPrice = askingPrice;
    }

    public Date getClosureTimestamp() {
        return new Date(closureTimestamp);
    }

    public void setClosureTimestamp(Date closureTimestamp) {
        this.closureTimestamp = closureTimestamp.getTime();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Person getSeller() {
        return seller;
    }

    public long getSellerReference() {
        return seller == null ? 0 : seller.getIdentity();
    }

    public Set<Bid> getBids() {
        return bids;
    }

    public Bid getBid(Person person) {
        for (Bid bid : bids) {
            if (bid.getBidder().compareTo(person) == 0) {
                return bid;
            }
        }
        return null;
        //JAVA 8 language features is not compatible with jersey version
        //        Optional<Bid> result = bids.stream().filter(bid -> bid.getBidder().compareTo(person) == 0).findFirst();
        //                return result.isPresent() ? result.get() : null;
    }

    public boolean isClosed() {
        return new Date().after(getClosureTimestamp()) || isSealed();
    }

    public boolean isSealed() {
        return unitCount <= 0;
    }
}
