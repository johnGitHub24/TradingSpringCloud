# 測試與 CI — TradingSpringCloud

> 衝突以 [TradingSpringCloud 規格書.md](../TradingSpringCloud%20規格書.md) 為準。  
> 規範：EngineeringOS `knowledge/testing.md` @ 0.1.4

## 測試分層

| 層級 | 註記 | Gradle 任務 | 依賴 |
|------|------|-------------|------|
| 單元 | `*ServiceTest`、`GatewayRouteConfigTest` | `gradlew :module:test` | 無 |
| 整合／切片 | `@WebMvcTest`／`@SpringBootTest` + WireMock | 同上 | WireMock（gateway） |
| 全專案 | 驗證入口 | `.\scripts\check.ps1`（`gradlew check`） | 同上 |

## Case ID 對照

| Case ID | 單元 | 整合／切片 |
|---------|------|------------|
| CLOUD-001 | `DashboardServiceTest` | `DashboardIntegrationTest` |
| CLOUD-002 | `GatewayRouteConfigTest` | `DashboardIntegrationTest`（`/gateway/routes`） |
| CLOUD-003 | `GatewayRouteConfigTest` | `GatewayRouteIntegrationTest` |
| CLOUD-004 | `GatewayRouteConfigTest` | `GatewayRouteIntegrationTest` |
| CLOUD-LOOP-001 | `TrustServiceTest` | `TrustControllerTest` |
| CLOUD-LOOP-002 | `TrustServiceTest` | `TrustControllerTest` |
| CLOUD-ORDER-001 | `OrderQueryServiceTest` | `OrderQueryControllerTest` |
| CLOUD-ORDER-002 | `OrderQueryServiceTest` | `OrderQueryControllerTest` |

## 執行

```powershell
.\scripts\check.ps1
```

## 備註：Gateway MVC 代理測試

Gateway MVC 底層 JDK HttpClient 預設嘗試 HTTP/2 (h2c upgrade)，與 WireMock/Jetty
協商時會觸發 `RST_STREAM` / `EOFException`。已於 `GatewayAppConfig` 提供
`ClientHttpRequestFactory` Bean 強制 HTTP/1.1 解決，CLOUD-003/004 因此可穩定自動化。

## CI

`.github/workflows/ci.yml`：push / PR 觸發 `gradlew check`。
