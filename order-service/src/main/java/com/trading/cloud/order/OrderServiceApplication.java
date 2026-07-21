package com.trading.cloud.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 【職責】啟動提供示範訂單查詢 API 的 order-service Spring Boot 應用。
 * 【技巧】以 {@code @SpringBootApplication} 啟用元件掃描與自動設定，將 API、Service、例外處理納入容器。
 * 【概念】啟動入口只建立應用程式邊界；訂單資料與查詢規則交由獨立元件管理，維持職責分離。
 * 【邊界】不在此定義訂單種子資料、查詢端點或錯誤契約。
 */
@SpringBootApplication
public class OrderServiceApplication {

    /** 啟動 order-service。 */
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
