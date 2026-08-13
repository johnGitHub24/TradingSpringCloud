# TradingSpringCloud `scripts/` — Pure only

> 套用：`<WorkspaceRoot>/EngineeringOS/eos-minimal/hooks/apply-workspace.ps1`（不要 `-WithDemo`）

| File | Role |
|------|------|
| `portable-env.*` / `env.*` | OS `JAVA_HOME` |
| `check.*` | `gradlew check` |
| `intellij-run.properties` | `:order-service:bootRun` |
| `fix-intellij-run.ps1` | 本機 IDE 提示 |

本專案無 `demo/`（僅 FinTechDemo 使用 `-WithDemo`）。啟動請用 Gradle `bootRun`，不要把 `check.ps1` 當成啟動腳本。
