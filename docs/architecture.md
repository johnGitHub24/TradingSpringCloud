# Architecture — TradingSpringCloud

> 衝突以 [TradingSpringCloud 規格書.md](../TradingSpringCloud%20規格書.md) 為準。  
> EOS 入口：[TradingSpringCloud-SPEC.md](../TradingSpringCloud-SPEC.md)。  
> 中文學習：[架構學習導引.md](架構學習導引.md)、[功能流程說明.md](功能流程說明.md)。  
> 儲存：[資料庫設計.md](資料庫設計.md)。規範：EngineeringOS `knowledge/documentation.md` @ 0.1.4

## Layers

| Layer | Module / Package | Responsibility |
|-------|------------------|----------------|
| Gateway API | `gateway/.../api` | `DashboardController`（Feign 聚合） |
| Gateway Application | `gateway/.../application` | `DashboardService` |
| Gateway Client | `gateway/.../client` | OpenFeign → loop／order |
| Gateway Config | `gateway/.../config` | `GatewayRouteConfig`（代理）、HTTP/1.1 client |
| loop API／App | `loop-service` | 信任分查詢／累加（記憶體） |
| order API／App | `order-service` | 訂單查詢（記憶體 Map） |
| order Domain | `order-service/.../domain` | `OrderNotFoundException` + ProblemDetail |
| Common | `common/dto` | `TrustResponse`、`OrderSummaryResponse`、`DashboardResponse` |

## Module map

| Module | Port | Notes |
|--------|------|-------|
| `gateway` | 8080 | Gateway MVC + Feign Dashboard + Vue static |
| `loop-service` | 8081 | 閉環信任分 |
| `order-service` | 8082 | 訂單查詢 |
| `common` | — | 共用 DTO |

**刻意不做：** Eureka、Config Server、Kafka、Redis、DB（對照 [TradingMicroService](../../TradingMicroService)）。

## Runtime

```text
【Feign 聚合】
Client → GET /api/v1/dashboard
       → LoopServiceClient → loop :8081 /api/v1/trust
       → OrderServiceClient → order :8082 /api/v1/orders
       → DashboardResponse

【Gateway MVC 代理】
Client → GET /proxy/loop/**  → 改寫 → loop /api/v1/**
Client → GET /proxy/orders/** → 改寫 → order /api/v1/orders/**
```

服務位址為固定 URL（`ServiceUrlsProperties`），非服務發現。

## Visual maps

| 文件 | 用途 |
|------|------|
| [codeGraphic.html](codeGraphic.html) | Tab：Feign／代理／服務／模組（圖為主） |
| [專案引導教學.html](專案引導教學.html) | 長文引導＋流程圖 |
