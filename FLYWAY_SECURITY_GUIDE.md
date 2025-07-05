# 🛡️ GUIA DE SEGURANÇA DO FLYWAY

## 📋 VISÃO GERAL

Este sistema implementa uma validação de segurança para migrações do Flyway que **previne automaticamente** a execução de operações perigosas como `DELETE`, `TRUNCATE` e `DROP`.

### ✨ FUNCIONALIDADES

- ✅ **Bloqueio automático** de operações destrutivas
- ✅ **Configuração flexível** via `application.properties`
- ✅ **Bypass por ambiente** (development, test, production)
- ✅ **Modo permissivo** para avisos ao invés de erros
- ✅ **Operações específicas permitidas** via configuração
- ✅ **Logs detalhados** de validação

---

## 🔧 CONFIGURAÇÕES

### `application.properties`

```properties
# ========================================
# CONFIGURAÇÕES DE SEGURANÇA DO FLYWAY
# ========================================

# Habilitar/desabilitar validação (padrão: true)
flyway.security.validation.enabled=true

# Modo de validação: strict (bloqueia) ou permissive (avisa)
flyway.security.validation.mode=strict

# Logs detalhados da validação
flyway.security.validation.verbose=true

# Operações bloqueadas (padrão: DELETE,TRUNCATE,DROP)
flyway.security.blocked-operations=DELETE,TRUNCATE,DROP

# Operações explicitamente permitidas
flyway.security.allowed-operations=DROP INDEX,ALTER TABLE DROP COLUMN

# Ambientes que ignoram a validação
flyway.security.bypass.environments=test,development

# Ambiente atual
flyway.security.current.environment=development
```

---

## 🚨 OPERAÇÕES BLOQUEADAS

### Por Padrão:

| Operação | Padrão de Detecção | Exemplo |
|----------|-------------------|---------|
| **DELETE** | `DELETE FROM` | `DELETE FROM users WHERE active = false` |
| **TRUNCATE** | `TRUNCATE TABLE` | `TRUNCATE TABLE logs` |
| **DROP** | `DROP TABLE/INDEX/VIEW` | `DROP TABLE old_table` |

### Exemplos de Scripts Bloqueados:

```sql
-- ❌ BLOQUEADO: Deletar registros
DELETE FROM users WHERE last_login < '2023-01-01';

-- ❌ BLOQUEADO: Limpar tabela
TRUNCATE TABLE system_logs;

-- ❌ BLOQUEADO: Remover tabela
DROP TABLE temporary_data;

-- ❌ BLOQUEADO: Remover índice
DROP INDEX idx_old_column;
```

---

## ✅ OPERAÇÕES PERMITIDAS

### Automaticamente Permitidas:

```sql
-- ✅ CREATE com IF NOT EXISTS
CREATE TABLE IF NOT EXISTS new_table (id BIGINT);

-- ✅ ALTER TABLE DROP CONSTRAINT
ALTER TABLE users DROP CONSTRAINT old_constraint;

-- ✅ ON DELETE CASCADE
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- ✅ Todas as operações INSERT, UPDATE, CREATE
INSERT INTO users (name) VALUES ('Test');
UPDATE users SET status = 'active' WHERE id = 1;
CREATE INDEX idx_name ON users(name);
```

---

## 🔨 COMO USAR

### 1. **Desenvolvimento Normal**
```properties
flyway.security.current.environment=development
flyway.security.bypass.environments=development
```
- ✅ Validação ignorada em desenvolvimento
- ✅ Desenvolvedores podem testar livremente

### 2. **Ambiente de Teste**
```properties
flyway.security.current.environment=test
flyway.security.validation.mode=permissive
```
- ⚠️ Operações perigosas geram avisos
- ✅ Testes podem continuar executando

### 3. **Produção (Máxima Segurança)**
```properties
flyway.security.current.environment=production
flyway.security.validation.mode=strict
flyway.security.validation.enabled=true
```
- 🛡️ Todas as operações perigosas são bloqueadas
- 🚨 Falha imediata se detectar operação destrutiva

---

## 🎯 CENÁRIOS DE USO

### Cenário 1: Permitir uma operação específica
```properties
# Permitir apenas DROP INDEX
flyway.security.allowed-operations=DROP INDEX
```

### Cenário 2: Modo de aviso apenas
```properties
# Avisar mas não bloquear
flyway.security.validation.mode=permissive
```

### Cenário 3: Desabilitar completamente
```properties
# Desabilitar validação
flyway.security.validation.enabled=false
```

### Cenário 4: Bypass temporário
```properties
# Adicionar ambiente atual ao bypass
flyway.security.bypass.environments=production,maintenance
flyway.security.current.environment=maintenance
```

---

## 🧪 TESTANDO O SISTEMA

### 1. **Testar Bloqueio**
1. Renomeie `V99__EXAMPLE_DANGEROUS_MIGRATION.sql.disabled` para `.sql`
2. Execute a aplicação
3. Observe o erro de segurança

### 2. **Testar Modo Permissivo**
```properties
flyway.security.validation.mode=permissive
```
- Script executa com avisos

### 3. **Testar Bypass**
```properties
flyway.security.current.environment=test
flyway.security.bypass.environments=test
```
- Validação é ignorada

---

## 📊 LOGS DE EXEMPLO

### Migração Bloqueada:
```
🚨 OPERAÇÃO PERIGOSA DETECTADA na migração 'Delete old records'!
Script contém operação potencialmente destrutiva: \bDELETE\s+FROM\b
Para sua segurança, esta migração foi bloqueada.

OPÇÕES PARA RESOLVER:
1. Configure 'flyway.security.validation.enabled=false'
2. Adicione a operação em 'flyway.security.allowed-operations'
3. Configure ambiente em 'flyway.security.bypass.environments'
4. Mude para 'flyway.security.validation.mode=permissive'
```

### Migração Permitida:
```
🔍 Validando segurança da migração: Add user profile fields
✅ Migração validada com sucesso: Add user profile fields
```

### Validação Ignorada:
```
🔧 Validação de segurança ignorada para ambiente: development
```

---

## 🔧 ARQUITETURA TÉCNICA

### Componentes:

1. **`FlywaySecurityCallback`**
   - Implementa `Callback` do Flyway
   - Intercepta evento `BEFORE_EACH_MIGRATE`
   - Analisa scripts SQL com regex

2. **`FlywayConfig`**
   - Configura e registra o callback
   - Detecta tipo de banco automaticamente

3. **Configurações**
   - Propriedades em `application.properties`
   - Injeção via `@Value`

### Padrões Regex:
```java
DELETE: "\\bDELETE\\s+FROM\\b"
TRUNCATE: "\\bTRUNCATE\\s+TABLE\\b"
DROP: "\\bDROP\\s+(TABLE|DATABASE|SCHEMA|VIEW|INDEX|FUNCTION|PROCEDURE)\\b"
```

---

## ⚠️ IMPORTANTES

### 1. **Falsos Positivos**
- O sistema pode detectar operações em comentários
- Use operações explicitamente permitidas se necessário

### 2. **Limitações**
- Análise baseada em regex (não parser SQL completo)
- Não detecta operações dinâmicas ou em stored procedures

### 3. **Boas Práticas**
- Sempre teste migrações em desenvolvimento primeiro
- Use ambientes específicos para bypass controlado
- Mantenha logs de validação ativos em produção

---

## 🚀 COMANDOS DE TESTE

### Testar com Migração Perigosa:
```bash
# 1. Habilitar migração de teste
mv src/main/resources/db/migration/V99__EXAMPLE_DANGEROUS_MIGRATION.sql.disabled \
   src/main/resources/db/migration/V99__EXAMPLE_DANGEROUS_MIGRATION.sql

# 2. Executar aplicação
mvn spring-boot:run

# 3. Observar erro de segurança
```

### Testar Modo Permissivo:
```bash
# 1. Configurar modo permissivo
echo "flyway.security.validation.mode=permissive" >> src/main/resources/application.properties

# 2. Executar novamente
mvn spring-boot:run

# 3. Observar avisos ao invés de erros
```

---

## 📋 CHECKLIST DE SEGURANÇA

- [ ] Validação habilitada em produção
- [ ] Modo strict configurado para produção
- [ ] Bypass configurado apenas para desenvolvimento/teste
- [ ] Operações permitidas documentadas
- [ ] Logs de validação habilitados
- [ ] Scripts de migração testados em desenvolvimento
- [ ] Time treinado sobre o sistema de segurança

---

**🎉 Com este sistema, suas migrações estão protegidas contra operações destrutivas acidentais!** 