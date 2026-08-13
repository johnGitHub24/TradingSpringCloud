package com.trading.cloud.order.api;

import com.trading.cloud.common.dto.OrderSummaryResponse;
import com.trading.cloud.order.application.OrderQueryService;
import com.trading.cloud.order.config.GlobalExceptionHandler;
import com.trading.cloud.order.domain.OrderNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 【職責】切片測試 OrderQueryController 查單契約。
 * 【技巧】{@code @WebMvcTest} + Mock Service；匯入 {@link GlobalExceptionHandler} 以涵蓋錯誤路徑組態。
 * 【概念】Controller 測試用 stub 回傳 DTO，驗證 HTTP 狀態與 JSON 欄位即可。
 */
@WebMvcTest(controllers = OrderQueryController.class)
@Import(GlobalExceptionHandler.class)
class OrderQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderQueryService orderQueryService;

    /**
     * CASE CLOUD-ORDER-001：依 ID 查單。
     * Given: Service 回傳 FILLED 訂單；When: GET /api/v1/orders/1001；Then: 200 且 status=FILLED。
     */
    @Test
    void CLOUD_ORDER_001_returnsOrder() throws Exception {
        OrderSummaryResponse order = new OrderSummaryResponse();
        order.setOrderId(1001L);
        order.setSymbol("BTCUSDT");
        order.setStatus("FILLED");
        when(orderQueryService.getById(1001L)).thenReturn(order);

        mockMvc.perform(get("/api/v1/orders/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FILLED"));
    }

    /**
     * CASE CLOUD-ORDER-001：查無訂單轉 RFC 7807 404。
     * Given: Service 拋 OrderNotFoundException；When: GET /api/v1/orders/9999；Then: 404 且 errorCode。
     */
    @Test
    void CLOUD_ORDER_001_missingOrderReturnsProblemDetail() throws Exception {
        when(orderQueryService.getById(9999L))
                .thenThrow(new OrderNotFoundException("Order not found: 9999"));

        mockMvc.perform(get("/api/v1/orders/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Order Not Found"))
                .andExpect(jsonPath("$.errorCode").value("ORDER_NOT_FOUND"))
                .andExpect(jsonPath("$.detail").value("Order not found: 9999"));
    }

    /**
     * CASE CLOUD-ORDER-002：列出全部訂單。
     * Given: Service 回傳一筆摘要；When: GET /api/v1/orders；Then: 200 且陣列含 orderId。
     */
    @Test
    void CLOUD_ORDER_002_listsOrders() throws Exception {
        OrderSummaryResponse order = new OrderSummaryResponse();
        order.setOrderId(1001L);
        order.setSymbol("BTCUSDT");
        order.setStatus("FILLED");
        when(orderQueryService.listAll()).thenReturn(List.of(order));

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(1001))
                .andExpect(jsonPath("$[0].status").value("FILLED"));
    }
}
