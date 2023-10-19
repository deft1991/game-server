package com.deft.gateway.data;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

/**
 * @author Sergey Golitsyn
 * created on 05.10.2023
 * <p>
 * Session Token for Authentication. Store uniq token and user Id into Session Store
 */
@RedisHash(value = "session_token", timeToLive = 300)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SessionToken {

    private String id;
    private String userId;

    public static void deserializeField(SessionToken sessionToken, String field, String value) {
        switch (field) {
            case "id" -> sessionToken.setId(value);
            case "userId" -> sessionToken.setUserId(value);
        }
    }
}
