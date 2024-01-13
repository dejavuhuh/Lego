package io.github.dejavuhuh.lego.redis;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Arrays;
import java.util.Collections;

/**
 * Redis服务
 *
 * @author wu.yue
 * @since 2024/1/12 09:33
 */
@RequiredArgsConstructor
public class RedisService implements RedisAPI {

    private final StringRedisTemplate redisTemplate;
    @Setter
    private KeyPrefixProvider keyPrefixProvider = KeyPrefixProvider.NO_PREFIX;

    @Override
    public String createCacheKey(String originalKey) {
        String keyPrefix = keyPrefixProvider.getKeyPrefix();
        if (keyPrefix != null) {
            return keyPrefix + KEY_SEPARATOR + originalKey;
        }
        return originalKey;
    }

    @Override
    public <T> T executeLuaScript(String luaScript, Class<T> returnType, Object... args) {
        DefaultRedisScript<T> script = new DefaultRedisScript<>(luaScript, returnType);
        Object[] toStringArgs = Arrays.stream(args).map(Object::toString).toArray(Object[]::new);
        return redisTemplate.execute(script, Collections.emptyList(), toStringArgs);
    }
}
