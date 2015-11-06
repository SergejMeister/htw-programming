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
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

/**
 * Person Entity.
 */
@Table(schema = "broker", name = "Person")
@Entity
@PrimaryKeyJoinColumn(name = "personIdentity")
public class Person extends BaseEntity {

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    private Set<Auction> auctions;

    @OneToMany(mappedBy = "bidder", cascade = CascadeType.ALL)
    private Set<Bid> bids;

    @NotNull
    @Size(min = 2, max = 80, message = "An event's person alias must contain between 2 and 80 characters.")
    @Column(unique = true)
    private String alias;

    @NotNull
    @Column
    private byte[] passwordHash;

    @Column(name = "groupAlias")
    @NotNull
    @Enumerated(EnumType.STRING)
    private Group group;

    @Embedded
    @Valid
    private Name name;

    @Embedded
    @Valid
    private Address address;

    @Embedded
    @Valid
    private Contact contact;

    public Person() {
        this(Group.USER);
    }

    public Person(Group group) {
        this.group = group;
        setAuctions(new HashSet<>());
        setBids(new HashSet<>());
    }

    public Set<Auction> getAuctions() {
        return auctions;
    }

    public void setAuctions(Set<Auction> auctions) {
        this.auctions = auctions;
    }

    public Set<Bid> getBids() {
        return bids;
    }

    public void setBids(Set<Bid> bids) {
        this.bids = bids;
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

    //TODO: password hashing in entity object?
    public byte[] passwordHash(String password) throws NoSuchAlgorithmException {
        return HashUtility.hashAsByte(password);
    }

    public Group getGroup() {
        return group;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
