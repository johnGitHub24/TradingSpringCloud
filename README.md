# Trading Spring Cloud

**Spring Cloud 分散式入門練習** — Gateway MVC 路由 + OpenFeign 服務聚合。  
學習路線第 ③ 站，介於 Trading System MVP 與 APIGatewayMQ 之間。

## 文件入口

| 文件 | 說明 |
|------|------|
| [TradingSpringCloud 規格書.md](TradingSpringCloud%20規格書.md) | **主規格書（權威）** |
| [TradingSpringCloud-SPEC.md](TradingSpringCloud-SPEC.md) | EOS 英文入口／摘要 |
| [API規格書.md](API規格書.md) | API 端點完整參考 |
| [docs/architecture.md](docs/architecture.md) | 分層與模組（可執行摘要） |
| [docs/codeGraphic.html](docs/codeGraphic.html) | Tab 式架構圖（Feign／代理／服務／模組） |
| [docs/testing.md](docs/testing.md) | 驗證入口／DoD |
| [docs/資料庫設計.md](docs/資料庫設計.md) | 無 DB（記憶體） |
| [docs/驗證設計.md](docs/驗證設計.md) | ProblemDetail／錯誤 |
| [docs/測試與CI.md](docs/測試與CI.md) | Case ID、CI |
| [docs/功能流程說明.md](docs/功能流程說明.md) | 每個 API 怎麼跑（Mermaid） |
| [docs/架構學習導引.md](docs/架構學習導引.md) | Spring Cloud 概念學習地圖 |
| [docs/專案引導教學.html](docs/專案引導教學.html) | 互動架構與流程圖 |
| [docs/初學者學習說明書.md](docs/初學者學習說明書.md) | 3 天節奏 |
| [開發專案.md](開發專案.md) | 開發計畫、DoD |
| [CLAUDE.md](CLAUDE.md) | AI／工程薄規則（EOS 0.1.4） |

## 模組

| 模組 | Port | 職責 |
|------|------|------|
| `gateway` | 8080 | Gateway MVC + Feign Dashboard + Vue |
| `loop-service` | 8081 | 閉環信任分（延伸 TransactionClosedStateMachine） |
| `order-service` | 8082 | 訂單查詢（延伸 MVP） |
| `common` | — | 共用 DTO |

## 快速啟動

```powershell
cd "D:\ClaudeCode\TradingSpringCloud"
. .\scripts\env.ps1
.\scripts\start-all.ps1
```

或分別啟動：

```powershell
.\gradlew.bat :loop-service:bootRun    # 8081
.\gradlew.bat :order-service:bootRun   # 8082
.\gradlew.bat :gateway:bootRun         # 8080
```

| 用途 | URL |
|------|-----|
| 教學 | `docs/專案引導教學.html` 或 http://localhost:8080/guide.html |
| Vue 控制台 | http://localhost:8080/ |
| Dashboard API | http://localhost:8080/api/v1/dashboard |

## 測試

```powershell
.\gradlew.bat clean test    # 整合 4 + 單元 2 = 6，全綠
.\scripts\check.ps1
```

## 五專案路線

```text
TransactionClosedStateMachine → Trading System MVP → TradingSpringCloud → APIGatewayMQ → TradingKubernetes
```

> Docs standard: EngineeringOS eos-minimal @ 0.1.4 — `knowledge/documentation.md`
