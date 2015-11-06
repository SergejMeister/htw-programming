package com.htw.master.prog.broker.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Contact model.
 */
@Embeddable
public class Contact {

    public static final String MAIL_PATTERN = "[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+";

    @NotNull
    @Pattern(regexp = MAIL_PATTERN, message = "{invalid.email}")
    @Column(unique = true, nullable = false, updatable = true)
    @Size(min = 1, max = 63, message = "An event's email must contain between 1 and 63 characters.")
    private String email;

    @Column(nullable = true, updatable = true)
    @Size(min = 0, max = 63, message = "Max. phone length is 63 characters.")
    private String phone;

    public Contact() {
    }

    public Contact(String email, String phone) {
        setEmail(email);
        setPhone(phone);
    }

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
