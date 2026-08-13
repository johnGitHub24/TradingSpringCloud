package com.trading.cloud.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 【職責】確認 Gateway 路由 Bean 已註冊進 Spring 容器。
 * 【技巧】{@code @SpringBootTest} 注入兩個 {@link RouterFunction} Bean。
 * 【概念】先驗證組態是否裝載，再做 HTTP 轉發整合測試，可縮小失敗時的排查範圍。
 */
@SpringBootTest
@ActiveProfiles("test")
class GatewayRouteConfigTest {

    @Autowired
    private RouterFunction<ServerResponse> loopServiceRoute;

    @Autowired
    private RouterFunction<ServerResponse> orderServiceRoute;

    /**
     * CASE CLOUD-002：路由 Bean 註冊。
     * Given: test profile 啟動；When: 注入 loop／order RouterFunction；Then: 皆非 null。
     */
    @Test
    void CLOUD_002_gatewayRouteBeansRegistered() {
        assertThat(loopServiceRoute).isNotNull();
        assertThat(orderServiceRoute).isNotNull();
    }

    /**
     * CASE CLOUD-003：loop 代理路徑改寫契約。
     * Given: 公開／下游範本常數；When: 代入 segment=trust；Then: 等於整合測試實際轉發 URI。
     */
    @Test
    void CLOUD_003_loopProxyRewritesSegmentToLoopApi() {
        assertThat(GatewayRouteConfig.LOOP_PROXY_PATTERN).isEqualTo("/proxy/loop/{segment}");
        assertThat(GatewayRouteConfig.LOOP_DOWNSTREAM_PATTERN.replace("{segment}", "trust"))
                .isEqualTo("/api/v1/trust");
    }

    /**
     * CASE CLOUD-004：order 代理路徑改寫契約。
     * Given: 公開／下游範本常數；When: 代入 segment=1001；Then: 等於整合測試實際轉發 URI。
     */
    @Test
    void CLOUD_004_orderProxyRewritesSegmentToOrderApi() {
        assertThat(GatewayRouteConfig.ORDER_PROXY_PATTERN).isEqualTo("/proxy/orders/{segment}");
        assertThat(GatewayRouteConfig.ORDER_DOWNSTREAM_PATTERN.replace("{segment}", "1001"))
                .isEqualTo("/api/v1/orders/1001");
    }
}
