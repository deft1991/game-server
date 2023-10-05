package com.deft.authservice.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author Sergey Golitsyn
 * created on 05.10.2023
 */
@Entity
@Table
@Data
@EqualsAndHashCode(callSuper = true)
@Cache(region = "PermissionCache", usage = CacheConcurrencyStrategy.READ_WRITE)
public class Permission extends BaseEntity {
    private String name;
    private String code;
}
