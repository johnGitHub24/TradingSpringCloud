package com.trading.cloud.gateway.api;

import com.trading.cloud.common.dto.DashboardResponse;
import com.trading.cloud.common.dto.OrderSummaryResponse;
import com.trading.cloud.common.dto.TrustResponse;
import com.trading.cloud.gateway.application.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 【職責】提供 Gateway 儀表板聚合與代理路徑說明的對外 REST API。
 * 【技巧】以 Spring MVC 的 {@code @RestController} 與 {@code @GetMapping} 將回傳 DTO 自動序列化為 JSON。
 * 【概念】薄 Controller 只負責 HTTP 邊界與委派；跨服務資料組裝集中在 Service，能讓 API 層容易測試與維護。
 * 【邊界】不直接呼叫 Feign、不執行聚合規則，也不實作路由轉發。
 */
@RestController
@RequestMapping("/api/v1")
public class DashboardController {

    private final DashboardService dashboardService;

    /** 注入處理跨服務儀表板聚合的應用服務。 */
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * 【職責】回傳 loop-service 信任分數與 order-service 訂單摘要組成的儀表板。
     * 【技巧】控制器將 HTTP GET 無參數請求委派給 {@link DashboardService}，由 Spring 序列化回應。
     * 【概念】Controller 不知道下游服務細節，可避免 HTTP 邊界與跨服務協調邏輯相互耦合。
     * @return 統一的儀表板回應
     */
    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return dashboardService.build();
    }

    /**
     * 【職責】提供 Gateway 對外代理路徑與下游路徑的對照資訊。
     * 【技巧】使用不可變 {@link Map#of(Object, Object, Object, Object, Object, Object)} 建構固定說明資料。
     * 【概念】將路徑對照暴露為簡易端點，可協助學習與除錯，但不會取代實際 RouterFunction 的執行規則。
     * 【邊界】僅提供說明文字，不偵測路由是否可達。
     * @return 代理路徑對照表
     */
    @GetMapping("/gateway/routes")
    public Map<String, String> routes() {
        return Map.of(
                "loopProxy", "/proxy/loop/{resource} → loop-service /api/v1/{resource}",
                "orderProxy", "/proxy/orders/{id} → order-service /api/v1/orders/{id}",
                "dashboard", "/api/v1/dashboard → OpenFeign 聚合"
        );
    }
}
