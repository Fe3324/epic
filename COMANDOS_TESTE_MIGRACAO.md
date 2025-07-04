# 🧪 Comandos para Testar Migrações Específicas

## 🎯 **Resumo da Implementação**

Implementamos um sistema que **detecta automaticamente** o tipo de banco e executa **apenas as migrações compatíveis**:

- ✅ **H2**: Executa V1-V4 (comuns) + V11 (H2 específico)
- ✅ **Oracle**: Executa V1-V4 (comuns) + V10 (Oracle específico)
- ✅ **Procedures com verificações** de existência
- ✅ **Detecção automática** via `FlywayConfig.java`

---

## 🔧 **Comandos de Teste**

### **1. Testar com H2 (Atual)**

```bash
# Executar com H2 (padrão)
mvn spring-boot:run

# Ou executar em background para ver logs
mvn spring-boot:run > logs_h2.txt 2>&1 &

# Ver logs de detecção
tail -f logs_h2.txt | grep -E "(DETECÇÃO|CONFIGURAÇÃO|Banco detectado)"
```

**Resultado esperado:**
```
=== DETECÇÃO DO BANCO DE DADOS ===
Produto: h2
Banco detectado: H2
=== CONFIGURAÇÃO DO FLYWAY ===
Tipo de banco: H2
Locais de migração:
  - classpath:db/migration
  - classpath:db/migration/h2
```

### **2. Testar com Oracle (Simulado)**

```bash
# OPÇÃO A: Usando profile Oracle
mvn spring-boot:run --spring.profiles.active=oracle

# OPÇÃO B: Configurando manualmente no application.properties
# (comente H2 e descomente Oracle)

# OPÇÃO C: Usando variáveis de ambiente
export DB_URL="jdbc:oracle:thin:@localhost:1521:XE"
export DB_DRIVER="oracle.jdbc.OracleDriver"
mvn spring-boot:run
```

**Resultado esperado:**
```
=== DETECÇÃO DO BANCO DE DADOS ===
Produto: oracle
Banco detectado: ORACLE
=== CONFIGURAÇÃO DO FLYWAY ===
Tipo de banco: ORACLE
Locais de migração:
  - classpath:db/migration
  - classpath:db/migration/oracle
```

---

## 🔍 **Verificações após Execução**

### **A. Verificar Migrações Aplicadas**

```bash
# Conectar ao H2 Console
# URL: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:file:./data/testdb
# User: sa
# Password: password
```

```sql
-- Ver todas as migrações aplicadas
SELECT version, description, type, script, installed_on, success 
FROM flyway_schema_history 
ORDER BY installed_rank;

-- Verificar se V11 (H2 específico) foi executado
SELECT * FROM flyway_schema_history WHERE version = '11';

-- Verificar se V10 (Oracle específico) NÃO foi executado
SELECT * FROM flyway_schema_history WHERE version = '10';
```

### **B. Verificar Tables/Views Criadas**

```sql
-- Verificar se tabelas H2 específicas existem
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_NAME IN ('H2_CACHE_DATA', 'APP_SETTINGS');

-- Verificar se views H2 específicas existem
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.VIEWS 
WHERE TABLE_NAME LIKE 'V_H2_%';

-- Ver todas as views criadas
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.VIEWS 
ORDER BY TABLE_NAME;
```

### **C. Verificar Functions/Aliases H2**

```sql
-- Ver functions Java criadas para H2
SELECT ALIAS_NAME FROM INFORMATION_SCHEMA.FUNCTION_ALIASES 
WHERE ALIAS_NAME LIKE '%validate%' OR ALIAS_NAME LIKE '%format%';

-- Testar functions específicas do H2
SELECT 'CPF válido' as teste, validate_cpf('12345678901') as resultado;
SELECT 'Telefone formatado' as teste, format_phone('11999999999') as resultado;
SELECT 'Idade calculada' as teste, calculate_age('1990-05-15') as resultado;
```

### **D. Verificar Dados Específicos H2**

```sql
-- Ver configurações H2 inseridas
SELECT * FROM app_settings WHERE setting_key LIKE 'h2.%';

-- Ver cache H2 inicial
SELECT * FROM h2_cache_data WHERE cache_type = 'SYSTEM';

-- Ver estatísticas H2
SELECT * FROM v_h2_statistics;
```

---

## 🔧 **Comandos de Compilação e Build**

```bash
# Compilar projeto
mvn compile

# Executar testes
mvn test

# Gerar package
mvn package

# Limpar e recompilar
mvn clean compile

# Executar com profile específico
mvn spring-boot:run -Dspring-boot.run.profiles=oracle
```

---

## 📊 **Validações de Funcionamento**

### **1. Logs de Startup**

Procure nos logs por:
```
=== DETECÇÃO DO BANCO DE DADOS ===
=== CONFIGURAÇÃO DO FLYWAY ===
Migração V4 concluída: Procedures e validações adicionadas com sucesso!
Migração H2 V11 concluída - Funcionalidades específicas do H2 implementadas!
```

### **2. Queries de Validação**

```sql
-- Contar migrações executadas
SELECT COUNT(*) as total_migracoes FROM flyway_schema_history WHERE success = true;

-- Ver última migração
SELECT version, description, installed_on 
FROM flyway_schema_history 
ORDER BY installed_rank DESC 
LIMIT 1;

-- Verificar integridade dos dados
SELECT 
    (SELECT COUNT(*) FROM users) as usuarios,
    (SELECT COUNT(*) FROM roles) as roles,
    (SELECT COUNT(*) FROM addresses) as enderecos,
    (SELECT COUNT(*) FROM system_logs) as logs;
```

### **3. Testar Funcionalidades Específicas**

```sql
-- H2: Testar views específicas
SELECT categoria, metrica, valor_texto 
FROM v_h2_statistics 
WHERE categoria = 'Sistema';

-- H2: Testar functions Java (se implementadas)
-- SELECT validate_cpf('12345678901') as cpf_valido;

-- Comum: Testar views gerais
SELECT * FROM v_users_complete LIMIT 5;
SELECT * FROM v_system_statistics;
```

---

## 🚨 **Troubleshooting**

### **Erro: "FlywayConfig não encontrado"**
```bash
# Verificar se arquivo existe
ls -la src/main/java/com/app/epic/config/FlywayConfig.java

# Recompilar
mvn clean compile
```

### **Erro: "Migração não encontrada"**
```bash
# Verificar estrutura de diretórios
ls -la src/main/resources/db/migration/
ls -la src/main/resources/db/migration/h2/
ls -la src/main/resources/db/migration/oracle/
```

### **Erro: "Banco não detectado"**
```bash
# Ver logs completos
mvn spring-boot:run --debug > debug.log 2>&1

# Procurar por logs de detecção
grep -A 5 -B 5 "DETECÇÃO DO BANCO" debug.log
```

### **Erro: "Function não encontrada"**
- Verifique se as classes Java das functions estão implementadas
- As functions H2 são apenas ALIAs, as implementações viriam depois

---

## ✅ **Checklist de Verificação**

### **Arquivos Criados:**
- [ ] `FlywayConfig.java` - Detecção automática
- [ ] `application-oracle.properties` - Configuração Oracle
- [ ] `V10__Oracle_procedures.sql` - Procedures Oracle
- [ ] `V11__H2_specific_features.sql` - Features H2
- [ ] Documentações em `.md`

### **Funcionalidades:**
- [ ] Detecção automática do banco funciona
- [ ] Migrações H2 específicas executam
- [ ] Migrações Oracle específicas não executam no H2
- [ ] Views específicas são criadas
- [ ] Logs de detecção aparecem
- [ ] Estrutura de diretórios está correta

### **Testes:**
- [ ] Compilação sem erros
- [ ] Execução da aplicação sem falhas
- [ ] Migrações aplicadas corretamente
- [ ] Dados inseridos nas tabelas
- [ ] Views funcionando
- [ ] Console H2 acessível

---

## 🎉 **Resultado Final**

**Se tudo funcionar corretamente, você terá:**

1. ✅ **Sistema que detecta automaticamente** H2 vs Oracle
2. ✅ **Migrações específicas** executadas apenas no banco correto
3. ✅ **Procedures com verificações** que não falham se já existem
4. ✅ **Estrutura escalável** para adicionar novos bancos
5. ✅ **Zero configuração manual** necessária

**Comando final de teste:**
```bash
mvn spring-boot:run && echo "🎉 Sistema funcionando perfeitamente!"
``` 