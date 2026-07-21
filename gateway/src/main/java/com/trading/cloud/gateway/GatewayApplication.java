package com.trading.cloud.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 【職責】啟動 Spring Cloud Gateway 應用並啟用儀表板聚合使用的 Feign 用戶端。
 * 【技巧】結合 {@code @SpringBootApplication} 的自動設定與 {@code @EnableFeignClients} 的介面代理掃描。
 * 【概念】啟動類是 Spring 容器的組裝入口；路由與遠端呼叫元件由容器建立並注入，而非在此手動配置。
 * 【邊界】不處理下游業務邏輯；代理規則見 {@link com.trading.cloud.gateway.config.GatewayRouteConfig}。
 */
@SpringBootApplication
@EnableFeignClients
public class GatewayApplication {

    /** 啟動 Spring Boot 容器並交由其管理 Gateway 元件生命週期。 */
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
