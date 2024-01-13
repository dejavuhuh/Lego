-- PostgreSQL中检查表是否存在并删除的语句
DROP TABLE IF EXISTS t_user;

-- 创建表，PostgreSQL中标识符（如表名和列名）通常是小写的
-- 如果您要使用大写字母或特殊字符，需要使用双引号
CREATE TABLE t_user
(
    id    BIGINT      NOT NULL, -- PostgreSQL中不需要COMMENT关键字，可以在表定义之后单独添加注释
    name  VARCHAR(30) NULL DEFAULT NULL,
    age   INT         NULL DEFAULT NULL,
    email VARCHAR(50) NULL DEFAULT NULL,
    PRIMARY KEY (id)
);

-- PostgreSQL允许在表定义之后单独为每个列添加注释
COMMENT ON COLUMN t_user.id IS '主键ID';
COMMENT ON COLUMN t_user.name IS '姓名';
COMMENT ON COLUMN t_user.age IS '年龄';
COMMENT ON COLUMN t_user.email IS '邮箱';

-- 删除表中所有数据
DELETE
FROM t_user;

-- 向表中插入数据
INSERT INTO t_user (id, name, age, email)
VALUES (1, 'Jone', 18, 'test1@baomidou.com'),
       (2, 'Jack', 20, 'test2@baomidou.com'),
       (3, 'Tom', 28, 'test3@baomidou.com'),
       (4, 'Sandy', 21, 'test4@baomidou.com'),
       (5, 'Billie', 24, 'test5@baomidou.com');
