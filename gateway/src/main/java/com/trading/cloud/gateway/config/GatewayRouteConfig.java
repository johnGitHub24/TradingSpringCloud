package com.trading.cloud.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

/**
 * 【職責】定義 Spring Cloud Gateway MVC 的 loop-service 與 order-service 代理路由。
 * 【技巧】以函數式 {@code RouterFunction}、路徑述詞與 {@code setPath} Filter 組合轉發規則。
 * 【概念】Gateway 將外部代理 URI 轉換為下游 API URI，讓客戶端不需直接知道每個服務的部署位置。
 * 【邊界】不負責資料聚合、授權、重試或下游服務的業務處理。
 */
@Slf4j
@Configuration
public class GatewayRouteConfig {

  private final ServiceUrlsProperties serviceUrls;

  /** 注入代理路由解析下游位置所需的設定。 */
  public GatewayRouteConfig(ServiceUrlsProperties serviceUrls) {
    this.serviceUrls = serviceUrls;
  }

  /**
   * 【職責】建立將 {@code /proxy/loop/{segment}} 轉發至 loop-service 的路由。
   * 【技巧】以路徑變數搭配 {@code setPath} 轉寫為下游 {@code /api/v1/{segment}}，並以 Filter 記錄請求。
   * 【概念】Filter 在轉發前改寫請求，可將公開 API 與下游實際 URI 解耦。
   * 【邊界】只代理單一路徑片段，不處理多層路徑或回應內容轉換。
   * @return loop-service 的 Gateway MVC 路由
   */
  @Bean
  public RouterFunction<ServerResponse> loopServiceRoute() {
    return route("loop_service")
        .route(path("/proxy/loop/{segment}"), http(serviceUrls.getLoopUrl()))
        .filter(setPath("/api/v1/{segment}"))
        .filter((request, next) -> {
          log.info("[Gateway] loop proxy {} {}", request.method(), request.uri().getPath());
          return next.handle(request);
        })
        .build();
  }

  /**
   * 【職責】建立將 {@code /proxy/orders/{segment}} 轉發至 order-service 的路由。
   * 【技巧】以 {@code setPath} 保留路徑片段並補上 order-service 的 API 前綴，轉發前寫入存取日誌。
   * 【概念】函數式路由將比對、目標位址與 Filter 宣告在同一處，便於檢視代理契約。
   * 【邊界】不驗證訂單是否存在；該語意由下游服務回應。
   * @return order-service 的 Gateway MVC 路由
   */
  @Bean
  public RouterFunction<ServerResponse> orderServiceRoute() {
    return route("order_service")
        .route(path("/proxy/orders/{segment}"), http(serviceUrls.getOrderUrl()))
        .filter(setPath("/api/v1/orders/{segment}"))
        .filter((request, next) -> {
          log.info("[Gateway] order proxy {} {}", request.method(), request.uri().getPath());
          return next.handle(request);
        })
        .build();
  }
}
