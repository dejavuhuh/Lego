package io.github.dejavuhuh.lego.lang;

/**
 * TODO
 *
 * @author wu.yue
 * @since 2023/12/30 04:31
 */
@FunctionalInterface
public interface ThrowableTask {

    void run() throws Throwable;
}
