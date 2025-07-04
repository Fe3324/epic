# Script para criar usuário admin na aplicação Epic
# Uso: .\create-admin.ps1 -Username "meuadmin" -Email "admin@teste.com" -Password "admin123"

param(
    [Parameter(Mandatory=$true)]
    [string]$Username,
    
    [Parameter(Mandatory=$true)]
    [string]$Email,
    
    [Parameter(Mandatory=$true)]
    [string]$Password,
    
    [string]$FirstName = "Admin",
    [string]$LastName = "Sistema",
    [string]$BaseUrl = "http://localhost:8080"
)

Write-Host "=== CRIANDO USUÁRIO ADMIN ===" -ForegroundColor Green

# 1. Registrar usuário com roles de admin
$registerBody = @{
    username = $Username
    email = $Email
    password = $Password
    confirmPassword = $Password
    firstName = $FirstName
    lastName = $LastName
} | ConvertTo-Json

try {
    Write-Host "Registrando usuário admin: $Username" -ForegroundColor Yellow
    
    $response = Invoke-RestMethod -Uri "$BaseUrl/auth/register-first-admin" `
        -Method POST `
        -ContentType "application/json" `
        -Body $registerBody
    
    Write-Host "✅ SUCESSO! Usuário admin criado!" -ForegroundColor Green
    Write-Host "Username: $($response.data.user.username)" -ForegroundColor White
    Write-Host "Email: $($response.data.user.email)" -ForegroundColor White
    Write-Host "Roles: $($response.data.user.roles | ForEach-Object { $_.name } | Join-String -Separator ', ')" -ForegroundColor White
    Write-Host "Token: $($response.data.accessToken.Substring(0,50))..." -ForegroundColor Cyan
    
    # Testar acesso admin
    Write-Host "`n=== TESTANDO ACESSO ADMIN ===" -ForegroundColor Green
    $headers = @{ Authorization = "Bearer $($response.data.accessToken)" }
    
    $roles = Invoke-RestMethod -Uri "$BaseUrl/api/roles" -Headers $headers
    Write-Host "✅ Acesso a /api/roles funcionando! Total de roles: $($roles.data.totalElements)" -ForegroundColor Green
    
} catch {
    Write-Host "❌ ERRO: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $responseBody = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($responseBody)
        $responseText = $reader.ReadToEnd()
        Write-Host "Detalhes: $responseText" -ForegroundColor Red
    }
}

Write-Host "`n=== COMANDOS ÚTEIS ===" -ForegroundColor Yellow
Write-Host "Login: Invoke-RestMethod -Uri '$BaseUrl/auth/login' -Method POST -ContentType 'application/json' -Body '{\"username\":\"$Username\",\"password\":\"$Password\"}'" -ForegroundColor Gray 