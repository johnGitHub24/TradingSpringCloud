# Trading Spring Cloud

**Spring Cloud 分散式入門練習** — Gateway MVC 路由 + OpenFeign 服務聚合。  
學習路線第 ③ 站，介於 Trading System MVP 與 APIGatewayMQ 之間。

## 文件入口

單一入口：本 README。衝突以主規格為準。

| 文件 | 說明 |
|------|------|
| [TradingSpringCloud 規格書.md](TradingSpringCloud%20規格書.md) | **主規格（權威）** |
| [API規格書.md](API規格書.md) | API 契約 |
| [docs/architecture.md](docs/architecture.md) | 分層與模組 |
| [docs/codeGraphic.html](docs/codeGraphic.html) | 架構圖（非權威） |
| [docs/testing.md](docs/testing.md) | 測試／Case／check |
| [docs/資料庫設計.md](docs/資料庫設計.md) | 資料庫 |
| [docs/驗證設計.md](docs/驗證設計.md) | 驗證／權限 |
| [CLAUDE.md](CLAUDE.md) | AI 薄規則 |
| [scripts/README.md](scripts/README.md) | 驗證／啟動腳本 |

## 模組

| 模組 | Port | 職責 |
|------|------|------|
| `gateway` | 8080 | Gateway MVC + Feign Dashboard + Vue |
| `loop-service` | 8081 | 閉環信任分（延伸 TransactionClosedStateMachine） |
| `order-service` | 8082 | 訂單查詢（延伸 MVP） |
| `common` | — | 共用 DTO |

## 快速開始

驗證（JDK 21；可先 `. .\scripts\env.ps1`）：

```powershell
.\scripts\check.ps1
```

啟動（IntelliJ Gradle 建議 `:order-service:bootRun`；另開 loop／gateway）：

```powershell
.\gradlew.bat :order-service:bootRun   # 8082（IntelliJ 預設）
.\gradlew.bat :loop-service:bootRun    # 8081
.\gradlew.bat :gateway:bootRun         # 8080
```

| 用途 | URL |
|------|-----|
| 教學 | `docs/codeGraphic.html` 或 http://localhost:8080/guide.html |
| Vue 控制台 | http://localhost:8080/ |
| Dashboard API | http://localhost:8080/api/v1/dashboard |

## 測試

```powershell
.\scripts\check.ps1          # = gradlew check（unit + integration）
```

## 五專案路線

```text
TransactionClosedStateMachine → Trading System MVP → TradingSpringCloud → APIGatewayMQ → TradingKubernetes
```

> Docs standard: EngineeringOS eos-minimal @ 0.1.10 — `knowledge/documentation.md`

