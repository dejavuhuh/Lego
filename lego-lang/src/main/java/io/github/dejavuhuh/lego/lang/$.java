package io.github.dejavuhuh.lego.lang;

/**
 * 万能工具类
 *
 * @author wu.yue
 * @since 2023/12/30 03:02
 */
public class $ {

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    public static void tryCatch(ThrowableTask task) {
        try {
            task.run();
        } catch (Throwable ignored) {

        }
    }
}
