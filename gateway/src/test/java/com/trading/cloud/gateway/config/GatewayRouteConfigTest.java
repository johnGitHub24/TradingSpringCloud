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
}
