package io.github.dejavuhuh.lego.redis;

/**
 * Redis Key前缀提供者
 *
 * @author wu.yue
 * @since 2024/1/12 09:37
 */
@FunctionalInterface
public interface KeyPrefixProvider {

    KeyPrefixProvider NO_PREFIX = () -> null;

    String getKeyPrefix();
}
