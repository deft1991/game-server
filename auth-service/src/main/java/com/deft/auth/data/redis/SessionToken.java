package com.deft.auth.data.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

/**
 * @author Sergey Golitsyn
 * created on 05.10.2023
 *
 * Session Token for Authentication. Store uniq token and user Id into Session Store
 */
@RedisHash(value = "session_token", timeToLive = 300)
@Getter
@Setter
public class SessionToken {

    @Id
    private String id;
    private String userId;

}
