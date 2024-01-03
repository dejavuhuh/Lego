package com.github.dejavuhuh.lego.rollback.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertInto;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

/**
 * TODO
 *
 * @author wu.yue
 * @since 2024/1/1 03:08
 */
public class RollbackSqlGenerator {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public RollbackSqlGenerator(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<SQLStatements> generate(String sql, DbType dbType) {

        List<SQLStatement> statements = SQLUtils.parseStatements(sql, dbType);
        List<SQLStatements> rollbackStatements = new ArrayList<>(statements.size());
        for (SQLStatement statement : statements) {
            SQLStatements rollbackStatement;
            if (statement instanceof SQLCreateTableStatement) {
                rollbackStatement =
                        new SQLStatements(
                                new CreateTableRollbackSqlGenerator()
                                        .generate((SQLCreateTableStatement) statement, dbType));
            } else if (statement instanceof SQLDeleteStatement) {
                rollbackStatement =
                        new SQLStatements(
                                new DeleteRollbackSqlGenerator(jdbcTemplate)
                                        .generate((SQLDeleteStatement) statement, dbType));
            } else if (statement instanceof SQLInsertInto) {
                rollbackStatement =
                        new SQLStatements(
                                new InsertRollbackSqlGenerator(jdbcTemplate)
                                        .generate((SQLInsertInto) statement, dbType));
            } else if (statement instanceof SQLUpdateStatement) {
                rollbackStatement =
                        new SQLStatements(
                                new UpdateRollbackSqlGenerator(jdbcTemplate, dataSource)
                                        .generate((SQLUpdateStatement) statement, dbType));
            } else {
                throw new UnsupportedOperationException("不支持对该SQL语句生成回滚语句：" + statement.toString());
            }
            rollbackStatements.add(rollbackStatement);
        }
        return rollbackStatements;
    }

    public String generateToSQL(String sql, DbType dbType) {

        List<SQLStatements> rollbackStatements = generate(sql, dbType);
        StringBuilder sb = new StringBuilder();

        // 倒序遍历
        for (int i = rollbackStatements.size() - 1; i >= 0; i--) {
            List<? extends SQLStatement> statements = rollbackStatements.get(i).getStatements();
            for (int j = 0; j < statements.size(); j++) {
                SQLStatement statement = statements.get(j);
                statement.setAfterSemi(true);
                String rollbackSQL =
                        SQLUtils.toSQLString(
                                statement, dbType, new SQLUtils.FormatOption(true, false));
                sb.append(rollbackSQL);

                // 最后一个循环，不加换行
                if (i == 0 && j == statements.size() - 1) {
                    continue;
                }
                sb.append(System.lineSeparator());
            }
        }

        return sb.toString();
    }
}
