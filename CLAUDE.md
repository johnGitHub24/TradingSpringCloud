# TradingSpringCloud — 專案規則（薄）

繼承：EngineeringOS eos-minimal @ **0.1.10**  
公版：`EngineeringOS/eos-minimal/`  
權威規格：[TradingSpringCloud 規格書.md](TradingSpringCloud%20規格書.md)  
EOS 入口：[TradingSpringCloud-SPEC.md](TradingSpringCloud-SPEC.md)

## 與公版差異

- 多模組：`gateway`（:8080）+ `loop-service`（:8081）+ `order-service`（:8082）+ `common`
- 固定 URL Feign／Gateway MVC（無 Eureka／Config／DB）
- 驗證入口：`.\scripts\check.ps1`（`gradlew check`）
- 本機 Demo：IntelliJ／Gradle `:order-service:bootRun`（:8082）；另開 `:loop-service:bootRun`（:8081）、`:gateway:bootRun`（:8080）
- Docs standard：`knowledge/documentation.md`

## 本專案專屬

- Domain：Gateway 代理 vs Feign 聚合教學
- 架構：`docs/architecture.md`；DB：`docs/資料庫設計.md`；驗證：`docs/驗證設計.md`
- 測試：`docs/testing.md`、`docs/testing.md`
- API 契約：[API規格書.md](API規格書.md)

## 註解深度
- comment_verbosity: **detailed**
- 權威：`EngineeringOS/eos-minimal/knowledge/comments.md` §0／§3b（eos-minimal @ 0.1.10）
- 結構：【職責】【技巧】【概念】；簡單 getter 可併入類別說明


## Git Remote
- 帳號：`johnGitHub24`；一專案一 repo
- 規範：`EngineeringOS/eos-minimal/knowledge/專案上船-GitHub.md`

## 回寫

問題與公版改善建議 → `EngineeringOS/eos-minimal/feedback/SYNC_LOG.md`
