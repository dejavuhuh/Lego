package io.github.dejavuhuh.lego.rollback.sql;

import com.alibaba.druid.DbType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TODO
 *
 * @author wu.yue
 * @since 2024/1/3 14:22
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:tc:postgresql:15-alpine:///lego?TC_INITSCRIPT=init_postgres.sql"
})
public class RollbackSQLGeneratorTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
    @Autowired
    private RollbackSQLGenerator rollbackSqlGenerator;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/test_case.csv")
    public void runAllTestCases(String originalSQL, String expectedRollbackSQL) {
        String rollbackSQL = rollbackSqlGenerator.generateToSQL(originalSQL, DbType.postgresql);
        assertThat(rollbackSQL.replaceAll("\\s+", "")).isEqualTo(expectedRollbackSQL.replaceAll("\\s+", ""));
    }
}
