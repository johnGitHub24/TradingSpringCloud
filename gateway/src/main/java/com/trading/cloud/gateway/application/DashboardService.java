package com.trading.cloud.gateway.application;

import com.trading.cloud.common.dto.DashboardResponse;
import com.trading.cloud.common.dto.OrderSummaryResponse;
import com.trading.cloud.common.dto.TrustResponse;
import com.trading.cloud.gateway.client.LoopServiceClient;
import com.trading.cloud.gateway.client.OrderServiceClient;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 【職責】協調 loop-service 與 order-service 的讀取結果，建立 Gateway 儀表板回應。
 * 【技巧】透過兩個宣告式 Feign Client 取得共用 DTO，再於應用服務層組裝新的 {@link DashboardResponse}。
 * 【概念】聚合服務是跨服務協調的單一位置，避免 Controller 同時知道多個下游契約與資料轉換細節。
 * 【邊界】不處理 HTTP 路由、下游狀態機、失敗重試或持久化。
 */
@Service
public class DashboardService {

    private final LoopServiceClient loopServiceClient;
    private final OrderServiceClient orderServiceClient;

    /** 注入讀取兩個下游服務資料的 Feign 用戶端。 */
    public DashboardService(LoopServiceClient loopServiceClient, OrderServiceClient orderServiceClient) {
        this.loopServiceClient = loopServiceClient;
        this.orderServiceClient = orderServiceClient;
    }

    /**
     * 【職責】取得信任分數與訂單清單，並計算儀表板的訂單數量及最後一筆狀態。
     * 【技巧】先呼叫 Feign Client 取得型別化資料，再以空清單條件運算子安全設定最新狀態。
     * 【概念】聚合物件與下游 DTO 分開建立，避免將任一服務的傳輸模型直接當成 Gateway 對外模型。
     * 【邊界】假設下游回傳順序可代表「最後一筆」；不自行排序或容錯下游連線失敗。
     * @return 包含下游摘要的儀表板回應
     */
    public DashboardResponse build() {
        TrustResponse trust = loopServiceClient.getTrust();
        List<OrderSummaryResponse> orders = orderServiceClient.listOrders();

        DashboardResponse dashboard = new DashboardResponse();
        dashboard.setSystemTrust(trust.getSystemTrust());
        dashboard.setOrderCount(orders.size());
        dashboard.setLatestOrderStatus(orders.isEmpty() ? "N/A" : orders.get(orders.size() - 1).getStatus());
        dashboard.setMessage("Feign aggregated loop-service + order-service");
        return dashboard;
    }
}
