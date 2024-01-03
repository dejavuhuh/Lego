package com.github.dejavuhuh.lego.rollback.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.SQLUtils.FormatOption;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
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

    private final CreateTableRollbacker createTableRollbacker;
    private final DeleteRollbacker deleteRollbacker;
    private final InsertRollbacker insertRollbacker;
    private final UpdateRollbacker updateRollbacker;

    public RollbackSqlGenerator(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        this.createTableRollbacker = new CreateTableRollbacker();
        this.deleteRollbacker = new DeleteRollbacker(jdbcTemplate);
        this.insertRollbacker = new InsertRollbacker(jdbcTemplate);
        this.updateRollbacker = new UpdateRollbacker(jdbcTemplate, dataSource);
    }

    public List<SQLStatements> generate(String originalSQL, DbType dbType) {

        List<SQLStatement> statements = SQLUtils.parseStatements(originalSQL, dbType);
        List<SQLStatements> rollbackStatements = new ArrayList<>(statements.size());
        for (SQLStatement statement : statements) {
            SQLStatements rollbackStatement;
            if (statement instanceof SQLCreateTableStatement) {
                SQLDropTableStatement dropTableStatement = createTableRollbacker.generate((SQLCreateTableStatement) statement, dbType);
                rollbackStatement = new SQLStatements(dropTableStatement);
            } else if (statement instanceof SQLDeleteStatement) {
                SQLInsertInto insertIntoStatement = deleteRollbacker.generate((SQLDeleteStatement) statement, dbType);
                rollbackStatement = new SQLStatements(insertIntoStatement);
            } else if (statement instanceof SQLInsertInto) {
                List<SQLDeleteStatement> deleteStatements = insertRollbacker.generate((SQLInsertInto) statement, dbType);
                rollbackStatement = new SQLStatements(deleteStatements);
            } else if (statement instanceof SQLUpdateStatement) {
                List<SQLUpdateStatement> updateStatements = updateRollbacker.generate((SQLUpdateStatement) statement, dbType);
                rollbackStatement = new SQLStatements(updateStatements);
            } else {
                throw new UnsupportedOperationException("不支持对该SQL语句生成回滚语句：" + statement.toString());
            }
            rollbackStatements.add(rollbackStatement);
        }
        return rollbackStatements;
    }

    public String generateToSQL(String originalSQL, DbType dbType) {

        List<SQLStatements> rollbackStatements = generate(originalSQL, dbType);
        StringBuilder sb = new StringBuilder();

        // 倒序遍历
        for (int i = rollbackStatements.size() - 1; i >= 0; i--) {
            List<? extends SQLStatement> statements = rollbackStatements.get(i).getStatements();
            for (int j = 0; j < statements.size(); j++) {
                SQLStatement statement = statements.get(j);
                statement.setAfterSemi(true);
                // upper case, no pretty
                FormatOption formatOption = new FormatOption(true, false);
                String rollbackSQL = SQLUtils.toSQLString(statement, dbType, formatOption);
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
