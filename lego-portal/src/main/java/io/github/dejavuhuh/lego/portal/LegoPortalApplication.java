package io.github.dejavuhuh.lego.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动类
 *
 * @author wu.yue
 * @since 2024/1/5 17:07
 */
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@SpringBootApplication
@EnableAspectJAutoProxy
public class LegoPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(LegoPortalApplication.class, args);
    }
}

