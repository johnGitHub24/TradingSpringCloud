# Testing and Verification — TradingSpringCloud

> 衝突以 [TradingSpringCloud 規格書.md](../TradingSpringCloud%20規格書.md) 為準。  
> Case ID 詳見 [測試與CI.md](測試與CI.md)。  
> 規範：EngineeringOS `knowledge/testing.md` @ 0.1.4

## Check command

```powershell
.\scripts\check.ps1
```

等同 `.\gradlew.bat check`（需 JDK 21；可先 `. .\scripts\env.ps1`）。與 CI 同一入口。

## Test layers

| Layer | Location | 說明 |
|-------|----------|------|
| 單元／切片 | `loop-service`／`order-service` `@WebMvcTest` | CLOUD-LOOP-001、CLOUD-ORDER-001 |
| 整合 | `gateway` `@SpringBootTest` + WireMock | Feign 聚合、代理轉發（HTTP/1.1） |
| 全專案 | `gradlew test`／`check` | 整合 4 + 單元 2 ≈ 6 |

## Minimum case types

| Type | Coverage |
|------|----------|
| Happy Path | CLOUD-001 Dashboard；CLOUD-003／004 代理；Trust／Order 單元 |
| Error Path | order-service 404 ProblemDetail（規格／Controller 測試） |

## Key classes

| Test | Case |
|------|------|
| `DashboardIntegrationTest` | CLOUD-001 |
| `GatewayRouteConfigTest` | CLOUD-002 |
| `GatewayRouteIntegrationTest` | CLOUD-003／004 |
| `TrustControllerTest` | CLOUD-LOOP-001 |
| `OrderQueryControllerTest` | CLOUD-ORDER-001 |

## DoD

- [ ] Unit tests green
- [ ] Gateway 整合（WireMock）green
- [ ] Check command matches CI
- [ ] Dashboard 聚合 + 至少一條代理路徑有覆蓋

詳見 [測試與CI.md](測試與CI.md)。
