package com.htw.master.prog.broker.model;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * Address model.
 */
@Embeddable
public class Address {

    private String street;

    private String postCode;

    @NotNull

    private String city;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
