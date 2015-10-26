package com.htw.master.prog.broker.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Contact model.
 */
public class Contact {

    public static final String MAIL_PATTERN = "[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+";

    @NotNull
    @Pattern(regexp = MAIL_PATTERN, message = "{invalid.email}")
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
