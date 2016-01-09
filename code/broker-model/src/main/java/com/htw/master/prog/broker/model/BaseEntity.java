package com.htw.master.prog.broker.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Date;

/**
 * BaseEntity.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
@XmlSeeAlso({Auction.class, Bid.class, Person.class})
@Entity
@Table(name = "BaseEntity")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "discriminator")
public abstract class BaseEntity implements Comparable, Serializable {

    //@XmlElement
    @XmlID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long identity;

    @XmlElement
    @Version
    @NotNull
    @Column(nullable = false, updatable = true)
    private int version;

    @XmlElement
    @NotNull
    @Column(nullable = false, updatable = false, insertable = true)
    private Long creationTimestamp;

    public BaseEntity() {
        this.creationTimestamp = new Date().getTime();
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(Object o) {
        if (o == null) {
            return -1;
        } else {
            if (o instanceof BaseEntity) {
                BaseEntity other = (BaseEntity) o;
                return identity.compareTo(other.getIdentity());
            } else {
                return -1;
            }
        }
    }

    public Long getIdentity() {
        return identity;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Long getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public Date getCreationDate() {
        return new Date(creationTimestamp);
    }
}
