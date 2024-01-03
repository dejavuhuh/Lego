package com.github.dejavuhuh.lego.lang;

import java.util.function.Supplier;

/**
 * 断言工具类
 *
 * @author wu.yue
 * @since 2023/12/30 03:35
 */
public class Check {

    public static <T> void notNull(T reference, Supplier<String> nameSupplier) {
        if (reference == null) {
            throw new NullPointerException(nameSupplier.get());
        }
    }

    public static <T> void notNull(T reference, String name) {
        notNull(reference, () -> name);
    }
}
