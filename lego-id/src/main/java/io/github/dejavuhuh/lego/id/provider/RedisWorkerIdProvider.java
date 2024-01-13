package io.github.dejavuhuh.lego.id.provider;

import io.github.dejavuhuh.lego.id.WorkerIdProvider;
import io.github.dejavuhuh.lego.redis.RedisAPI;
import io.github.dejavuhuh.lego.spring.ResourceReader;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 基于Redis的雪花算法的workerId提供者
 *
 * @author wu.yue
 * @since 2024/1/11 15:02
 */
public class RedisWorkerIdProvider implements WorkerIdProvider, DisposableBean {

    private final String clientId = UUID.randomUUID().toString();
    private final String prefix;
    private final int ttl = 600;
    private final RedisAPI redisAPI;
    private final Long workerId;
    private final String renewWorkerIdScript;
    private final String returnWorkerIdScript;

    public RedisWorkerIdProvider(RedisAPI redisAPI) throws IOException {
        this.redisAPI = redisAPI;
        this.prefix = redisAPI.createCacheKey("worker-id");
        this.workerId = acquireWorkerId();
        this.renewWorkerIdScript = ResourceReader.readFileFromResources("lua/renew_worker_id.lua");
        this.returnWorkerIdScript = ResourceReader.readFileFromResources("lua/return_worker_id.lua");
    }

    private long acquireWorkerId() throws IOException {
        String acquireWorkerIdScript = ResourceReader.readFileFromResources("lua/acquire_worker_id.lua");

        long workerId = redisAPI.executeLuaScript(acquireWorkerIdScript, Long.class, prefix, clientId, ttl);

        if (workerId < 0) {
            throw new IllegalStateException("申请workerId失败");
        }

        return workerId;
    }

    /**
     * 360秒续一次TTL，保证workerId不会过期，如果节点宕机了，最多在360秒后就会使workerId失效
     */
    @Scheduled(initialDelay = 360, fixedDelay = 360, timeUnit = TimeUnit.SECONDS)
    public void scheduledRenew() {
        if (workerId == null) {
            // TODO 严重错误，需要记录系统错误日志
            throw new IllegalStateException("workerId仍未初始化");
        }

        long ret = redisAPI.executeLuaScript(renewWorkerIdScript, Long.class, prefix, clientId, ttl, workerId);

        if (ret < 0) {
            // TODO 严重错误，需要记录系统错误日志
            throw new IllegalStateException("workerId续租失败，workerId=" + workerId);
        }
    }

    @Override
    public long getWorkerId() {
        return workerId;
    }

    @Override
    public void destroy() {
        Long ret = redisAPI.executeLuaScript(returnWorkerIdScript, Long.class, prefix, clientId, workerId);
        if (ret < 0) {
            // TODO 严重错误，需要记录系统错误日志
            throw new IllegalStateException("归还workerId失败，workerId=" + workerId);
        }
    }
}
