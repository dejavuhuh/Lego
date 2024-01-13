package io.github.dejavuhuh.lego.autoconfigure;

import io.github.dejavuhuh.lego.base.Constants;
import io.github.dejavuhuh.lego.redis.KeyPrefixProvider;
import io.github.dejavuhuh.lego.redis.RedisAPI;
import io.github.dejavuhuh.lego.redis.RedisService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis 自动配置
 *
 * @author wu.yue
 * @since 2024/1/12 09:27
 */
@Configuration(Constants.BEAN_ID_PREFIX + "RedisAutoConfiguration")
@AutoConfigureAfter(org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class)
public class RedisAutoConfiguration {

    @Bean(Constants.BEAN_ID_PREFIX + "RedisAPI")
    public RedisAPI redisAPI(
            RedisConnectionFactory redisConnectionFactory,
            ObjectProvider<KeyPrefixProvider> keyPrefixProvider) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate(redisConnectionFactory);
        RedisService redisService = new RedisService(redisTemplate);
        keyPrefixProvider.ifAvailable(redisService::setKeyPrefixProvider);
        return redisService;
    }
}
