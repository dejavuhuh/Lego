package io.github.dejavuhuh.lego.id;

/**
 * WorkerId提供者
 *
 * @author wu.yue
 * @since 2024/1/12 00:25
 */
public interface WorkerIdProvider {

    /**
     * 获取WorkerId
     *
     * @return workerId
     */
    long getWorkerId();
}
