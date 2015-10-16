package com.htw.master.prog.broker.model;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Entity Auction.
 */
public class Auction extends BaseEntity {

    @OneToMany(mappedBy = "auction")
    List<Bid> bids;

    @NotNull
    private String title;

    @NotNull
    private Integer unitCount;

    @NotNull
    private Double askingPrice;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date closureTimestamp;

    @NotNull
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auctionReference")
    private Person seller;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getUnitCount() {
        return unitCount;
    }

    public void setUnitCount(Integer unitCount) {
        this.unitCount = unitCount;
    }

    public Double getAskingPrice() {
        return askingPrice;
    }

    public void setAskingPrice(Double askingPrice) {
        this.askingPrice = askingPrice;
    }

    public Date getClosureTimestamp() {
        return closureTimestamp;
    }

    public void setClosureTimestamp(Date closureTimestamp) {
        this.closureTimestamp = closureTimestamp;
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

    public void setSeller(Person seller) {
        this.seller = seller;
    }

    public long getSellerReference() {
        return seller.getIdentity();
    }

    public List<Bid> getBids() {
        return bids;
    }

    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }

    public Bid getBid(Person person) {
        Optional<Bid> result = bids.stream().filter(bid -> bid.getBidder().compareTo(person) == 0).findFirst();
        return result.isPresent() ? result.get() : null;
    }

    public boolean isClosed() {
        //TODO: Question: if auction is sealed -> it's closed too?
        return new Date().after(closureTimestamp);
    }

    public boolean isSealed() {
        //TODO sealed if unitCount 0 ???
        throw new NotImplementedException();
    }
}
