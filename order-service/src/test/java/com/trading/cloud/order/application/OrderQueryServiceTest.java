package com.trading.cloud.order.application;

import com.trading.cloud.common.dto.OrderSummaryResponse;
import com.trading.cloud.order.domain.OrderNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 【職責】驗證 OrderQueryService 種子查詢與找不到訂單的領域契約。
 * 【技巧】直接 new Service；以 AssertJ 檢查例外與防禦性複本。
 * 【概念】單元測記憶體 Map 語意；HTTP 404 轉譯留給 Controller／Handler。
 */
@Tag("unit")
class OrderQueryServiceTest {

    private OrderQueryService orderQueryService;

    @BeforeEach
    void setUp() {
        orderQueryService = new OrderQueryService();
    }

    /**
     * CASE CLOUD-ORDER-001：依 ID 查到種子訂單。
     * Given: 建構子種子 1001 FILLED；When: getById(1001)；Then: symbol／status 正確。
     */
    @Test
    void CLOUD_ORDER_001_getByIdReturnsSeededOrder() {
        OrderSummaryResponse order = orderQueryService.getById(1001L);

        assertThat(order.getOrderId()).isEqualTo(1001L);
        assertThat(order.getSymbol()).isEqualTo("BTCUSDT");
        assertThat(order.getStatus()).isEqualTo("FILLED");
        assertThat(order.getService()).isEqualTo("order-service");
    }

    /**
     * CASE CLOUD-ORDER-001：查無資料拋 OrderNotFoundException。
     * Given: 不存在的 9999；When: getById；Then: 例外訊息含訂單 ID。
     */
    @Test
    void CLOUD_ORDER_001_getByIdMissingThrowsNotFound() {
        assertThatThrownBy(() -> orderQueryService.getById(9999L))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("9999");
    }

    /**
     * CASE CLOUD-ORDER-002：列出全部種子且回傳不可變複本。
     * Given: 兩筆種子；When: listAll；Then: 含 1001／1002，且 add 會失敗。
     */
    @Test
    void CLOUD_ORDER_002_listAllReturnsImmutableSeedCopy() {
        List<OrderSummaryResponse> orders = orderQueryService.listAll();

        assertThat(orders).hasSize(2);
        assertThat(orders).extracting(OrderSummaryResponse::getOrderId)
                .containsExactlyInAnyOrder(1001L, 1002L);
        assertThatThrownBy(() -> orders.add(new OrderSummaryResponse()))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
