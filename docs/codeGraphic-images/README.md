# CodeGraphic image export

Source: docs/codeGraphic.html
Tool: @mermaid-js/mermaid-cli@11 (dark)
Script: EngineeringOS/eos-minimal/hooks/export-codeGraphic-images.ps1

| File | Tab |
|------|-----|
| `01-feign.svg` / `.png` | Feign 聚合 |
| `02-proxy.svg` / `.png` | Gateway 代理 |
| `03-services.svg` / `.png` | 服務 |
| `04-modules.svg` / `.png` | 模組 |

Re-run from project root:

    & "d:\ClaudeCode\EngineeringOS\eos-minimal\hooks\export-codeGraphic-images.ps1" -ProjectRoot .
