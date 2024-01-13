package io.github.dejavuhuh.lego.portal.controller;

import com.alibaba.druid.DbType;
import com.alibaba.druid.util.JdbcUtils;
import io.github.dejavuhuh.lego.base.Constants;
import io.github.dejavuhuh.lego.rollback.sql.RollbackSQLGenerator;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * TODO
 *
 * @author wu.yue
 * @since 2024/1/5 17:12
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(Constants.API_PREFIX + "/rollback-sql")
public class RollbackSQLController {

    private final RollbackSQLGenerator generator;
    private final DataSource dataSource;

    @PostMapping(value = "/generate")
    public String generate(@RequestBody String originalSQL) throws SQLException {
        @Cleanup Connection connection = dataSource.getConnection();
        String jdbcURL = connection.getMetaData().getURL();
        Driver jdbcDriver = DriverManager.getDriver(jdbcURL);
        String driverClassName = jdbcDriver.getClass().getName();
        DbType dbType = JdbcUtils.getDbTypeRaw(jdbcURL, driverClassName);
        return generator.generateToSQL(originalSQL, dbType);
    }
}
