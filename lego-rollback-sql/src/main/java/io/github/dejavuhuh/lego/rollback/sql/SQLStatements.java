package io.github.dejavuhuh.lego.rollback.sql;

import com.alibaba.druid.sql.ast.SQLStatement;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * TODO
 *
 * @author wu.yue
 * @since 2024/1/2 21:55
 */
@Getter
public class SQLStatements {

    private final List<? extends SQLStatement> statements;

    public SQLStatements(SQLStatement statement) {
        this.statements = Collections.singletonList(statement);
    }

    public SQLStatements(List<? extends SQLStatement> statements) {
        this.statements = statements;
    }
}
