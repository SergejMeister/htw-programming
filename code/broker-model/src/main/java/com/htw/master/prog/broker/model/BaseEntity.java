package com.htw.master.prog.broker.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * BaseEntity.
 */
@Entity
//@Table(schema = "broker", name = "BaseEntity")
@Table(name = "BaseEntity")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "discriminator")
public abstract class BaseEntity implements Comparable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long identity;

    @Version
    @NotNull
    @Column(nullable = false, updatable = true)
    private int version;

    @NotNull
    @Column(nullable = false, updatable = false, insertable = true)
    private Long creationTimestamp;

    protected BaseEntity() {
        this.creationTimestamp = new Date().getTime();
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(Object o) {
        if (o == null) {
            return -1;
        } else {
            if (o instanceof BaseEntity) {
                BaseEntity other = (BaseEntity) o;
                return identity.compareTo(other.getIdentity());
            } else {
                return -1;
            }
        }
    }

    public Long getIdentity() {
        return identity;
    }

    public int getVersion() {
        return version;
    }

    public Date getCreationTimestamp() {
        return new Date(creationTimestamp);
    }
}
