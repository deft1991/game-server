package com.deft.auth.data.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;

import java.util.List;

/**
 * @author Sergey Golitsyn
 * created on 05.10.2023
 *
 * Security roles in our system. We can close some endpoints with @PreAuthorize
 */

@Entity
@Table
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Cacheable
@Cache(region = "RoleCache", usage = CacheConcurrencyStrategy.READ_WRITE)
public class Role extends BaseEntity {

    private String name;
    private String code;

    @Cache(region = "PermissionCache", usage = CacheConcurrencyStrategy.READ_WRITE)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "permission_role",
            joinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id", referencedColumnName = "id")})
    private List<Permission> permissions;
}
