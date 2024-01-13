package io.github.dejavuhuh.lego.autoconfigure;

import io.github.dejavuhuh.lego.base.Constants;
import io.github.dejavuhuh.lego.rollback.sql.RollbackSQLGenerator;
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
@Configuration(Constants.BEAN_ID_PREFIX + "RollbackSqlAutoConfiguration")
@AutoConfiguration(after = DataSourceAutoConfiguration.class)
public class RollbackSqlAutoConfiguration {

    @Bean(Constants.BEAN_ID_PREFIX + "RollbackSQLGenerator")
    public RollbackSQLGenerator rollbackSQLGenerator(DataSource dataSource) {
        return new RollbackSQLGenerator(dataSource);
    }
}
