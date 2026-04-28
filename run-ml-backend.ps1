$ErrorActionPreference = "Stop"

$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$setupScript = Join-Path $scriptRoot "setup-python-service.ps1"
$pythonScript = Join-Path $scriptRoot "start-python-service.ps1"

& $setupScript
& $pythonScript
