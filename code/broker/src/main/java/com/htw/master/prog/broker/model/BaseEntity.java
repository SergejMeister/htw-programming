package com.htw.master.prog.broker.model;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * BaseEntity.
 */
@Entity
@Table(name = "BaseEntity")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "discriminator")
public abstract class BaseEntity implements Comparable {

    @Id
    @GeneratedValue
    private Long identity;

    @NotNull
    private Integer version;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTimestamp;

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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }
}
