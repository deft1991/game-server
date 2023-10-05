package com.deft.authservice.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Sergey Golitsyn
 * created on 05.10.2023
 */
@Entity
@Table
@Data
@EqualsAndHashCode(callSuper = true)
public class Permission extends BaseEntity {
    private String name;
    private String code;
}
