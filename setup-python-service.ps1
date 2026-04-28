$ErrorActionPreference = "Stop"

$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$serviceRoot = Join-Path $scriptRoot "python-service"
$requirementsFile = Join-Path $serviceRoot "requirements.txt"
$buildAssetsScript = Join-Path $serviceRoot "scripts\build_assets.py"

Set-Location $serviceRoot

py -3.12 -m pip install --upgrade pip
py -3.12 -m pip install --no-cache-dir --default-timeout=100 -r $requirementsFile
py -3.12 $buildAssetsScript

Write-Host ""
Write-Host "Python microservice dependencies installed successfully." -ForegroundColor Green
