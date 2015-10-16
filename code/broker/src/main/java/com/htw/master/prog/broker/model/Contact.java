package com.htw.master.prog.broker.model;

import javax.validation.constraints.NotNull;

/**
 * Contact model.
 */
public class Contact {

    @NotNull
    private String email;

    private String phone;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
