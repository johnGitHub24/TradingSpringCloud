package com.trading.cloud.gateway.client;

import com.trading.cloud.common.dto.TrustResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 【職責】宣告 Gateway 呼叫 loop-service 信任分數 API 的 Feign 契約。
 * 【技巧】{@code @FeignClient} 以設定值 {@code trading.services.loop-url} 建立宣告式 HTTP 用戶端。
 * 【概念】Feign 將 Java 介面代理為遠端呼叫，讓聚合服務可用明確的型別而非手寫 HTTP 程式交換資料。
 * 【邊界】不處理重試、熔斷、快取或信任分數的商業計算。
 */
@FeignClient(name = "loop-service", url = "${trading.services.loop-url}")
public interface LoopServiceClient {

    /**
     * 【職責】讀取 loop-service 目前公布的系統信任分數。
     * 【技巧】使用 {@code @GetMapping} 宣告與下游 Controller 相同的唯讀 HTTP 路徑。
     * 【概念】方法回傳 DTO 而非 HTTP 本文，將傳輸格式的解碼工作交給 Feign。
     * @return 包含信任分數與服務來源的回應
     */
    @GetMapping("/api/v1/trust")
    TrustResponse getTrust();
}
