package com.trading.cloud.gateway.api;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 【職責】驗證 Gateway MVC 代理路徑會正確轉發至 loop／order 下游。
 * 【技巧】RANDOM_PORT + {@link TestRestTemplate}；WireMock 驗證實際被呼叫的下游 URI。
 * 【概念】代理測試關注「路徑改寫與轉發」，與 Feign 聚合是兩條不同的 Gateway 教學路徑。
 * 【技巧驗證】CASE CLOUD-003、CASE CLOUD-004：/proxy/loop、/proxy/orders 轉發與下游請求次數。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GatewayRouteIntegrationTest {

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
    private TestRestTemplate restTemplate;

    @BeforeEach
    void stub() {
        loopMock.resetAll();
        orderMock.resetAll();
        String trustBody = "{\"systemTrust\":7,\"service\":\"loop-service\"}";
        loopMock.stubFor(get(urlEqualTo("/api/v1/trust"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Content-Length", String.valueOf(trustBody.getBytes().length))
                        .withBody(trustBody)));
        String orderBody = "{\"orderId\":1001,\"symbol\":\"BTCUSDT\",\"status\":\"FILLED\",\"service\":\"order-service\"}";
        orderMock.stubFor(get(urlEqualTo("/api/v1/orders/1001"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Content-Length", String.valueOf(orderBody.getBytes().length))
                        .withBody(orderBody)));
    }

    /**
     * CASE CLOUD-003：loop 代理轉發。
     * Given: WireMock stub /api/v1/trust；When: GET /proxy/loop/trust；Then: 200 且下游被呼叫一次。
     */
    @Test
    void CLOUD_003_loopProxyForwardsToLoopService() {
        ResponseEntity<String> response = restTemplate.getForEntity("/proxy/loop/trust", String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).contains("\"systemTrust\":7");
        loopMock.verify(1, com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor(urlEqualTo("/api/v1/trust")));
    }

    /**
     * CASE CLOUD-003：下游 404 經 loop 代理原樣轉回。
     * Given: WireMock /api/v1/missing 回 404；When: GET /proxy/loop/missing；Then: 404。
     */
    @Test
    void CLOUD_003_loopProxyForwardsDownstreamNotFound() {
        String body = "{\"status\":404,\"title\":\"Not Found\"}";
        loopMock.stubFor(get(urlEqualTo("/api/v1/missing"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Content-Length", String.valueOf(body.getBytes().length))
                        .withBody(body)));

        ResponseEntity<String> response = restTemplate.getForEntity("/proxy/loop/missing", String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    /**
     * CASE CLOUD-004：order 代理轉發。
     * Given: WireMock stub /api/v1/orders/1001；When: GET /proxy/orders/1001；Then: 200 且下游被呼叫一次。
     */
    @Test
    void CLOUD_004_orderProxyForwardsToOrderService() {
        ResponseEntity<String> response = restTemplate.getForEntity("/proxy/orders/1001", String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).contains("\"status\":\"FILLED\"");
        orderMock.verify(1, com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor(urlEqualTo("/api/v1/orders/1001")));
    }

    /**
     * CASE CLOUD-004：下游 404 經代理原樣轉回。
     * Given: WireMock /api/v1/orders/9999 回 404；When: GET /proxy/orders/9999；Then: 404。
     */
    @Test
    void CLOUD_004_orderProxyForwardsDownstreamNotFound() {
        String problem = "{\"title\":\"Order Not Found\",\"errorCode\":\"ORDER_NOT_FOUND\"}";
        orderMock.stubFor(get(urlEqualTo("/api/v1/orders/9999"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Content-Length", String.valueOf(problem.getBytes().length))
                        .withBody(problem)));

        ResponseEntity<String> response = restTemplate.getForEntity("/proxy/orders/9999", String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).contains("ORDER_NOT_FOUND");
    }
}
