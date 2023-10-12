package com.deft.auth.repo.postgres;

import com.deft.auth.data.entity.AuthUser;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.HibernateHints;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Sergey Golitsyn
 * created on 05.10.2023
 *
 *
 */
@Repository
public interface AuthUserRepository extends CrudRepository<AuthUser, String> {
    @QueryHints(@QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "true"))
    Optional<AuthUser> findByUsername(String username);
}
