package com.trading.cloud.gateway.api;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 【職責】驗證 Gateway 儀表板經 Feign 聚合 loop／order 下游的整合行為。
 * 【技巧】{@code @SpringBootTest} + MockMvc；WireMock 動態埠搭配 {@code @DynamicPropertySource}。
 * 【概念】整合測試用假下游隔離真實服務，專注驗證聚合契約與 JSON 欄位。
 * 【技巧驗證】CLOUD-001：Feign 聚合 systemTrust／orderCount／latestOrderStatus。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardIntegrationTest {

    static WireMockServer loopMock = new WireMockServer(0);
    static WireMockServer orderMock = new WireMockServer(0);

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        loopMock.start();
        orderMock.start();
        registry.add("trading.services.loop-url", () -> "http://localhost:" + loopMock.port());
        registry.add("trading.services.order-url", () -> "http://localhost:" + orderMock.port());
    }

    @AfterAll
    static void shutdown() {
        loopMock.stop();
        orderMock.stop();
    }

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void stub() {
        loopMock.resetAll();
        orderMock.resetAll();
        loopMock.stubFor(get(urlEqualTo("/api/v1/trust"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"systemTrust\":5,\"service\":\"loop-service\"}")));
        orderMock.stubFor(get(urlEqualTo("/api/v1/orders"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"orderId\":1001,\"symbol\":\"BTCUSDT\",\"status\":\"FILLED\",\"service\":\"order-service\"}]")));
    }

    /**
     * CASE CLOUD-001：儀表板 Feign 聚合。
     * Given: WireMock 回傳 trust=5 與一筆 FILLED 訂單；When: GET /api/v1/dashboard；Then: 200 且欄位正確。
     */
    @Test
    void CLOUD_001_dashboardAggregatesViaFeign() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.systemTrust").value(5))
                .andExpect(jsonPath("$.orderCount").value(1))
                .andExpect(jsonPath("$.latestOrderStatus").value("FILLED"));
    }
}
