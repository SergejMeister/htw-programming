package com.htw.master.prog.broker.model;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

/**
 * Name model.
 */
public class Name {

    @Column(name = "familyName")
    @NotNull
    private String family;

    @Column(name = "givenName")
    @NotNull
    private String given;

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
}
