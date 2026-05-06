$ErrorActionPreference = "Stop"

$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$serviceRoot = Join-Path $scriptRoot "python-service"

Set-Location $serviceRoot
python -m uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
