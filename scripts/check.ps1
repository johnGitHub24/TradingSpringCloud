. "$PSScriptRoot\env.ps1"
Set-Location (Split-Path $PSScriptRoot -Parent)
.\gradlew.bat check
