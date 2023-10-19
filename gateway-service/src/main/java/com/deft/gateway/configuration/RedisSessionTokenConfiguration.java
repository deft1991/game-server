package com.deft.gateway.configuration;

import com.deft.gateway.data.SessionToken;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveKeyCommands;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.ReactiveStringCommands;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author Sergey Golitsyn
 * created on 14.10.2023
 */
@Configuration
@RequiredArgsConstructor
public class RedisSessionTokenConfiguration {

    private final RedisConnectionFactory factory;

    @Bean
    ReactiveRedisTemplate<String, SessionToken> redisOperations(ReactiveRedisConnectionFactory factory) {
//        Jackson2JsonRedisSerializer<SessionToken> serializer = new Jackson2JsonRedisSerializer<>(SessionToken.class);
//
//        RedisSerializationContext.RedisSerializationContextBuilder<String, SessionToken> builder =
//                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
//
//        RedisSerializationContext<String, SessionToken> context = builder.value(serializer).build();
//
//        return new ReactiveRedisTemplate<>(factory, context);

        RedisSerializationContext<String, SessionToken> serializationContext = RedisSerializationContext
                .<String, SessionToken>newSerializationContext(new StringRedisSerializer())
                .key(new StringRedisSerializer())
                .value(new Jackson2JsonRedisSerializer<>(SessionToken.class))
//                .hashKey(new Jackson2JsonRedisSerializer<>(String.class))
//                .hashValue(new GenericJackson2JsonRedisSerializer())
                .build();
        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }


    @Bean
    public ReactiveKeyCommands keyCommands(final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        return reactiveRedisConnectionFactory.getReactiveConnection()
                .keyCommands();
    }

    @Bean
    public ReactiveStringCommands stringCommands(final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        return reactiveRedisConnectionFactory.getReactiveConnection()
                .stringCommands();
    }

    @PreDestroy
    public void cleanRedis() {
//        RedisConnection.serverCommands().
        factory.getConnection()
                .commands()
                .flushDb();
    }
}
