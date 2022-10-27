package com.una.system.sharing.config;

import com.una.common.pojo.User;
import com.una.common.pojo.UserSessionList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializationContext.RedisSerializationContextBuilder;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  @Bean("configurationRedisOperations")
  public ReactiveRedisOperations<String, String> configurationRedisOperations(
      final ReactiveRedisConnectionFactory factory) {
    final Jackson2JsonRedisSerializer<String> serializer = new Jackson2JsonRedisSerializer<>(
        String.class);
    final RedisSerializationContextBuilder<String, String> builder = RedisSerializationContext
        .newSerializationContext(new StringRedisSerializer());
    final RedisSerializationContext<String, String> context = builder.value(serializer).build();
    return new ReactiveRedisTemplate<>(factory, context);
  }

  @Bean("manangerUserRedisOperations")
  public ReactiveRedisOperations<String, UserSessionList> manangerUserRedisOperations(
      final ReactiveRedisConnectionFactory factory) {
    Jackson2JsonRedisSerializer<UserSessionList> serializer = null;
    serializer = new Jackson2JsonRedisSerializer<>(UserSessionList.class);
    RedisSerializationContextBuilder<String, UserSessionList> builder = null;
    builder = RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
    final RedisSerializationContext<String, UserSessionList> context = builder.value(serializer)
        .build();
    return new ReactiveRedisTemplate<>(factory, context);
  }

  @Bean("shortTermOperations")
  public ReactiveRedisOperations<String, Object> shortTermOperations(
      final ReactiveRedisConnectionFactory factory) {
    final Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(
        Object.class);
    final RedisSerializationContextBuilder<String, Object> builder = RedisSerializationContext
        .newSerializationContext(new StringRedisSerializer());
    final RedisSerializationContext<String, Object> context = builder.value(serializer).build();
    return new ReactiveRedisTemplate<>(factory, context);
  }

  @Bean("userRedisOperations")
  public ReactiveRedisOperations<String, User> userRedisOperations(
      final ReactiveRedisConnectionFactory factory) {
    final Jackson2JsonRedisSerializer<User> serializer = new Jackson2JsonRedisSerializer<>(
        User.class);
    final RedisSerializationContextBuilder<String, User> builder = RedisSerializationContext
        .newSerializationContext(new StringRedisSerializer());
    final RedisSerializationContext<String, User> context = builder.value(serializer).build();
    return new ReactiveRedisTemplate<>(factory, context);
  }

}
