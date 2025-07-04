# Script para configurar Git na aplicação Epic
# Uso: .\setup-git.ps1

Write-Host "🔧 === CONFIGURAÇÃO GIT PARA APLICAÇÃO EPIC ===" -ForegroundColor Green
Write-Host ""

# Variáveis
$repoUrl = "https://github.com/Fe3324/epic.git"
$gitPath = "C:\Program Files\Git\bin"

# 1. Verificar se Git está instalado
Write-Host "🔍 1. VERIFICANDO INSTALAÇÃO DO GIT..." -ForegroundColor Yellow
try {
    if (Test-Path $gitPath) {
        $env:PATH += ";$gitPath"
        $gitVersion = git --version
        Write-Host "✅ Git encontrado: $gitVersion" -ForegroundColor Green
    } else {
        throw "Git não encontrado"
    }
} catch {
    Write-Host "❌ Git não instalado. Instalando..." -ForegroundColor Red
    try {
        winget install --id Git.Git -e --source winget
        $env:PATH += ";$gitPath"
        Write-Host "✅ Git instalado com sucesso!" -ForegroundColor Green
    } catch {
        Write-Host "❌ Erro ao instalar Git. Instale manualmente." -ForegroundColor Red
        exit 1
    }
}

Write-Host ""

# 2. Verificar se já é um repositório Git
Write-Host "📁 2. VERIFICANDO STATUS DO REPOSITÓRIO..." -ForegroundColor Yellow
if (Test-Path ".git") {
    Write-Host "✅ Repositório Git já inicializado" -ForegroundColor Green
    
    # Verificar remote
    try {
        $remote = git remote get-url origin
        if ($remote -eq $repoUrl) {
            Write-Host "✅ Remote configurado corretamente: $remote" -ForegroundColor Green
        } else {
            Write-Host "⚠️ Remote diferente: $remote" -ForegroundColor Yellow
            Write-Host "Atualizando remote..." -ForegroundColor Yellow
            git remote set-url origin $repoUrl
            Write-Host "✅ Remote atualizado!" -ForegroundColor Green
        }
    } catch {
        Write-Host "⚠️ Adicionando remote..." -ForegroundColor Yellow
        git remote add origin $repoUrl
        Write-Host "✅ Remote adicionado!" -ForegroundColor Green
    }
} else {
    Write-Host "⚠️ Inicializando repositório Git..." -ForegroundColor Yellow
    git init
    git remote add origin $repoUrl
    Write-Host "✅ Repositório inicializado!" -ForegroundColor Green
}

Write-Host ""

# 3. Configurar usuário Git
Write-Host "👤 3. CONFIGURANDO USUÁRIO GIT..." -ForegroundColor Yellow
$currentUser = git config user.name
$currentEmail = git config user.email

if (-not $currentUser) {
    $userName = Read-Host "Digite seu nome"
    git config user.name $userName
    Write-Host "✅ Nome configurado: $userName" -ForegroundColor Green
} else {
    Write-Host "✅ Nome já configurado: $currentUser" -ForegroundColor Green
}

if (-not $currentEmail) {
    $userEmail = Read-Host "Digite seu email"
    git config user.email $userEmail
    Write-Host "✅ Email configurado: $userEmail" -ForegroundColor Green
} else {
    Write-Host "✅ Email já configurado: $currentEmail" -ForegroundColor Green
}

Write-Host ""

# 4. Verificar status dos arquivos
Write-Host "📋 4. VERIFICANDO STATUS DOS ARQUIVOS..." -ForegroundColor Yellow
$status = git status --porcelain
if ($status) {
    Write-Host "⚠️ Arquivos não commitados encontrados:" -ForegroundColor Yellow
    git status --short
    
    $commit = Read-Host "Deseja adicionar e committar todos os arquivos? (s/n)"
    if ($commit -eq "s" -or $commit -eq "S") {
        git add .
        $commitMessage = Read-Host "Digite a mensagem do commit"
        git commit -m $commitMessage
        Write-Host "✅ Commit realizado!" -ForegroundColor Green
        
        $push = Read-Host "Deseja fazer push para o GitHub? (s/n)"
        if ($push -eq "s" -or $push -eq "S") {
            git push origin main
            Write-Host "✅ Push realizado!" -ForegroundColor Green
        }
    }
} else {
    Write-Host "✅ Working tree limpo - nada para committar" -ForegroundColor Green
}

Write-Host ""

# 5. Verificar sincronização com remote
Write-Host "🔄 5. VERIFICANDO SINCRONIZAÇÃO..." -ForegroundColor Yellow
try {
    git fetch origin
    $behind = git rev-list HEAD..origin/main --count
    $ahead = git rev-list origin/main..HEAD --count
    
    if ($behind -gt 0) {
        Write-Host "⚠️ Seu repositório está $behind commits atrás do remote" -ForegroundColor Yellow
        $pull = Read-Host "Deseja fazer pull? (s/n)"
        if ($pull -eq "s" -or $pull -eq "S") {
            git pull origin main
            Write-Host "✅ Pull realizado!" -ForegroundColor Green
        }
    } elseif ($ahead -gt 0) {
        Write-Host "⚠️ Seu repositório está $ahead commits à frente do remote" -ForegroundColor Yellow
        $push = Read-Host "Deseja fazer push? (s/n)"
        if ($push -eq "s" -or $push -eq "S") {
            git push origin main
            Write-Host "✅ Push realizado!" -ForegroundColor Green
        }
    } else {
        Write-Host "✅ Repositório sincronizado com o remote" -ForegroundColor Green
    }
} catch {
    Write-Host "⚠️ Não foi possível verificar sincronização" -ForegroundColor Yellow
}

Write-Host ""

# 6. Mostrar informações finais
Write-Host "📊 6. INFORMAÇÕES DO REPOSITÓRIO:" -ForegroundColor Yellow
Write-Host "Repository URL: $repoUrl" -ForegroundColor White
Write-Host "Branch atual: $(git branch --show-current)" -ForegroundColor White
Write-Host "Último commit: $(git log -1 --oneline)" -ForegroundColor White
Write-Host "Remote: $(git remote get-url origin)" -ForegroundColor White

Write-Host ""
Write-Host "🎉 === CONFIGURAÇÃO CONCLUÍDA ===" -ForegroundColor Green
Write-Host ""
Write-Host "📋 COMANDOS ÚTEIS:" -ForegroundColor Yellow
Write-Host "git status              - Verificar status" -ForegroundColor Gray
Write-Host "git add .               - Adicionar arquivos" -ForegroundColor Gray
Write-Host "git commit -m 'msg'     - Fazer commit" -ForegroundColor Gray
Write-Host "git push origin main    - Enviar para GitHub" -ForegroundColor Gray
Write-Host "git pull origin main    - Baixar do GitHub" -ForegroundColor Gray
Write-Host "git log --oneline       - Ver histórico" -ForegroundColor Gray
Write-Host ""
Write-Host "🔗 Acesse o repositório: $repoUrl" -ForegroundColor Cyan 