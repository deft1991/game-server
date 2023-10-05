package com.deft.authservice.repo.redis;

import com.deft.authservice.data.redis.UserRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Sergey Golitsyn
 * created on 05.10.2023
 */
@Repository
public interface UserRepository extends CrudRepository<UserRedis, String> {
}
