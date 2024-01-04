package io.github.dejavuhuh.lego.rollback.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.builder.impl.SQLBuilderImpl;
import com.alibaba.druid.sql.builder.impl.SQLUpdateBuilderImpl;
import com.alibaba.druid.sql.dialect.oscar.ast.stmt.OscarSelectStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectStatement;
import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoSelectStatement;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

/**
 * TODO
 *
 * @author wu.yue
 * @since 2024/1/2 22:03
 */
@RequiredArgsConstructor
public class UpdateRollbacker {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @SneakyThrows
    public List<SQLUpdateStatement> generate(SQLUpdateStatement statement, DbType dbType) {
        String tableName = statement.getTableName().getSimpleName();

        // 构建查询语句
        SQLSelectStatement selectStatement = buildSelectStatement(statement, dbType);

        // 执行查询
        String selectSql = selectStatement.toString();

        // 旧数据
        List<Map<String, Object>> oldData = jdbcTemplate.queryForList(selectSql);

        // 找到主键（可能存在多个，比如联合主键）
        @Cleanup Connection conn = dataSource.getConnection();
        DatabaseMetaData meta = conn.getMetaData();
        // 存在首尾的反引号时，移除反引号
        ResultSet pkResultSet =
                meta.getPrimaryKeys(
                        null,
                        null,
                        tableName.startsWith("`") && tableName.endsWith("`")
                                ? tableName.substring(1, tableName.length() - 1)
                                : tableName);
        List<String> primaryKeys = new ArrayList<>();
        while (pkResultSet.next()) {
            String columnName = pkResultSet.getString("COLUMN_NAME");
            primaryKeys.add(columnName);
        }
        if (primaryKeys.isEmpty()) {
            throw new IllegalStateException("根据UPDATE语句无法生成对应的回滚语句，因为无主键");
        }

        List<SQLUpdateStatement> result = new ArrayList<>();
        List<SQLUpdateSetItem> setItems = statement.getItems();
        for (Map<String, Object> row : oldData) {
            SQLUpdateBuilderImpl updateStatementBuilder = new SQLUpdateBuilderImpl(dbType);

            // table
            updateStatementBuilder.from(tableName);

            // set
            for (SQLUpdateSetItem setItem : setItems) {
                SQLExpr column = setItem.getColumn();
                if (column instanceof SQLIdentifierExpr) {
                    String columnName = ((SQLIdentifierExpr) column).getName();
                    Object oldValue = row.get(columnName);
                    updateStatementBuilder.setValue(columnName, oldValue);
                }
            }

            SQLUpdateStatement updateStatement = updateStatementBuilder.getSQLUpdateStatement();
            SQLExpr where = null;
            for (String primaryKey : primaryKeys) {
                SQLExpr columnExpr = SQLUtils.toSQLExpr(primaryKey, dbType);
                Object primaryKeyValue = row.get(primaryKey);
                SQLBinaryOpExpr condition;
                if (primaryKeyValue == null) {
                    condition = SQLBinaryOpExpr.isNull(columnExpr);
                } else {
                    SQLExpr valueExpr = SQLBuilderImpl.toSQLExpr(primaryKeyValue, dbType);
                    condition = SQLBinaryOpExpr.eq(columnExpr, valueExpr);
                }
                where = SQLBinaryOpExpr.and(where, condition);
            }
            updateStatement.setWhere(where);

            result.add(updateStatement);
        }

        return result;
    }

    private SQLSelectStatement buildSelectStatement(SQLUpdateStatement statement, DbType dbType) {
        SQLSelectStatement selectStatement;
        if (dbType == DbType.postgresql) {
            selectStatement = new PGSelectStatement();
        } else if (dbType == DbType.oscar) {
            selectStatement = new OscarSelectStatement();
        } else if (dbType == DbType.presto) {
            selectStatement = new PrestoSelectStatement();
        } else {
            selectStatement = new SQLSelectStatement(dbType);
        }
        SQLSelect select = new SQLSelect();
        SQLSelectQueryBlock selectQueryBlock = new SQLSelectQueryBlock();
        selectQueryBlock.addSelectItem(new SQLSelectItem(new SQLAllColumnExpr()));
        selectQueryBlock.setFrom(statement.getTableSource());
        select.setQuery(selectQueryBlock);
        selectStatement.setSelect(select);
        selectStatement.addWhere(statement.getWhere());
        return selectStatement;
    }
}
