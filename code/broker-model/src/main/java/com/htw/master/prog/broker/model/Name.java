package com.htw.master.prog.broker.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Name model.
 */
@Embeddable
public class Name {

    @Column(name = "familyName", nullable = false, updatable = true)
    @NotNull
    @Size(min = 1, max = 31, message = "An event's family name must contain between 1 and 31 characters.")
    private String family;

    @Column(name = "givenName", nullable = false, updatable = true)
    @NotNull
    @Size(min = 1, max = 31, message = "An event's given name must contain between 1 and 31 characters.")
    private String given;

    protected Name() {
    }

    public Name(String family, String given) {
        setFamily(family);
        setGiven(given);
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getGiven() {
        return given;
    }

    public void setGiven(String given) {
        this.given = given;
    }

    @Override
    public String toString() {
        return "Name{" +
            "family='" + family + '\'' +
            ", given='" + given + '\'' +
            '}';
    }
}
