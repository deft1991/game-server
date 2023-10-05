package com.deft.authservice.repo;

import com.deft.authservice.data.entity.AuthUser;
import com.deft.authservice.data.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Sergey Golitsyn
 * created on 05.10.2023
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(String name);
    List<Role> findByNameIn(List<String> names);
}
