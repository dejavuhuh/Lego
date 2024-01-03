package com.github.dejavuhuh.lego.rollback.sql;

import static org.assertj.core.api.Assertions.assertThat;

import com.alibaba.druid.DbType;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * TODO
 *
 * @author wu.yue
 * @since 2024/1/3 14:22
 */
@SpringBootTest
public class RollbackSqlGeneratorTest {

    @Autowired private RollbackSqlGenerator rollbackSqlGenerator;

    @ParameterizedTest
    @CsvFileSource(resources = "/test_case.csv")
    public void runAllTestCases(String originalSQL, String expectedRollbackSQL) {
        String rollbackSQL = rollbackSqlGenerator.generateToSQL(originalSQL, DbType.h2);
        assertThat(rollbackSQL).isEqualTo(expectedRollbackSQL);
    }
}
