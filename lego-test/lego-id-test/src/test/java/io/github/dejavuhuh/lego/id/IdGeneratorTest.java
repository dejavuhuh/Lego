package io.github.dejavuhuh.lego.id;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TODO
 *
 * @author wu.yue
 * @since 2024/1/13 01:46
 */
@SpringBootTest
public class IdGeneratorTest {

    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379);

    @Autowired
    private IdGenerator idGenerator;

    @BeforeAll
    static void beforeAll() {
        redis.start();
    }

    @AfterAll
    static void afterAll() {
        redis.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
    }

    @Test
    public void generateId() throws InterruptedException {
        // 线程安全的List
        List<Long> ids = new CopyOnWriteArrayList<>();
        // 1000个线程并发写入
        int threads = 1000;
        int countPerThread = 100;
        CountDownLatch latch = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                for (int j = 0; j < countPerThread; j++) {
                    ids.add(idGenerator.nextId());
                }
                latch.countDown();
            }).start();
        }

        latch.await();
        assertThat(ids).hasSize(threads * countPerThread);
        assertThat(ids.stream().distinct().collect(Collectors.toList())).hasSize(threads * countPerThread);

    }
}
