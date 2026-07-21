# 測試與 CI — TradingSpringCloud

> 衝突以 [TradingSpringCloud 規格書.md](../TradingSpringCloud%20規格書.md) 為準。  
> 規範：EngineeringOS `knowledge/testing.md` @ 0.1.4

## 測試分層

| 層級 | 註記 | Gradle 任務 | 依賴 |
|------|------|-------------|------|
| 單元/切片 | `@WebMvcTest`（mock service） | `gradlew :module:test` | 無 |
| 整合 | `@SpringBootTest` + WireMock | `gradlew :gateway:test` | WireMock |
| 全專案 | — | `gradlew test`／`scripts/check.ps1` | 同上 |

## Case ID 對照

| Case ID | 類型 | 模組 | 測試類別 |
|---------|------|------|----------|
| CLOUD-001 | 整合 | gateway | DashboardIntegrationTest（Feign 聚合） |
| CLOUD-002 | 整合 | gateway | GatewayRouteConfigTest（路由 Bean 註冊） |
| CLOUD-003 | 整合 | gateway | GatewayRouteIntegrationTest（loop 代理轉發） |
| CLOUD-004 | 整合 | gateway | GatewayRouteIntegrationTest（order 代理轉發） |
| CLOUD-LOOP-001 | 單元 | loop-service | TrustControllerTest |
| CLOUD-ORDER-001 | 單元 | order-service | OrderQueryControllerTest |

**統計：** 6 個測試（整合 4 + 單元 2）。

## 執行

```powershell
.\gradlew.bat clean test --console=plain
.\scripts\check.ps1
```

## 備註：Gateway MVC 代理測試

Gateway MVC 底層 JDK HttpClient 預設嘗試 HTTP/2 (h2c upgrade)，與 WireMock/Jetty
協商時會觸發 `RST_STREAM` / `EOFException`。已於 `GatewayAppConfig` 提供
`ClientHttpRequestFactory` Bean 強制 HTTP/1.1 解決，CLOUD-003/004 因此可穩定自動化。

## CI

`.github/workflows/ci.yml`：push / PR 觸發 `gradlew test`。
