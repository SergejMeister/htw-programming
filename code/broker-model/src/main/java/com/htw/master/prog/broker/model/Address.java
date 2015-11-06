package com.htw.master.prog.broker.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Address model.
 */
@Embeddable
public class Address {

    @Column(nullable = true, updatable = true)
    @Size(min = 0, max = 63, message = "Max. street length is 63 characters.")
    private String street;

    @Column(nullable = true, updatable = true)
    @Size(min = 0, max = 15, message = "An event's postCode must contain between 0 and 15 characters.")
    private String postCode;

    @NotNull
    @Column(nullable = false, updatable = true)
    @Size(min = 1, max = 63, message = "An event's city must contain between 1 and 63 characters.")
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
