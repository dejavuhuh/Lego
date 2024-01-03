package com.github.dejavuhuh.lego.autoconfigure;

import com.github.dejavuhuh.lego.rollback.sql.RollbackSqlGenerator;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 自动配置类
 *
 * @author wu.yue
 * @since 2024/1/3 11:27
 */
@Configuration
@AutoConfiguration(after = DataSourceAutoConfiguration.class)
public class RollbackSqlAutoConfiguration {

    @Bean
    public RollbackSqlGenerator rollbackSqlGenerator(DataSource dataSource) {
        return new RollbackSqlGenerator(dataSource);
    }
}
