package com.htw.master.prog.broker.model;

import com.htw.master.prog.broker.enums.Group;
import com.htw.master.prog.broker.util.HashUtility;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Person Entity.
 */
@Table(name = "Person")
@Entity
public class Person extends BaseEntity {

    @OneToMany(mappedBy = "seller")
    private List<Auction> auctions;

    @OneToMany(mappedBy = "bidder")
    private List<Bid> bids;

    private String alias;

    private byte[] passwordHash;

    @Column(name = "groupAlias")
    @NotNull
    @Enumerated(EnumType.STRING)
    private Group group;

    @Embedded
    private Name name;

    @Embedded
    private Address address;

    @Embedded
    private Contact contact;

    public Person() {
    }

    public Person(Group group) {
        this.group = group;
    }

    public List<Auction> getAuctions() {
        return auctions;
    }

    public void setAuctions(List<Auction> auctions) {
        this.auctions = auctions;
    }

    public List<Bid> getBids() {
        return bids;
    }

    public void setBids(List<Bid> bids) {
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
