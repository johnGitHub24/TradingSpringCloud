package com.trading.cloud.loop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 【職責】啟動提供系統信任分數 API 的 loop-service Spring Boot 應用。
 * 【技巧】{@code @SpringBootApplication} 組合元件掃描、自動設定與組態註解，建立服務容器。
 * 【概念】啟動類只負責交給 Spring 建立元件圖；Controller 與 Service 的協作不應手動寫在此處。
 * 【邊界】不定義信任分數規則、HTTP 路由或資料保存策略。
 */
@SpringBootApplication
public class LoopServiceApplication {

    /** 啟動 loop-service。 */
    public static void main(String[] args) {
        SpringApplication.run(LoopServiceApplication.class, args);
    }
}
