package com.github.dejavuhuh.lego.rollback.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLTimestampExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertInto;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oscar.ast.stmt.OscarSelectStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectStatement;
import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoSelectStatement;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO
 *
 * @author wu.yue
 * @since 2024/1/1 03:33
 */
@Component
@RequiredArgsConstructor
public class DeleteRollbackSqlGenerator {

    private final JdbcTemplate jdbcTemplate;

    public SQLInsertInto generate(SQLDeleteStatement statement, DbType dbType) {
        // 构建查询语句
        SQLSelectStatement selectStatement = buildSelectStatement(statement, dbType);

        // 执行查询

        String selectSql = selectStatement.toString();
        List<Map<String, Object>> data = jdbcTemplate.queryForList(selectSql);
        if (data.isEmpty()) {
            throw new IllegalArgumentException("根据查询语句无法生成对应的回滚语句，因为无数据可删");
        }
        // 构建批量插入语句
        SQLInsertInto insertStatement = new PGInsertStatement();
        insertStatement.setTableSource(statement.getTableName());

        Set<String> columns = data.get(0).keySet();
        for (String column : columns) {
            insertStatement.addColumn(new SQLIdentifierExpr(column));
        }

        for (Map<String, Object> row : data) {
            Collection<Object> values = row.values();
            SQLInsertStatement.ValuesClause valuesClause = new SQLInsertStatement.ValuesClause();
            for (Object value : values) {
                if (value instanceof Timestamp) {
                    value = new SQLTimestampExpr(value.toString());
                }
                valuesClause.addValue(value);
            }
            insertStatement.addValueCause(valuesClause);
        }
        return insertStatement;
    }

    private SQLSelectStatement buildSelectStatement(SQLDeleteStatement statement, DbType dbType) {
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
