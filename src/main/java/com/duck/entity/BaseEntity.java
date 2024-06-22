package com.duck.entity;

import com.duck.config.DateTimeConfig;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity implements Serializable {
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private ZonedDateTime createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private ZonedDateTime lastModifiedDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = DateTimeConfig.getCurrentDateTimeInTimeZone();
        this.lastModifiedDate = this.createdDate;
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = DateTimeConfig.getCurrentDateTimeInTimeZone();
    }
}
