package com.deft.authservice.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * @author Sergey Golitsyn
 * created on 05.10.2023
 */
@Entity
@Table
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {

    private String name;
    private String code;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "permission_role",
            joinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id", referencedColumnName = "id")})
    private List<Permission> permissions;
}
