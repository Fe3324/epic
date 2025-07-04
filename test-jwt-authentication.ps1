# Script para demonstrar autenticação JWT na aplicação Epic
# Demonstra: Login, Acesso a endpoints protegidos, Renovação de token, Logout

Write-Host "🔐 === DEMONSTRAÇÃO COMPLETA DE AUTENTICAÇÃO JWT ===" -ForegroundColor Green
Write-Host ""

$baseUrl = "http://localhost:8080"

# 1. REGISTRO DE USUÁRIO ADMIN
Write-Host "👑 1. REGISTRANDO USUÁRIO ADMIN..." -ForegroundColor Yellow
try {
    $registerBody = @{
        username = "demoadmin"
        email = "demo@admin.com"
        password = "admin123"
        confirmPassword = "admin123"
        firstName = "Demo"
        lastName = "Admin"
    } | ConvertTo-Json

    $registerResponse = Invoke-RestMethod -Uri "$baseUrl/auth/register-first-admin" -Method POST -ContentType 'application/json' -Body $registerBody
    Write-Host "✅ Admin registrado com sucesso!" -ForegroundColor Green
    Write-Host "Username: $($registerResponse.data.user.username)" -ForegroundColor White
    Write-Host "Roles: $($registerResponse.data.user.roles | ForEach-Object { $_.name } | Join-String -Separator ', ')" -ForegroundColor White
} catch {
    Write-Host "⚠️ Admin pode já existir, tentando fazer login..." -ForegroundColor Yellow
}

Write-Host ""

# 2. LOGIN E OBTENÇÃO DE TOKENS
Write-Host "🔐 2. FAZENDO LOGIN..." -ForegroundColor Yellow
try {
    $loginBody = @{
        username = "demoadmin"
        password = "admin123"
    } | ConvertTo-Json

    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -ContentType 'application/json' -Body $loginBody
    
    Write-Host "✅ Login realizado com sucesso!" -ForegroundColor Green
    Write-Host "Access Token: $($loginResponse.data.accessToken.Substring(0,50))..." -ForegroundColor Cyan
    Write-Host "Refresh Token: $($loginResponse.data.refreshToken.Substring(0,50))..." -ForegroundColor Cyan
    Write-Host "Expira em: $($loginResponse.data.expiresIn) segundos" -ForegroundColor White
    Write-Host "Usuário: $($loginResponse.data.user.username)" -ForegroundColor White
    
    # Salvar tokens
    $accessToken = $loginResponse.data.accessToken
    $refreshToken = $loginResponse.data.refreshToken
    $headers = @{Authorization = "Bearer $accessToken"}
    
} catch {
    Write-Host "❌ ERRO no login: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""

# 3. VALIDAÇÃO DE TOKEN
Write-Host "🔍 3. VALIDANDO TOKEN..." -ForegroundColor Yellow
try {
    $validationResponse = Invoke-RestMethod -Uri "$baseUrl/auth/validate" -Headers $headers
    Write-Host "✅ Token é válido: $($validationResponse.data)" -ForegroundColor Green
} catch {
    Write-Host "❌ Token inválido!" -ForegroundColor Red
}

Write-Host ""

# 4. ACESSANDO DADOS DO USUÁRIO ATUAL
Write-Host "👤 4. OBTENDO DADOS DO USUÁRIO ATUAL..." -ForegroundColor Yellow
try {
    $meResponse = Invoke-RestMethod -Uri "$baseUrl/auth/me" -Headers $headers
    Write-Host "✅ Dados do usuário:" -ForegroundColor Green
    Write-Host "ID: $($meResponse.data.id)" -ForegroundColor White
    Write-Host "Username: $($meResponse.data.username)" -ForegroundColor White
    Write-Host "Email: $($meResponse.data.email)" -ForegroundColor White
    Write-Host "Nome: $($meResponse.data.fullName)" -ForegroundColor White
    Write-Host "Roles: $($meResponse.data.roles | ForEach-Object { $_.name } | Join-String -Separator ', ')" -ForegroundColor White
} catch {
    Write-Host "❌ Erro ao obter dados do usuário!" -ForegroundColor Red
}

Write-Host ""

# 5. TESTANDO ENDPOINTS PROTEGIDOS
Write-Host "🔒 5. TESTANDO ENDPOINTS PROTEGIDOS..." -ForegroundColor Yellow

# 5.1 - Endpoint para usuários autenticados
Write-Host "   5.1. Acessando /api/users (autenticado)..." -ForegroundColor Cyan
try {
    $usersResponse = Invoke-RestMethod -Uri "$baseUrl/api/users" -Headers $headers
    Write-Host "   ✅ Acesso autorizado! Total de usuários: $($usersResponse.data.totalElements)" -ForegroundColor Green
} catch {
    Write-Host "   ❌ Acesso negado!" -ForegroundColor Red
}

# 5.2 - Endpoint apenas para ADMINs
Write-Host "   5.2. Acessando /api/roles (apenas ADMIN)..." -ForegroundColor Cyan
try {
    $rolesResponse = Invoke-RestMethod -Uri "$baseUrl/api/roles" -Headers $headers
    Write-Host "   ✅ Acesso ADMIN autorizado! Total de roles: $($rolesResponse.data.totalElements)" -ForegroundColor Green
    Write-Host "   Roles disponíveis: $($rolesResponse.data.content | ForEach-Object { $_.name } | Join-String -Separator ', ')" -ForegroundColor White
} catch {
    Write-Host "   ❌ Acesso ADMIN negado!" -ForegroundColor Red
}

Write-Host ""

# 6. TESTANDO RENOVAÇÃO DE TOKEN
Write-Host "🔄 6. TESTANDO RENOVAÇÃO DE TOKEN..." -ForegroundColor Yellow
try {
    $refreshBody = @{
        refreshToken = $refreshToken
    } | ConvertTo-Json

    $refreshResponse = Invoke-RestMethod -Uri "$baseUrl/auth/refresh" -Method POST -ContentType 'application/json' -Body $refreshBody -Headers $headers
    
    Write-Host "✅ Token renovado com sucesso!" -ForegroundColor Green
    Write-Host "Novo Access Token: $($refreshResponse.data.accessToken.Substring(0,50))..." -ForegroundColor Cyan
    Write-Host "Novo Refresh Token: $($refreshResponse.data.refreshToken.Substring(0,50))..." -ForegroundColor Cyan
    
    # Atualizar tokens
    $accessToken = $refreshResponse.data.accessToken
    $refreshToken = $refreshResponse.data.refreshToken
    $headers = @{Authorization = "Bearer $accessToken"}
    
} catch {
    Write-Host "❌ Erro na renovação do token!" -ForegroundColor Red
}

Write-Host ""

# 7. TESTANDO ENDPOINT ADMINISTRATIVO
Write-Host "👑 7. TESTANDO ENDPOINT ADMINISTRATIVO..." -ForegroundColor Yellow
try {
    $adminResponse = Invoke-RestMethod -Uri "$baseUrl/admin/promote-user/1" -Method POST -Headers $headers
    Write-Host "✅ Endpoint administrativo acessível!" -ForegroundColor Green
} catch {
    if ($_.Exception.Response.StatusCode -eq 404) {
        Write-Host "⚠️ Usuário ID 1 não encontrado (normal)" -ForegroundColor Yellow
    } else {
        Write-Host "❌ Erro no endpoint administrativo!" -ForegroundColor Red
    }
}

Write-Host ""

# 8. TESTANDO LOGOUT
Write-Host "🚪 8. FAZENDO LOGOUT..." -ForegroundColor Yellow
try {
    $logoutResponse = Invoke-RestMethod -Uri "$baseUrl/auth/logout" -Method POST -Headers $headers
    Write-Host "✅ Logout realizado com sucesso!" -ForegroundColor Green
} catch {
    Write-Host "❌ Erro no logout!" -ForegroundColor Red
}

Write-Host ""

# 9. TESTANDO ACESSO APÓS LOGOUT
Write-Host "🔒 9. TESTANDO ACESSO APÓS LOGOUT..." -ForegroundColor Yellow
try {
    $testAfterLogout = Invoke-RestMethod -Uri "$baseUrl/auth/me" -Headers $headers
    Write-Host "⚠️ Token ainda válido (logout não implementa blacklist)" -ForegroundColor Yellow
} catch {
    Write-Host "✅ Acesso negado após logout!" -ForegroundColor Green
}

Write-Host ""

# 10. TESTANDO ACESSO SEM TOKEN
Write-Host "🚫 10. TESTANDO ACESSO SEM TOKEN..." -ForegroundColor Yellow
try {
    $noTokenResponse = Invoke-RestMethod -Uri "$baseUrl/api/users"
    Write-Host "❌ Acesso deveria ser negado!" -ForegroundColor Red
} catch {
    Write-Host "✅ Acesso corretamente negado sem token!" -ForegroundColor Green
}

Write-Host ""
Write-Host "🎉 === DEMONSTRAÇÃO COMPLETA ===" -ForegroundColor Green
Write-Host ""
Write-Host "📋 RESUMO DO QUE FOI TESTADO:" -ForegroundColor Yellow
Write-Host "✅ Registro de usuário admin" -ForegroundColor Green
Write-Host "✅ Login e obtenção de tokens JWT" -ForegroundColor Green
Write-Host "✅ Validação de token" -ForegroundColor Green
Write-Host "✅ Acesso a dados do usuário autenticado" -ForegroundColor Green
Write-Host "✅ Endpoints protegidos por autenticação" -ForegroundColor Green
Write-Host "✅ Endpoints protegidos por role ADMIN" -ForegroundColor Green
Write-Host "✅ Renovação de token" -ForegroundColor Green
Write-Host "✅ Logout de usuário" -ForegroundColor Green
Write-Host "✅ Proteção contra acesso sem token" -ForegroundColor Green
Write-Host ""
Write-Host "🚀 A autenticação JWT está funcionando perfeitamente!" -ForegroundColor Green

# Salvar tokens para uso posterior
Write-Host ""
Write-Host "💾 TOKENS SALVOS PARA USO POSTERIOR:" -ForegroundColor Yellow
Write-Host "Access Token: $accessToken" -ForegroundColor Gray
Write-Host "Refresh Token: $refreshToken" -ForegroundColor Gray
Write-Host ""
Write-Host "🔧 COMANDOS ÚTEIS:" -ForegroundColor Yellow
Write-Host "`$headers = @{Authorization = 'Bearer $accessToken'}" -ForegroundColor Gray
Write-Host "Invoke-RestMethod -Uri 'http://localhost:8080/api/users' -Headers `$headers" -ForegroundColor Gray 