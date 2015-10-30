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

    @Column(name = "familyName")
    @NotNull
    //@Size(min = 2, max = 200, message = "An event's family name must contain between 2 and 200 characters.")
    private String family;

    @Column(name = "givenName")
    @NotNull
    //@Size(min = 2, max = 200, message = "An event's given name must contain between 2 and 200 characters.")
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
