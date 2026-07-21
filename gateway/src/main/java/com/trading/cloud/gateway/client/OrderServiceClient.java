package com.trading.cloud.gateway.client;

import com.trading.cloud.common.dto.OrderSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 【職責】宣告 Gateway 呼叫 order-service 查詢 API 的型別安全 Feign 契約。
 * 【技巧】{@code @FeignClient} 依 Spring MVC 註解產生 HTTP 用戶端，URL 由
 * {@code trading.services.order-url} 外部化設定。
 * 【概念】介面方法看似本機呼叫，Feign 會在執行期將其轉為遠端 HTTP 請求，減少手寫序列化與路由程式。
 * 【邊界】不實作重試、熔斷、快取或訂單商業規則。
 */
@FeignClient(name = "order-service", url = "${trading.services.order-url}")
public interface OrderServiceClient {

    /**
     * 【職責】取得 order-service 目前可查詢的訂單摘要。
     * 【技巧】以 {@code @GetMapping} 讓 Feign 對齊下游 Controller 的 HTTP 契約。
     * 【概念】回傳集合由 Feign 將 JSON 陣列解碼為共用 DTO，呼叫端不需自行解析本文。
     * @return 下游回傳的訂單摘要清單
     */
    @GetMapping("/api/v1/orders")
    List<OrderSummaryResponse> listOrders();

    /**
     * 【職責】依訂單識別碼向 order-service 取得單筆摘要。
     * 【技巧】{@code @PathVariable} 將方法引數安全地代入 URI 範本。
     * 【概念】以宣告式 URI 範本取代字串拼接，可使用戶端與服務端的路徑契約清楚對應。
     * @param orderId 要查詢的訂單識別碼
     * @return 下游回傳的訂單摘要
     */
    @GetMapping("/api/v1/orders/{orderId}")
    OrderSummaryResponse getOrder(@PathVariable("orderId") Long orderId);
}
