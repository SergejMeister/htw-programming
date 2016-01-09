package com.htw.master.prog.broker.model;

import de.sb.java.validation.Inequal;
import org.glassfish.jersey.message.filtering.EntityFiltering;

import javax.enterprise.util.AnnotationLiteral;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entity Auction.
 */
@XmlRootElement
@Table(name = "Auction")
@Entity
@PrimaryKeyJoinColumn(name = "auctionIdentity")
@Inequal(operator = Inequal.Operator.GREATER, leftAccessPath = {"closureTimestamp"}, rightAccessPath = {
        "creationTimestamp"})
public class Auction extends BaseEntity {

    @OneToMany(mappedBy = "auction", cascade = CascadeType.REMOVE)
    private final List<Bid> bids;

    @XmlElement
    @NotNull
    @Column(nullable = false, updatable = true)
    @Size(min = 1, max = 255)
    private String title;

    @XmlElement
    @Min(1)
    @Column(nullable = false, updatable = true)
    private int unitCount;

    @XmlElement
    @Min(1)
    @Column(nullable = false, updatable = true)
    private long askingPrice;

    @XmlElement
    @NotNull
    @Column(nullable = false, updatable = true)
    private Long closureTimestamp;

    @XmlElement
    @NotNull
    @Column(nullable = false, updatable = true)
    @Size(min = 1, max = 8189)
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sellerReference", nullable = false, updatable = false, insertable = true)
    private Person seller;

    @XmlElement
    @Transient
    private Long ownerBidPrice;

    public Auction() {
        this(null);
    }

    public Auction(Person seller) {
        this.bids = new ArrayList<>();
        this.seller = seller;
        setUnitCount(1);
        setAskingPrice(1);
        LocalDateTime today = LocalDateTime.now();
        setCreationTimestamp(Date.from(today.atZone(ZoneId.systemDefault()).toInstant()).getTime());
        Instant tomorrow = today.plusDays(1).atZone(ZoneId.systemDefault()).toInstant();
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

    public long getAskingPrice() {
        return askingPrice;
    }

    public void setAskingPrice(long askingPrice) {
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

    @XmlElement
    @XmlSellerAsEntityFilter
    public Person getSeller() {
        return seller;
    }

    @XmlElement
    @XmlSellerAsReferenceFilter
    public long getSellerReference() {
        return seller == null ? 0 : seller.getIdentity();
    }

    @XmlElement
    @XmlBidsAsEntityFilter
    public List<Bid> getBids() {
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

    @XmlElement(name = "closed")
    public boolean isClosed() {
        return new Date().after(getClosureTimestamp());
    }

    @XmlElement(name = "sealed")
    public boolean isSealed() {
        return 0 < bids.size() || isClosed();
    }

    public Long getOwnerBidPrice() {
        return ownerBidPrice;
    }

    public void setOwnerBidPrice(Long ownerBidPrice) {
        this.ownerBidPrice = ownerBidPrice;
    }

    /**
     * Filter annotation for associated sellers marshaled as entities.
     */
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @EntityFiltering
    @SuppressWarnings("all")
    static public @interface XmlSellerAsEntityFilter {
        static final class Literal extends AnnotationLiteral<XmlSellerAsEntityFilter>
                implements XmlSellerAsEntityFilter {
        }
    }

    /**
     * Filter annotation for associated sellers marshaled as references.
     */
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @EntityFiltering
    @SuppressWarnings("all")
    static public @interface XmlSellerAsReferenceFilter {
        static final class Literal extends AnnotationLiteral<XmlSellerAsReferenceFilter>
                implements XmlSellerAsReferenceFilter {
        }
    }

    /**
     * Filter annotation for associated bids marshaled as entities.
     */
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @EntityFiltering
    @SuppressWarnings("all")
    static public @interface XmlBidsAsEntityFilter {
        static final class Literal extends AnnotationLiteral<XmlBidsAsEntityFilter> implements XmlBidsAsEntityFilter {
        }
    }
}
