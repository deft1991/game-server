package com.deft.authservice.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.OffsetDateTime;

/**
 * @author Sergey Golitsyn
 * created on 05.10.2023
 */
@MappedSuperclass
@Getter
@Setter
public class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -278971545601962170L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    private Instant createDate;
    private Instant updateDate;

    @PrePersist
    void onCreate() {
        this.createDate = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updateDate = Instant.now();
    }
}
