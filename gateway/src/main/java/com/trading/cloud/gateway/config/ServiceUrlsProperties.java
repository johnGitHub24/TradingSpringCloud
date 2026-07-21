package com.trading.cloud.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 【職責】承載 Gateway 連往 loop-service 與 order-service 的下游基底 URL 設定。
 * 【技巧】使用 {@code @ConfigurationProperties} 將 {@code trading.services} 前綴的外部設定綁定為型別安全欄位。
 * 【概念】設定與程式分離後，不同環境可替換服務位置而無須重新編譯 Gateway。
 * 【邊界】僅保存位置資訊，不驗證服務可用性，也不負責發送 HTTP 請求。
 */
@Data
@ConfigurationProperties(prefix = "trading.services")
public class ServiceUrlsProperties {
    private String loopUrl = "http://localhost:8081";
    private String orderUrl = "http://localhost:8082";
}
