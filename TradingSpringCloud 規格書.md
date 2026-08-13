# TradingSpringCloud 規格書

> **本文件為 TradingSpringCloud 的唯一主規格書。**  
> 涵蓋：架構、模組拆分、API 契約、Spring Cloud 機制、測試、驗收標準。  
> 開發、測試、面試準備均以本文件為準。

---

## 第 0 章　文件體系與參考來源

### 0.1 本專案文件（權威）

| 文件 | 用途 |
|------|------|
| **`TradingSpringCloud 規格書.md`** | **主規格書（本文件）** |
| `TradingSpringCloud-SPEC.md` | EOS 英文入口／摘要（0.1.4） |
| `開發專案.md` | 開發計畫、能力對照、DoD |
| `API規格書.md` | API 端點完整參考 |
| `docs/architecture.md` | 分層／模組／runtime 摘要 |
| `docs/testing.md` | 驗證入口／DoD 摘要 |
| `docs/資料庫設計.md` | 無 DB（記憶體）說明 |
| `docs/驗證設計.md` | ProblemDetail／錯誤 |
| `docs/testing.md` | Case ID 對照、CI 與腳本 |
| `docs/architecture.md` | Spring Cloud 概念學習地圖（推薦起手式） |
| `docs/codeGraphic.html` | 可點選的互動架構與流程圖 |
| `docs/architecture.md` | 每個 API「做什麼、怎麼跑」（Mermaid） |
| `docs/architecture.md` | 環境、啟動、3 天入門 |

### 0.2 外部參考（非本專案規格）

| 來源 | 採用範圍 | **不採用** |
|------|----------|------------|
| **SpringBootDemo** | Gateway MVC 路由寫法、OpenFeign Client 寫法、`spring-cloud-dependencies:2023.0.0` | 其單體業務邏輯 |
| **TransactionClosedStateMachine** | 文件體系範本、閉環信任分概念（loop-service） | 狀態機細節 |
| **Trading System MVP** | 訂單查詢概念（order-service） | 完整風控/撮合 |
| **APIGatewayMQ 規格書** | **本規格書格式與章節結構** | Kafka / Redis 限流（留給 ④） |

```text
SpringBootDemo               ──實作參考──►  Gateway MVC / OpenFeign
TransactionClosedStateMachine ──文件範本──►  文件體系 + loop-service
APIGatewayMQ 規格書           ──格式規範──►  本文件章節結構
本文件                        ──唯一權威──►  模組 / API / 測試 / 驗收
```

### 0.3 當前成熟度

| Level | 名稱 | 狀態 |
|-------|------|------|
| ⚪ L0 | 規格階段 | ✅ 完成 |
| 🟡 L1 | 能跑 | ✅ **已達成**（三服務可啟動） |
| 🟠 L2 | 流程完整 | ✅ **已達成**（Gateway 代理 + Feign 聚合貫通） |
| 🔴 L3 | 接近 production | ⬜ 未達（未引入註冊中心 / 熔斷 / 監控叢集，屬 ④⑤ 範圍） |

### 0.4 五專案學習路線中的定位

| 順序 | 專案 | 學什麼 | 架構 |
|------|------|--------|------|
| ① | TransactionClosedStateMachine | Spring Boot 單體分層 | 單體 |
| ② | Trading System MVP | 業務、風控、狀態機 | 單體 |
| ③ | **TradingSpringCloud（本專案）** | **Gateway MVC、OpenFeign、多服務協作** | 多服務 |
| ④ | APIGatewayMQ | Kafka 削峰、限流、高併發 | 多模組 + MQ |
| ⑤ | TradingKubernetes | K8s、GitOps 部署 | 雲原生 |

各 repo **獨立**，互不依賴，各自可單獨跑。

### 0.5 核心名詞速查

| 名詞 | 說明 |
|------|------|
| Gateway MVC | Spring Cloud Gateway 的 Servlet（阻塞式）版本，用 `RouterFunction` 定義路由 |
| OpenFeign | 宣告式 HTTP client，用介面 + 註解呼叫遠端服務 |
| 代理路由 | Gateway 收 `/proxy/**` → 改寫路徑 → 轉發到後端服務 |
| 聚合 API | Gateway 用 Feign 併發呼叫多服務，合併成一份回應（Dashboard） |
| 服務拆分 | loop / order 各自獨立 Spring Boot 應用與 port |

---

## 第 1 章　系統範圍

### 1.1 核心功能

| # | 功能 | 說明 |
|---|------|------|
| 1 | 服務拆分 | loop-service、order-service 各自獨立可跑 |
| 2 | Gateway 代理 | `/proxy/loop/**`、`/proxy/orders/**` 路徑改寫後轉發 |
| 3 | OpenFeign 聚合 | Gateway 用 Feign 呼叫兩服務組出 Dashboard |
| 4 | 統一入口 | 前端 Vue / Swagger 只需認識 Gateway :8080 |
| 5 | 錯誤處理 | order-service 以 `ProblemDetail` 回 404 |

### 1.2 不在範圍（刻意排除以降低入門門檻）

- 服務註冊/發現（Eureka / Consul）→ 用固定 URL 設定
- Config Server 集中設定
- 熔斷/重試/限流（Resilience4j）
- 分散式事務（Seata / Saga）
- Kafka、Redis（留給 APIGatewayMQ）
- 資料庫（order-service 用記憶體 Map 模擬）

### 1.3 技術棧

| 層 | 技術 |
|----|------|
| Gateway | Spring Boot 3、Web MVC、`spring-cloud-gateway-server-mvc`、OpenFeign |
| loop-service | Spring Boot 3、Web MVC |
| order-service | Spring Boot 3、Web MVC、記憶體資料 |
| common | 共用 DTO（Lombok） |
| Spring Cloud | 2023.0.x（`spring-cloud-dependencies`） |
| 前端 | Vue 3 ESM（Gateway static） |
| API 文件 | springdoc-openapi（Swagger UI） |
| 建置 | Gradle 多模組 |
| 測試 | JUnit 5、MockMvc、`@WebMvcTest`、`@SpringBootTest`、WireMock |
| CI | GitHub Actions |

### 1.4 啟動方式

```powershell
cd "D:\SouceDemo\RemoteSpringBoot\TradingSpringCloud"
. .\scripts\env.ps1          # 設定 JAVA_HOME

# 驗證
.\scripts\check.ps1          # = gradlew check

# 啟動（各開一個終端／IntelliJ Gradle）
.\gradlew.bat :order-service:bootRun   # 8082
.\gradlew.bat :loop-service:bootRun    # 8081
.\gradlew.bat :gateway:bootRun         # 8080
```

---

## 第 2 章　分層架構

### 2.1 模組結構

```text
trading-spring-cloud/
├── common/                     ← 共用 DTO
│   └── dto/                    ← TrustResponse、OrderSummaryResponse、DashboardResponse
├── loop-service/  (:8081)      ← 閉環信任分（延伸 ①）
│   ├── api/                    ← TrustController
│   └── application/            ← TrustService
├── order-service/ (:8082)      ← 訂單查詢（延伸 ②）
│   ├── api/                    ← OrderQueryController
│   ├── application/            ← OrderQueryService（記憶體）
│   ├── domain/                 ← OrderNotFoundException
│   └── config/                 ← GlobalExceptionHandler（ProblemDetail）
└── gateway/       (:8080)      ← 統一入口
    ├── api/                    ← DashboardController
    ├── application/            ← DashboardService（Feign 聚合）
    ├── client/                 ← LoopServiceClient、OrderServiceClient（OpenFeign）
    └── config/                 ← GatewayRouteConfig（代理路由）
                                   GatewayAppConfig（HTTP/1.1 client）
                                   ServiceUrlsProperties
```

### 2.2 請求路徑

```text
【聚合（OpenFeign）】
Client → Gateway GET /api/v1/dashboard
       → DashboardService
       → LoopServiceClient.getTrust()   → loop-service  GET /api/v1/trust
       → OrderServiceClient.listOrders() → order-service GET /api/v1/orders
       → 合併為 DashboardResponse

【代理（Gateway MVC 路由）】
Client → Gateway GET /proxy/loop/trust
       → GatewayRouteConfig (path 改寫 → /api/v1/trust)
       → http() 轉發 → loop-service :8081

Client → Gateway GET /proxy/orders/1001
       → GatewayRouteConfig (path 改寫 → /api/v1/orders/1001)
       → http() 轉發 → order-service :8082
```

### 2.3 兩種呼叫方式對照（本專案教學核心）

| 面向 | OpenFeign（聚合） | Gateway 代理 |
|------|-------------------|--------------|
| 定義處 | `@FeignClient` 介面 | `RouterFunction` Bean |
| 誰發請求 | Gateway 應用程式碼 | Gateway 框架自動轉發 |
| 回應處理 | 反序列化成 DTO、可加工 | 原樣回傳 client |
| 適用 | 需要合併/加工多服務資料 | 單純轉發、隱藏內部拓撲 |
| 本專案端點 | `/api/v1/dashboard` | `/proxy/**` |

---

## 第 3 章　服務與資料模型

> 本專案無資料庫；order-service 以 `ConcurrentHashMap` 模擬資料源（種子：1001 BTCUSDT/FILLED、1002 ETHUSDT/NEW）。

### 3.1 共用 DTO（common）

| DTO | 欄位 |
|-----|------|
| `TrustResponse` | `systemTrust:int`、`service:String` |
| `OrderSummaryResponse` | `orderId:Long`、`symbol:String`、`status:String`、`service:String` |
| `DashboardResponse` | `systemTrust:int`、`orderCount:int`、`latestOrderStatus:String`、`message:String` |

### 3.2 服務職責

| 服務 | Port | 職責 | 狀態來源 |
|------|------|------|----------|
| loop-service | 8081 | 系統信任分查詢/累加 | 記憶體計數 |
| order-service | 8082 | 訂單查詢（單筆/列表） | 記憶體 Map |
| gateway | 8080 | 代理 + Feign 聚合 + 前端 | 無（轉發/聚合） |

---

## 第 4 章　API 規格

> 完整範例見 **`API規格書.md`**。Gateway Base URL：`http://localhost:8080`。

### 4.1 Gateway 端點

| # | 功能 | Method | 路徑 | 回應 |
|---|------|--------|------|------|
| 1 | Dashboard 聚合（Feign） | GET | `/api/v1/dashboard` | 200 |
| 2 | 代理 loop | GET | `/proxy/loop/{segment}` → loop `/api/v1/{segment}` | 透傳 |
| 3 | 代理 order | GET | `/proxy/orders/{segment}` → order `/api/v1/orders/{segment}` | 透傳 |

**`GET /api/v1/dashboard` 回應 200**

```json
{
  "systemTrust": 5,
  "orderCount": 2,
  "latestOrderStatus": "NEW",
  "message": "Feign aggregated loop-service + order-service"
}
```

### 4.2 loop-service 端點（:8081）

| # | 功能 | Method | 路徑 | 回應 |
|---|------|--------|------|------|
| 1 | 查詢信任分 | GET | `/api/v1/trust` | 200 |
| 2 | 信任分 +1 | POST | `/api/v1/trust/increment` | 200 |
| 3 | 健康 ping | GET | `/api/v1/health/ping` | 200 |

### 4.3 order-service 端點（:8082）

| # | 功能 | Method | 路徑 | 回應 |
|---|------|--------|------|------|
| 1 | 訂單列表 | GET | `/api/v1/orders` | 200 |
| 2 | 單筆訂單 | GET | `/api/v1/orders/{orderId}` | 200 / 404 |

### 4.4 錯誤碼摘要

| HTTP | errorCode | 情境 | 實作 |
|------|-----------|------|------|
| 404 | `ORDER_NOT_FOUND` | 訂單不存在 | `GlobalExceptionHandler` → `ProblemDetail` |
| 500 | — | 後端服務不可用（代理/Feign 失敗） | 預設 |

---

## 第 5 章　Spring Cloud 機制規格

### 5.1 Gateway MVC 路由（`GatewayRouteConfig`）

| Route ID | Predicate | Filter | 目標 |
|----------|-----------|--------|------|
| `loop_service` | `path("/proxy/loop/{segment}")` | `setPath("/api/v1/{segment}")` | `${trading.services.loop-url}` |
| `order_service` | `path("/proxy/orders/{segment}")` | `setPath("/api/v1/orders/{segment}")` | `${trading.services.order-url}` |

- 以 `RouterFunction<ServerResponse>` Bean 宣告，經 `http()` HandlerFunction 轉發。
- 加了 log filter 記錄轉發的 method / path。

### 5.2 OpenFeign Client

| Client | name | url | 方法 |
|--------|------|-----|------|
| `LoopServiceClient` | `loop-service` | `${trading.services.loop-url}` | `getTrust()` |
| `OrderServiceClient` | `order-service` | `${trading.services.order-url}` | `listOrders()`、`getOrder(id)` |

- `GatewayApplication` 標註 `@EnableFeignClients` 啟用。
- 服務位址由 `ServiceUrlsProperties`（prefix `trading.services`）注入，預設 `8081/8082`。

### 5.3 轉發 HTTP client 設定（`GatewayAppConfig`）

| 設定 | 值 | 原因 |
|------|-----|------|
| HTTP 版本 | HTTP/1.1（`JdkClientHttpRequestFactory`） | 避免 JDK HttpClient h2c 與後端協商觸發 `RST_STREAM`/`EOFException` |
| connectTimeout | 5s | 快速失敗 |

---

## 第 6 章　測試規格

### 6.1 測試分層

| 層級 | 註記 | Gradle 任務 | 依賴 | 模組 |
|------|------|-------------|------|------|
| 單元/切片 | `@WebMvcTest`（mock service） | `gradlew :module:test` | 無 | loop / order |
| 整合 | `@SpringBootTest` + WireMock | `gradlew :gateway:test` | WireMock | gateway |
| 全專案 | — | `.\scripts\check.ps1`（`gradlew check`） | 同上 | 全部 |

### 6.2 Case ID 對照

| Case ID | 類型 | 模組 | 測試類別 | 驗證 |
|---------|------|------|----------|------|
| CLOUD-001 | 整合 | gateway | `DashboardIntegrationTest` | Feign 聚合 loop+order 成 Dashboard |
| CLOUD-002 | 整合 | gateway | `GatewayRouteConfigTest` | 兩條路由 Bean 註冊 |
| CLOUD-003 | 整合 | gateway | `GatewayRouteIntegrationTest` | `/proxy/loop/trust` 轉發到 loop 後端 |
| CLOUD-004 | 整合 | gateway | `GatewayRouteIntegrationTest` | `/proxy/orders/1001` 轉發到 order 後端 |
| CLOUD-LOOP-001 | 單元 | loop-service | `TrustControllerTest` | `GET /api/v1/trust` 回信任分 |
| CLOUD-ORDER-001 | 單元 | order-service | `OrderQueryControllerTest` | `GET /api/v1/orders/{id}` 回訂單 |

**統計：整合 4 + 單元 2 = 6，全綠（0 失敗 / 0 錯誤）。**

### 6.3 測試技術重點

- **CLOUD-001/003/004** 用 WireMock 模擬後端服務，`@DynamicPropertySource` 動態注入隨機 port，驗證 Gateway↔後端的真實 HTTP 往返。
- **CLOUD-002** 以 `@SpringBootTest` 載入完整 context，`@Autowired` 兩個 `RouterFunction` 驗證註冊。
- **CLOUD-LOOP/ORDER-001** 用 `@WebMvcTest` 只載入單一 Controller 切片，`@MockBean` 隔離 Service。

### 6.4 DoD 檢查清單

- [x] `gradlew clean test` 全綠（unit + integration，6 個）
- [x] Gateway 代理 `/proxy/**` 有自動化整合測試（CLOUD-003/004）
- [x] Feign 聚合 `/api/v1/dashboard` 有整合測試（CLOUD-001）
- [ ] `.\scripts\check.ps1` 全綠；三服務可用 `:module:bootRun` 手動驗證
- [ ] Swagger 三服務可測

---

## 第 7 章　部署與運維

### 7.1 服務清單

| 服務 | 埠 | 啟動任務 |
|------|-----|----------|
| gateway | 8080 | `:gateway:bootRun` |
| loop-service | 8081 | `:loop-service:bootRun` |
| order-service | 8082 | `:order-service:bootRun` |

### 7.2 入口與文件

| 項目 | URL |
|------|-----|
| Vue 控制台 | http://localhost:8080/ |
| 教學頁 | http://localhost:8080/guide.html |
| Gateway Swagger | http://localhost:8080/swagger-ui.html |
| loop Swagger | http://localhost:8081/swagger-ui.html |
| order Swagger | http://localhost:8082/swagger-ui.html |
| Gateway Health | http://localhost:8080/actuator/health |

### 7.3 驗證腳本

| 腳本 | 用途 |
|------|------|
| `.\scripts\env.ps1` | 設定 JAVA_HOME |
| `.\scripts\check.ps1` | 驗證：`gradlew check` |
| `.\gradlew.bat :order-service:bootRun` 等 | 啟動各模組（8082／8081／8080） |

### 7.4 關鍵類別索引

| 模組 | 類別 | 職責 |
|------|------|------|
| gateway | `GatewayRouteConfig` | `/proxy/**` 代理路由 |
| gateway | `GatewayAppConfig` | HTTP/1.1 轉發 client |
| gateway | `LoopServiceClient` / `OrderServiceClient` | OpenFeign 宣告式呼叫 |
| gateway | `DashboardService` | Feign 聚合邏輯 |
| loop-service | `TrustController` / `TrustService` | 信任分 |
| order-service | `OrderQueryController` / `OrderQueryService` | 訂單查詢 |
| order-service | `GlobalExceptionHandler` | `ProblemDetail` 404 |
| common | `*Response` DTO | 跨服務資料契約 |

---

*最後更新：2026-07-07 | 技術棧：Spring Boot 3 · Spring Cloud 2023.0 · Gateway MVC · OpenFeign · JUnit 5 · WireMock*
