package com.trading.cloud.gateway.application;

import com.trading.cloud.common.dto.DashboardResponse;
import com.trading.cloud.common.dto.OrderSummaryResponse;
import com.trading.cloud.common.dto.TrustResponse;
import com.trading.cloud.gateway.client.LoopServiceClient;
import com.trading.cloud.gateway.client.OrderServiceClient;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * 【職責】驗證 DashboardService 聚合 loop／order DTO 的純編排邏輯。
 * 【技巧】Mockito 隔離兩個 Feign Client，不啟動 Spring／HTTP。
 * 【概念】單元測「組裝規則」；真實 Feign 往返留給 CASE CLOUD-001 整合測試。
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private LoopServiceClient loopServiceClient;
    @Mock
    private OrderServiceClient orderServiceClient;

    @InjectMocks
    private DashboardService dashboardService;

    /**
     * CASE CLOUD-001：Feign 聚合 systemTrust／orderCount／latestOrderStatus。
     * Given: trust=5 與一筆 FILLED；When: build；Then: 欄位與訊息正確。
     */
    @Test
    void CLOUD_001_buildAggregatesTrustAndLatestOrder() {
        TrustResponse trust = new TrustResponse();
        trust.setSystemTrust(5);
        trust.setService("loop-service");
        OrderSummaryResponse order = new OrderSummaryResponse();
        order.setOrderId(1001L);
        order.setSymbol("BTCUSDT");
        order.setStatus("FILLED");
        when(loopServiceClient.getTrust()).thenReturn(trust);
        when(orderServiceClient.listOrders()).thenReturn(List.of(order));

        DashboardResponse dashboard = dashboardService.build();

        assertThat(dashboard.getSystemTrust()).isEqualTo(5);
        assertThat(dashboard.getOrderCount()).isEqualTo(1);
        assertThat(dashboard.getLatestOrderStatus()).isEqualTo("FILLED");
        assertThat(dashboard.getMessage()).contains("Feign aggregated");
    }

    /**
     * CASE CLOUD-001：空訂單清單時最新狀態為 N/A。
     * Given: 空 list；When: build；Then: orderCount=0、latestOrderStatus=N/A。
     */
    @Test
    void CLOUD_001_buildEmptyOrdersUsesNaStatus() {
        TrustResponse trust = new TrustResponse();
        trust.setSystemTrust(0);
        when(loopServiceClient.getTrust()).thenReturn(trust);
        when(orderServiceClient.listOrders()).thenReturn(List.of());

        DashboardResponse dashboard = dashboardService.build();

        assertThat(dashboard.getOrderCount()).isZero();
        assertThat(dashboard.getLatestOrderStatus()).isEqualTo("N/A");
    }

    /**
     * CASE CLOUD-001：下游 Feign 失敗向上拋，不組裝部分結果。
     * Given: getTrust 拋 RuntimeException；When: build；Then: 同例外。
     */
    @Test
    void CLOUD_001_buildPropagatesDownstreamFailure() {
        when(loopServiceClient.getTrust()).thenThrow(new IllegalStateException("loop down"));

        assertThatThrownBy(() -> dashboardService.build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("loop down");
    }
}
