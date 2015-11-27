package com.htw.master.prog.broker.model;

import com.htw.master.prog.broker.enums.Group;
import com.htw.master.prog.broker.util.HashUtility;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Set;

/**
 * Person Entity.
 */
@Table(name = "Person")
@Entity
@PrimaryKeyJoinColumn(name = "personIdentity")
@XmlRootElement
public class Person extends BaseEntity {

    private static final byte[] EMPTY_PASSWORD_HASH = HashUtility.hashAsByte("");

    @OneToMany(mappedBy = "seller", cascade = CascadeType.REMOVE)
    private Set<Auction> auctions;

    @OneToMany(mappedBy = "bidder", cascade = CascadeType.REMOVE)
    private Set<Bid> bids;

    @XmlElement
    @NotNull
    @Size(min = 1, max = 16, message = "An event's person alias must contain between 2 and 80 characters.")
    @Column(unique = true, nullable = false, updatable = true)
    private String alias;

    @XmlElement
    @NotNull
    @Column(nullable = false, updatable = true)
    private byte[] passwordHash;

    @Column(name = "groupAlias", nullable = false, updatable = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @XmlElement
    private Group group;

    @Embedded
    @Valid
    @NotNull
    @XmlElement
    private Name name;

    @Embedded
    @Valid
    @NotNull
    @XmlElement
    private Address address;

    @Embedded
    @Valid
    @NotNull
    @XmlElement
    private Contact contact;

    public Person() {
        this(Group.USER);
    }

    public Person(Group group) {
        this.group = group;
        this.name = new Name();
        this.address = new Address();
        this.contact = new Contact();
        this.auctions = new HashSet<>();
        this.bids = new HashSet<>();
        this.passwordHash = EMPTY_PASSWORD_HASH;
    }

    public static byte[] passwordHash(String password) {
        return HashUtility.hashAsByte(password);
    }

    public Set<Auction> getAuctions() {
        return auctions;
    }

    public Set<Bid> getBids() {
        return bids;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public byte[] getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(byte[] passwordHash) {
        this.passwordHash = passwordHash;
    }

    @XmlElement
    public Group getGroup() {
        return group;
    }

    @XmlElement
    public Name getName() {
        return name;
    }

    @XmlElement
    public Address getAddress() {
        return address;
    }

    @XmlElement
    public Contact getContact() {
        return contact;
    }
}
