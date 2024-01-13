package io.github.dejavuhuh.lego.redis;

/**
 * Redis API
 *
 * @author wu.yue
 * @since 2024/1/12 09:26
 */
public interface RedisAPI {

    String KEY_SEPARATOR = ":";

    String createCacheKey(String originalKey);

    <T> T executeLuaScript(String luaScript, Class<T> returnType, Object... args);
}
