package com.htw.master.prog.broker.model;

import de.sb.java.validation.Inequal;
import org.glassfish.jersey.message.filtering.EntityFiltering;

import javax.enterprise.util.AnnotationLiteral;
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
import javax.xml.bind.annotation.XmlRootElement;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bid entity.
 */
@Table(name = "Bid", uniqueConstraints = {@UniqueConstraint(columnNames = {"auctionReference,bidderReference"})})
@Entity
@PrimaryKeyJoinColumn(name = "bidIdentity")
@Inequal(operator = Inequal.Operator.NOT_EQUAL, leftAccessPath = {"bidder", "identity"}, rightAccessPath = {
        "auction", "seller", "identity"})
@XmlRootElement
public class Bid extends BaseEntity {

    @XmlElement
    @Min(1)
    @Column(nullable = false, updatable = true)
    private long price;

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
        this(auction, bidder, 1);
    }

    public Bid(Auction auction, Person bidder, long price) {
        setPrice(price);
        this.auction = auction;
        this.bidder = bidder;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    // TODO wit XmlElement I have always an infinity loop exception bid->auction->bid
    // @XmlElement
    @XmlAuctionAsEntityFilter
    public Auction getAuction() {
        return auction;
    }

    @XmlElement
    @XmlAuctionAsReferenceFilter
    public long getAuctionReference() {
        return auction == null ? 0 : auction.getIdentity();
    }

    @XmlElement
    @XmlBidderAsEntityFilter
    public Person getBidder() {
        return bidder;
    }

    @XmlElement
    @XmlBidderAsReferenceFilter
    public long getBidderReference() {
        return bidder == null ? 0 : this.bidder.getIdentity();
    }

    /**
     * Filter annotation for associated bidders marshaled as entities.
     */
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @EntityFiltering
    @SuppressWarnings("all")
    static public @interface XmlBidderAsEntityFilter {
        static final class Literal extends AnnotationLiteral<XmlBidderAsEntityFilter>
                implements XmlBidderAsEntityFilter {
        }
    }

    /**
     * Filter annotation for associated bidders marshaled as references.
     */
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @EntityFiltering
    @SuppressWarnings("all")
    static public @interface XmlBidderAsReferenceFilter {
        static final class Literal extends AnnotationLiteral<XmlBidderAsReferenceFilter>
                implements XmlBidderAsReferenceFilter {
        }
    }

    /**
     * Filter annotation for associated auctions marshaled as entities.
     */
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @EntityFiltering
    @SuppressWarnings("all")
    static public @interface XmlAuctionAsEntityFilter {
        static final class Literal extends AnnotationLiteral<XmlAuctionAsEntityFilter>
                implements XmlAuctionAsEntityFilter {
        }
    }

    /**
     * Filter annotation for associated auctions marshaled as references.
     */
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @EntityFiltering
    @SuppressWarnings("all")
    static public @interface XmlAuctionAsReferenceFilter {
        static final class Literal extends AnnotationLiteral<XmlAuctionAsReferenceFilter>
                implements XmlAuctionAsReferenceFilter {
        }
    }
}
