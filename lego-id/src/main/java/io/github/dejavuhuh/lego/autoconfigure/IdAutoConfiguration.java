package io.github.dejavuhuh.lego.autoconfigure;

import io.github.dejavuhuh.lego.base.Constants;
import io.github.dejavuhuh.lego.id.IdGenerator;
import io.github.dejavuhuh.lego.id.WorkerIdProvider;
import io.github.dejavuhuh.lego.id.provider.RedisWorkerIdProvider;
import io.github.dejavuhuh.lego.redis.RedisAPI;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * TODO
 *
 * @author wu.yue
 * @since 2024/1/13 00:07
 */
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties(IdProperties.class)
@Configuration(Constants.BEAN_ID_PREFIX + "IdAutoConfiguration")
public class IdAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean(Constants.BEAN_ID_PREFIX + "WorkerIdProvider")
    public WorkerIdProvider defaultWorkIdProvider(RedisAPI redisAPI) throws IOException {
        return new RedisWorkerIdProvider(redisAPI);
    }

    @Bean(Constants.BEAN_ID_PREFIX + "IdGenerator")
    public IdGenerator idGenerator(WorkerIdProvider workerIdProvider) {
        return new IdGenerator(workerIdProvider);
    }
}
