package com.ywhc.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * YWHC后台管理系统启动类
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@MapperScan("com.ywhc.admin.modules.**.mapper")
@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
public class YwhcAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(YwhcAdminApplication.class, args);
        System.out.println(
            """

            ██╗   ██╗██╗    ██╗██╗  ██╗ ██████╗    █████╗ ██████╗ ███╗   ███╗██╗███╗   ██╗
            ╚██╗ ██╔╝██║    ██║██║  ██║██╔════╝   ██╔══██╗██╔══██╗████╗ ████║██║████╗  ██║
             ╚████╔╝ ██║ █╗ ██║███████║██║        ███████║██║  ██║██╔████╔██║██║██╔██╗ ██║
              ╚██╔╝  ██║███╗██║██╔══██║██║        ██╔══██║██║  ██║██║╚██╔╝██║██║██║╚██╗██║
               ██║   ╚███╔███╔╝██║  ██║╚██████╗   ██║  ██║██████╔╝██║ ╚═╝ ██║██║██║ ╚████║
               ╚═╝    ╚══╝╚══╝ ╚═╝  ╚═╝ ╚═════╝   ╚═╝  ╚═╝╚═════╝ ╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝

            :: YWHC Admin System ::                                    (v1.0.0)
            :: Spring Boot ::                                          (v3.2.0)
            :: Running on JDK ::                                       (v21)

            🚀 Application started successfully!
            📖 API Documentation: http://localhost:8080/api/doc.html
            """
        );
    }
}
