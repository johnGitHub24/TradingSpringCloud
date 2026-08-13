# TradingSpringCloud Specification

> **EOS 入口規格（英文摘要）。** 領域細節與驗收衝突以 [TradingSpringCloud 規格書.md](TradingSpringCloud%20規格書.md) 為準。  
> Docs standard: EngineeringOS eos-minimal @ 0.1.10 — `knowledge/documentation.md`

## 0. Document map

| File | Role |
|------|------|
| [TradingSpringCloud 規格書.md](TradingSpringCloud%20規格書.md) | **主規格書（權威）** |
| This file | EOS 英文入口／摘要 |
| [API規格書.md](API規格書.md) | API 端點 |
| [docs/architecture.md](docs/architecture.md) | 分層／模組／runtime |
| [docs/testing.md](docs/testing.md) | 測試／DoD 摘要 |
| [docs/資料庫設計.md](docs/資料庫設計.md) | 無 DB（記憶體）說明 |
| [docs/驗證設計.md](docs/驗證設計.md) | ProblemDetail／錯誤 |
| [docs/testing.md](docs/testing.md) | Case ID、CI |
| [CLAUDE.md](CLAUDE.md) | AI 薄規則 |
| [README.md](README.md) | 快速開始 |

## 1. Scope

- **Purpose:** Gateway MVC 代理 vs OpenFeign 聚合的入門對照（固定 URL，無 Eureka／Config）。
- **Stack:** Java 21 · Spring Boot 3 · `spring-cloud-gateway-server-mvc` · OpenFeign · Vue 3 static · Gradle 多模組
- **Ports:** gateway `8080` · loop-service `8081` · order-service `8082`
- **Non-goals:** Eureka、Config Server、Kafka、Redis、DB、熔斷限流

## 2. Architecture

- Feign 聚合：`GET /api/v1/dashboard` → loop trust + order list  
- Gateway 代理：`/proxy/loop/{segment}`、`/proxy/orders/{segment}`  
見 [docs/architecture.md](docs/architecture.md)。

## 3. API / Contract

權威細節：[API規格書.md](API規格書.md)、Controllers。

| Method | Path | Notes |
|--------|------|-------|
| GET | `/api/v1/dashboard` | Feign 聚合 |
| GET | `/api/v1/gateway/routes` | 路由檢視（Gateway） |
| GET | `/proxy/loop/{segment}` | → loop `/api/v1/{segment}` |
| GET | `/proxy/orders/{segment}` | → order `/api/v1/orders/{segment}` |
| GET | `/api/v1/trust` | loop（亦可經 proxy） |
| POST | `/api/v1/trust/increment` | loop |
| GET | `/api/v1/health/ping` | loop |
| GET | `/api/v1/orders`、`/api/v1/orders/{id}` | order；無則 404 ProblemDetail |

## 4. Test DoD

- [ ] `.\scripts\check.ps1`／`.\gradlew.bat check` green
- [ ] Dashboard 聚合 + 代理路徑 + order 404（見 [docs/testing.md](docs/testing.md)）

## 5. Changelog

| Date | Note |
|------|------|
| 2026-07-10 | EOS SPEC 入口；摘自中文規格書／API／Controllers／測試與CI |
