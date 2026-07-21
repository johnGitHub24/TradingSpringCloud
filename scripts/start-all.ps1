# 啟動三個微服務（各開新視窗）
. "$PSScriptRoot\env.ps1"
$root = Split-Path $PSScriptRoot -Parent
Write-Host "Starting loop-service :8081, order-service :8082, gateway :8080"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root'; .\gradlew.bat :loop-service:bootRun"
Start-Sleep -Seconds 8
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root'; .\gradlew.bat :order-service:bootRun"
Start-Sleep -Seconds 8
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root'; .\gradlew.bat :gateway:bootRun"
Write-Host "Open http://localhost:8080/ when all services are up"
