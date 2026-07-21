package com.trading.cloud.order.application;

import com.trading.cloud.common.dto.OrderSummaryResponse;
import com.trading.cloud.order.domain.OrderNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 【職責】以記憶體 Map 提供示範訂單查詢（啟動時種子資料）。
 * 【技巧】{@link ConcurrentHashMap} 存放摘要；查無資料拋 {@link OrderNotFoundException}。
 * 【概念】教學用 in-memory 服務可讓 Gateway／Feign 不必依賴真實 DB，仍能練習跨服務契約。
 * 【邊界】不負責真實撮合、持久化或分頁。
 */
@Service
public class OrderQueryService {

    private final Map<Long, OrderSummaryResponse> orders = new ConcurrentHashMap<>();

    /** 載入示範訂單種子資料。 */
    public OrderQueryService() {
        seed(1001L, "BTCUSDT", "FILLED");
        seed(1002L, "ETHUSDT", "NEW");
    }

    private void seed(Long id, String symbol, String status) {
        OrderSummaryResponse order = new OrderSummaryResponse();
        order.setOrderId(id);
        order.setSymbol(symbol);
        order.setStatus(status);
        order.setService("order-service");
        orders.put(id, order);
    }

    /**
     * 【職責】依 ID 查詢訂單摘要。
     * 【技巧】Map 查找；null 時拋領域例外供全域 Handler 轉 HTTP 404。
     * 【概念】用例外表達「資源不存在」，比回傳 Optional 再在 Controller 分支更利於統一錯誤回應。
     * @param orderId 訂單主鍵
     * @return 訂單摘要
     * @throws OrderNotFoundException 不存在時
     */
    public OrderSummaryResponse getById(Long orderId) {
        OrderSummaryResponse order = orders.get(orderId);
        if (order == null) {
            throw new OrderNotFoundException("Order not found: " + orderId);
        }
        return order;
    }

    /**
     * 【職責】回傳目前全部訂單的不可變清單。
     * 【技巧】{@link List#copyOf} 避免呼叫端修改內部 Map 內容。
     * 【概念】對外暴露防禦性複本，可保護服務內部狀態不被意外變更。
     * @return 不可變訂單摘要清單
     */
    public List<OrderSummaryResponse> listAll() {
        return List.copyOf(orders.values());
    }
}
