package com.deft.authservice.data.redis;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;
import java.util.UUID;

/**
 * @author Sergey Golitsyn
 * created on 05.10.2023
 */
@RedisHash(value = "user", timeToLive = 300)
@Getter
@Setter
public class UserRedis {

    @Id
    private String id;
    private String userId;
    private List<String> roles;

}
