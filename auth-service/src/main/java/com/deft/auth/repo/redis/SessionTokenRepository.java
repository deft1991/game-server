package com.deft.auth.repo.redis;

import com.deft.auth.data.redis.SessionToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Sergey Golitsyn
 * created on 05.10.2023
 *
 * Redis Session Token Repository
 */
@Repository
public interface SessionTokenRepository extends CrudRepository<SessionToken, String> {
}
