package com.github.dejavuhuh.lego.rollback.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;

/**
 * TODO
 *
 * @author wu.yue
 * @since 2024/1/1 03:15
 */
public class CreateTableRollbackSqlGenerator {

    public SQLDropTableStatement generate(SQLCreateTableStatement statement, DbType dbType) {
        SQLExprTableSource table = statement.getTableSource();
        // 构建 drop table 语句
        return new SQLDropTableStatement(table, dbType);
    }
}
