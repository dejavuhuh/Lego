package com.github.dejavuhuh.lego.lang;

/**
 * 映射
 *
 * @author wu.yue
 * @since 2023/12/30 03:59
 */
@FunctionalInterface
public interface Mapper<S, T> {

    static <T> Mapper<T, T> noop() {
        return t -> t;
    }

    T map(S source);
}
