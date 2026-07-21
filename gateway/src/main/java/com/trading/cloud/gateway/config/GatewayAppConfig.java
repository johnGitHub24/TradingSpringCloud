package com.trading.cloud.gateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * 【職責】集中 Gateway 的基礎 HTTP 用戶端設定，並啟用下游服務 URL 屬性綁定。
 * 【技巧】以 {@code @EnableConfigurationProperties} 註冊設定類別，並提供自訂
 * {@link ClientHttpRequestFactory} Bean 給 Gateway MVC 轉發流程使用。
 * 【概念】將基礎設施組裝留在 Configuration，可使路由與控制器只描述用途，不耦合 HTTP 實作細節。
 * 【邊界】不定義代理路徑與下游業務規則；正式環境應以環境變數覆寫 {@code trading.services.*}。
 */
@Configuration
@EnableConfigurationProperties(ServiceUrlsProperties.class)
public class GatewayAppConfig {

    /**
     * 【職責】提供固定使用 HTTP/1.1 的 Gateway 轉發用請求工廠。
     * 【技巧】設定 JDK {@link HttpClient} 版本與連線逾時，再包裝為 Spring 的
     * {@link JdkClientHttpRequestFactory}。
     * 【概念】JDK 用戶端預設偏好 HTTP/2；部分 WireMock／Jetty 後端協商 h2c 時可能中斷，
     * 固定 HTTP/1.1 可使此同步代理範例的傳輸行為一致。
     * 【邊界】僅設定連線協定與逾時，不處理重試、驗證或下游錯誤轉譯。
     * @return 供 Gateway MVC 使用的 HTTP 請求工廠
     */
    @Bean
    public ClientHttpRequestFactory gatewayClientHttpRequestFactory() {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        return new JdkClientHttpRequestFactory(httpClient);
    }
}
