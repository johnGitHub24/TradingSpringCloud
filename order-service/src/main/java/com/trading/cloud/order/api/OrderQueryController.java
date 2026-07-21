package com.trading.cloud.order.api;

import com.trading.cloud.common.dto.OrderSummaryResponse;
import com.trading.cloud.order.application.OrderQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 【職責】提供 order-service 訂單查詢的對外 REST API。
 * 【技巧】薄 {@code @RestController}：收路徑參數後委派 {@link OrderQueryService}，由 Spring 序列化 JSON。
 * 【概念】Controller 只做 HTTP 邊界；種子資料與「找不到」語意留在 Service，避免 Web 層與業務規則耦合。
 * 【邊界】不負責持久化、撮合或 Gateway 聚合；基底路徑 {@code /api/v1/orders}。
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderQueryController {

    private final OrderQueryService orderQueryService;

    /** 建構子注入 {@link OrderQueryService}。 */
    public OrderQueryController(OrderQueryService orderQueryService) {
        this.orderQueryService = orderQueryService;
    }

    /**
     * 【職責】依訂單 ID 回傳單筆摘要。
     * 【技巧】{@code @PathVariable} 綁定 URI；不存在時由全域例外處理轉成 404。
     * 【概念】查詢失敗用領域例外上拋，比在 Controller 手寫 if/404 更易統一錯誤格式。
     * @param orderId 訂單識別碼
     * @return 訂單摘要 DTO
     */
    @GetMapping("/{orderId}")
    public OrderSummaryResponse getOrder(@PathVariable Long orderId) {
        return orderQueryService.getById(orderId);
    }

    /**
     * 【職責】列出目前記憶體中的全部示範訂單。
     * 【技巧】無參數 {@code @GetMapping} 對應集合資源。
     * 【概念】列表端點與單筆端點分開，讓 Feign／Gateway 可依用途選擇契約。
     * @return 訂單摘要清單
     */
    @GetMapping
    public List<OrderSummaryResponse> listOrders() {
        return orderQueryService.listAll();
    }
}
