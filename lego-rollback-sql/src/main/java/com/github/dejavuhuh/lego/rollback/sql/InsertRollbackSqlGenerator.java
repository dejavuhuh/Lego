package com.github.dejavuhuh.lego.rollback.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertInto;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import com.alibaba.druid.sql.dialect.oscar.ast.stmt.OscarDeleteStatement;
import com.alibaba.druid.sql.dialect.oscar.ast.stmt.OscarSelectStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGDeleteStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectStatement;
import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoSelectStatement;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author wu.yue
 * @since 2024/1/2 16:40
 */
@RequiredArgsConstructor
public class InsertRollbackSqlGenerator {

    private final JdbcTemplate jdbcTemplate;

    public List<SQLDeleteStatement> generate(SQLInsertInto statement, DbType dbType) {
        SQLExprTableSource table = statement.getTableSource();
        List<SQLExpr> columns = statement.getColumns();
        List<SQLInsertStatement.ValuesClause> rows = statement.getValuesList();

        List<SQLDeleteStatement> result = new ArrayList<>(rows.size());
        for (SQLInsertStatement.ValuesClause row : rows) {
            List<SQLExpr> values = row.getValues();
            SQLExpr where = null;
            for (int i = 0; i < values.size(); i++) {
                SQLExpr column = columns.get(i);
                SQLExpr value = values.get(i);
                SQLBinaryOpExpr condition;
                if (value instanceof SQLNullExpr) {
                    condition = SQLBinaryOpExpr.isNull(column);
                } else {
                    condition = SQLBinaryOpExpr.eq(column, value);
                }
                where = SQLBinaryOpExpr.and(where, condition);
            }

            // 拼接Select语句
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
            selectQueryBlock.addSelectItem(
                    new SQLSelectItem(
                            new SQLAggregateExpr("count", null, new SQLAllColumnExpr()), "COUNT"));
            selectQueryBlock.setFrom(table);
            select.setQuery(selectQueryBlock);
            selectStatement.setSelect(select);
            selectStatement.addWhere(where);

            // 执行查询
            String selectSql = selectStatement.toString();
            List<Map<String, Object>> data = jdbcTemplate.queryForList(selectSql);
            long count = (long) data.get(0).get("COUNT");
            if (count > 0) {
                throw new IllegalStateException("根据INSERT语句无法生成对应的回滚语句, 因为VALUES条件不唯一: " + values);
            }

            // 构建Delete语句
            SQLDeleteStatement deleteStatement;
            if (dbType == DbType.postgresql) {
                deleteStatement = new PGDeleteStatement();
            } else if (dbType == DbType.oscar) {
                deleteStatement = new OscarDeleteStatement();
            } else if (dbType == DbType.mysql) {
                deleteStatement = new MySqlDeleteStatement();
            } else if (dbType == DbType.oracle) {
                deleteStatement = new OracleDeleteStatement();
            } else {
                deleteStatement = new SQLDeleteStatement(dbType);
            }
            deleteStatement.setTableSource(table);
            deleteStatement.setWhere(where);
            result.add(deleteStatement);
        }

        return result;
    }
}
