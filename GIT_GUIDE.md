# 📚 **GUIA GIT PARA APLICAÇÃO EPIC**

## 🎯 **REPOSITÓRIO CONFIGURADO**

- **URL**: https://github.com/Fe3324/epic.git
- **Branch Principal**: `main`
- **Status**: ✅ Configurado e sincronizado

---

## 🚀 **CONFIGURAÇÃO INICIAL**

### **1. Usando Script Automatizado (Recomendado)**
```powershell
# Execute o script de configuração
.\setup-git.ps1
```

### **2. Configuração Manual**
```bash
# Clonar repositório
git clone https://github.com/Fe3324/epic.git
cd epic

# Configurar usuário (se necessário)
git config user.name "Seu Nome"
git config user.email "seu.email@exemplo.com"

# Verificar configuração
git config --list
```

---

## 🔄 **WORKFLOW BÁSICO**

### **📋 Verificar Status**
```bash
# Status dos arquivos
git status

# Status resumido
git status --short

# Verificar diferenças
git diff
```

### **📝 Fazer Commit**
```bash
# Adicionar arquivos específicos
git add arquivo.java

# Adicionar todos os arquivos
git add .

# Commit com mensagem
git commit -m "feat: adiciona nova funcionalidade"

# Commit com editor
git commit
```

### **🔄 Sincronizar com GitHub**
```bash
# Baixar mudanças do GitHub
git pull origin main

# Enviar mudanças para GitHub
git push origin main

# Verificar se há mudanças remotas
git fetch origin
```

---

## 🌿 **TRABALHANDO COM BRANCHES**

### **📋 Listar Branches**
```bash
# Branches locais
git branch

# Todas as branches (local + remoto)
git branch -a

# Branches remotas
git branch -r
```

### **➕ Criar Branch**
```bash
# Criar e mudar para nova branch
git checkout -b feature/nova-funcionalidade

# Ou usando comando mais novo
git switch -c feature/nova-funcionalidade

# Criar branch a partir de commit específico
git checkout -b bugfix/correcao abc123
```

### **🔄 Trocar de Branch**
```bash
# Mudar para branch existente
git checkout main
git switch main

# Mudar para branch remota
git checkout -b feature/remota origin/feature/remota
```

### **🔀 Merge de Branches**
```bash
# Mudar para branch de destino
git checkout main

# Fazer merge
git merge feature/nova-funcionalidade

# Merge com mensagem personalizada
git merge feature/nova-funcionalidade -m "Merge: adiciona nova funcionalidade"
```

### **🗑️ Deletar Branch**
```bash
# Deletar branch local (após merge)
git branch -d feature/nova-funcionalidade

# Forçar deleção (cuidado!)
git branch -D feature/nova-funcionalidade

# Deletar branch remota
git push origin --delete feature/nova-funcionalidade
```

---

## 📜 **HISTÓRICO E LOGS**

### **📋 Ver Histórico**
```bash
# Log básico
git log

# Log resumido (uma linha por commit)
git log --oneline

# Log com gráfico
git log --graph --oneline

# Log com estatísticas
git log --stat

# Log de arquivo específico
git log arquivo.java
```

### **🔍 Buscar no Histórico**
```bash
# Buscar por mensagem
git log --grep="funcionalidade"

# Buscar por autor
git log --author="Felipe"

# Buscar por período
git log --since="2025-01-01" --until="2025-12-31"

# Buscar mudanças em código
git log -S "texto_no_codigo"
```

### **📊 Estatísticas**
```bash
# Contribuições por autor
git shortlog -sn

# Número de commits por autor
git log --pretty=format:'%an' | sort | uniq -c | sort -rn

# Estatísticas de arquivos
git log --stat
```

---

## 🔧 **COMANDOS ÚTEIS**

### **📋 Status Detalhado**
```bash
# Status com mais informações
git status -v

# Ver arquivos ignorados
git status --ignored

# Ver arquivos não rastreados
git ls-files --others --exclude-standard
```

### **🔄 Desfazer Alterações**
```bash
# Desfazer mudanças em arquivo não commitado
git checkout -- arquivo.java

# Desfazer todas as mudanças não commitadas
git checkout .

# Remover arquivo do índice (unstage)
git reset HEAD arquivo.java

# Desfazer último commit (mantém mudanças)
git reset HEAD~1

# Desfazer último commit (remove mudanças)
git reset --hard HEAD~1
```

### **📝 Corrigir Commits**
```bash
# Corrigir mensagem do último commit
git commit --amend -m "Nova mensagem"

# Adicionar arquivos ao último commit
git add arquivo.java
git commit --amend --no-edit

# Corrigir autor do último commit
git commit --amend --author="Nome <email@exemplo.com>"
```

### **🔄 Stash (Guardar Temporariamente)**
```bash
# Guardar mudanças temporariamente
git stash

# Guardar com mensagem
git stash save "trabalho em progresso"

# Listar stashes
git stash list

# Aplicar último stash
git stash pop

# Aplicar stash específico
git stash apply stash@{0}

# Deletar stash
git stash drop stash@{0}
```

---

## 🔀 **RESOLUÇÃO DE CONFLITOS**

### **⚠️ Quando Ocorrem Conflitos**
```bash
# Durante merge
git merge feature/branch
# CONFLICT (content): Merge conflict in arquivo.java

# Durante pull
git pull origin main
# CONFLICT (content): Merge conflict in arquivo.java
```

### **🛠️ Resolvendo Conflitos**
```bash
# 1. Ver arquivos em conflito
git status

# 2. Editar arquivos e remover marcadores
# <<<<<<< HEAD
# Seu código
# =======
# Código do remote
# >>>>>>> branch

# 3. Adicionar arquivos resolvidos
git add arquivo.java

# 4. Finalizar merge
git commit
```

### **🔧 Ferramentas de Merge**
```bash
# Configurar ferramenta de merge
git config merge.tool vimdiff

# Usar ferramenta visual
git mergetool

# Abortar merge em caso de problemas
git merge --abort
```

---

## 📋 **CONVENÇÕES DE COMMIT**

### **📝 Formato Padrão**
```
tipo(escopo): descrição breve

Descrição detalhada (opcional)

Referências (opcional)
```

### **🏷️ Tipos de Commit**
- `feat`: Nova funcionalidade
- `fix`: Correção de bug
- `docs`: Documentação
- `style`: Formatação de código
- `refactor`: Refatoração
- `test`: Testes
- `chore`: Tarefas de manutenção

### **📖 Exemplos**
```bash
# Funcionalidade
git commit -m "feat(auth): adiciona autenticação JWT"

# Correção
git commit -m "fix(users): corrige validação de email"

# Documentação
git commit -m "docs(readme): atualiza instruções de instalação"

# Refatoração
git commit -m "refactor(services): melhora estrutura do UserService"

# Testes
git commit -m "test(auth): adiciona testes para AuthController"
```

---

## 🔒 **SEGURANÇA E BOAS PRÁTICAS**

### **🛡️ Arquivo .gitignore**
```gitignore
# Já configurado no projeto
target/
*.log
.env
.DS_Store
*.tmp
```

### **🔐 Senhas e Secrets**
```bash
# NUNCA committar:
# - Senhas em texto plano
# - API keys
# - Tokens de acesso
# - Arquivos .env com dados sensíveis

# Use variáveis de ambiente
export DATABASE_PASSWORD=sua_senha
```

### **📋 Verificações Antes do Commit**
```bash
# 1. Executar testes
./mvnw test

# 2. Verificar build
./mvnw clean compile

# 3. Verificar status
git status

# 4. Revisar mudanças
git diff --cached

# 5. Commit
git commit -m "mensagem descritiva"
```

---

## 🚨 **TROUBLESHOOTING**

### **❌ Problemas Comuns**

#### **1. Erro de Autenticação**
```bash
# Verificar remote URL
git remote -v

# Usar HTTPS
git remote set-url origin https://github.com/Fe3324/epic.git

# Configurar credenciais
git config --global user.name "Seu Nome"
git config --global user.email "seu@email.com"
```

#### **2. Conflitos de Merge**
```bash
# Ver arquivos em conflito
git status

# Abortar merge se necessário
git merge --abort

# Ou resolver e continuar
git add .
git commit
```

#### **3. Commit Rejeitado**
```bash
# Remote está à frente
git pull origin main
git push origin main

# Forçar push (cuidado!)
git push --force-with-lease origin main
```

#### **4. Arquivo Grande**
```bash
# Remover arquivo do histórico
git filter-branch --force --index-filter 'git rm --cached --ignore-unmatch arquivo_grande.jar' --prune-empty --tag-name-filter cat -- --all
```

---

## 📊 **COMANDOS DE MONITORAMENTO**

### **📈 Estatísticas do Repositório**
```bash
# Tamanho do repositório
git count-objects -vH

# Arquivos mais modificados
git log --pretty=format: --name-only | sort | uniq -c | sort -rg | head -10

# Contribuidores mais ativos
git shortlog -sn --all

# Linhas de código por autor
git log --author="Felipe" --pretty=tformat: --numstat | awk '{ add += $1; subs += $2; loc += $1 - $2 } END { printf "added lines: %s, removed lines: %s, total lines: %s\n", add, subs, loc }'
```

### **🔍 Verificação de Integridade**
```bash
# Verificar integridade do repositório
git fsck

# Limpar arquivos desnecessários
git gc

# Otimizar repositório
git gc --aggressive
```

---

## 🔗 **LINKS ÚTEIS**

- **Repositório**: https://github.com/Fe3324/epic.git
- **Documentação Git**: https://git-scm.com/doc
- **GitHub Docs**: https://docs.github.com/
- **Conventional Commits**: https://www.conventionalcommits.org/

---

## 📞 **SUPORTE**

Se encontrar problemas:

1. **Execute**: `.\setup-git.ps1` para reconfigurar
2. **Verifique**: Status com `git status`
3. **Consulte**: Este guia para comandos específicos
4. **Reset**: Em último caso, faça backup e clone novamente

---

**🎉 Git configurado e pronto para desenvolvimento! 🚀** 