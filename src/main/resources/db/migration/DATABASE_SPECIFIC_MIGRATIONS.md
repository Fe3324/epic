# Migrações Específicas por Banco de Dados

Este projeto implementa um sistema inteligente de migrações que **detecta automaticamente** o tipo de banco de dados e executa apenas as migrações compatíveis.

## 🚀 Como Funciona

### **Detecção Automática**
O sistema detecta automaticamente o banco através da classe `FlywayConfig.java`:

```java
DatabaseType databaseType = detectDatabaseType();
List<String> locations = buildMigrationLocations(databaseType);
```

### **Estrutura de Diretórios**
```
src/main/resources/db/migration/
├── V1__Create_initial_schema.sql      # Migrações comuns (todos os bancos)
├── V2__Insert_initial_data.sql        # Migrações comuns
├── V3__Add_user_profile_fields.sql    # Migrações comuns
├── V4__Add_procedures_and_validations.sql # Migrações comuns
├── oracle/                            # Migrações específicas Oracle
│   └── V10__Oracle_procedures.sql
├── h2/                                # Migrações específicas H2
│   └── V11__H2_specific_features.sql
├── mysql/                             # Migrações específicas MySQL (futuro)
└── postgresql/                        # Migrações específicas PostgreSQL (futuro)
```

## 🎯 Bancos Suportados

| Banco | Status | Detecção | Migrações Específicas |
|-------|--------|----------|----------------------|
| **H2** | ✅ Ativo | `h2` | `/h2/` |
| **Oracle** | ✅ Ativo | `oracle` | `/oracle/` |
| **MySQL** | 🔧 Planejado | `mysql` | `/mysql/` |
| **PostgreSQL** | 🔧 Planejado | `postgresql` | `/postgresql/` |

## 📋 Configuração Automática

### **H2 Database (Atual)**
```properties
# Configuração automática para H2
spring.datasource.url=jdbc:h2:file:./data/testdb
spring.datasource.driverClassName=org.h2.Driver

# Migrações executadas:
# - Todas as migrações comuns (V1, V2, V3, V4)
# - Migrações H2 específicas (V11)
```

### **Oracle Database**
```properties
# Para usar Oracle, configure:
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
spring.datasource.driverClassName=oracle.jdbc.OracleDriver

# Migrações executadas:
# - Todas as migrações comuns (V1, V2, V3, V4)  
# - Migrações Oracle específicas (V10)
```

## 🔄 Como Alternar Entre Bancos

### **Método 1: Profiles do Spring**
```bash
# Usar H2 (padrão)
mvn spring-boot:run

# Usar Oracle
mvn spring-boot:run -Dspring-boot.run.profiles=oracle

# Ou usando arquivo de perfil
mvn spring-boot:run --spring.profiles.active=oracle
```

### **Método 2: Configuração Manual**
1. **Comente** as configurações do H2 no `application.properties`
2. **Descomente** as configurações do Oracle
3. **Execute** a aplicação normalmente

### **Método 3: Variáveis de Ambiente**
```bash
export DB_URL="jdbc:oracle:thin:@localhost:1521:XE"
export DB_DRIVER="oracle.jdbc.OracleDriver"
export DB_USERNAME="epic_user"
export DB_PASSWORD="epic_password"

mvn spring-boot:run
```

## 📝 Logs de Detecção

Quando a aplicação inicia, você verá logs como:

```
=== DETECÇÃO DO BANCO DE DADOS ===
Produto: h2
Versão: 2.1.214
Driver: H2 JDBC Driver
URL: jdbc:h2:file:./data/testdb
Banco detectado: H2

=== CONFIGURAÇÃO DO FLYWAY ===
Tipo de banco: H2
Locais de migração:
  - classpath:db/migration
  - classpath:db/migration/h2
  - classpath:db/migration/h2-specific
================================
```

## 🔧 Migrações por Banco

### **H2 Database (V11)**
- ✅ Functions Java customizadas (`CREATE ALIAS`)
- ✅ Cache interno otimizado
- ✅ Backup automático
- ✅ Compactação de dados
- ✅ Views com `H2VERSION()`

### **Oracle Database (V10)**
- ✅ Procedures nativas PL/SQL
- ✅ Functions Oracle (`REGEXP_LIKE`, `LISTAGG`)
- ✅ Packages complexos
- ✅ Triggers avançados
- ✅ Auditoria com `SYS_CONTEXT`

### **Comuns (V1-V4)**
- ✅ Schema básico (users, roles, addresses)
- ✅ Dados iniciais
- ✅ Campos de perfil
- ✅ Views e índices genéricos

## 🛠️ Adicionando Novos Bancos

### **1. Configurar Detecção**
Edite `FlywayConfig.java`:

```java
if (databaseProductName.contains("mysql")) {
    return DatabaseType.MYSQL;
}
```

### **2. Criar Diretório**
```bash
mkdir src/main/resources/db/migration/mysql
```

### **3. Adicionar Localização**
```java
case MYSQL:
    locations.add("classpath:db/migration/mysql");
    break;
```

### **4. Criar Migração Específica**
```sql
-- V12__MySQL_specific_features.sql
SELECT 'MySQL Database detectado' as status;

-- Procedures MySQL específicas aqui...
```

## 📊 Exemplos de Uso

### **H2 - Functions Java**
```sql
-- Validar CPF brasileiro
SELECT validate_cpf('12345678901') as cpf_valido;

-- Formatar telefone  
SELECT format_phone('11999999999') as telefone_formatado;

-- Calcular idade
SELECT calculate_age('1990-05-15') as idade;
```

### **Oracle - Procedures Nativas**
```sql
-- Criar usuário com validações
DECLARE
    v_result VARCHAR2(500);
BEGIN
    create_user_oracle('novo_user', 'email@teste.com', 'MinhaSenh@123', 'João', 'Silva', v_result);
    DBMS_OUTPUT.PUT_LINE(v_result);
END;
/

-- Relatório avançado
DECLARE
    v_cursor SYS_REFCURSOR;
BEGIN
    generate_user_report_oracle(v_cursor, 1);
    -- Processar cursor...
END;
/
```

## ⚠️ Vantagens do Sistema

### **1. Flexibilidade Total**
- ✅ Suporte a múltiplos bancos
- ✅ Detecção automática
- ✅ Migrações específicas por banco
- ✅ Fallback para migrações genéricas

### **2. Desenvolvimento Ágil**
- ✅ Desenvolva no H2 (rápido)
- ✅ Deploy no Oracle (produção)
- ✅ Sem alteração de código
- ✅ Migrações específicas automáticas

### **3. Manutenção Simplificada**
- ✅ Migrações organizadas por banco
- ✅ Logs detalhados de execução
- ✅ Validações automáticas
- ✅ Rollback seguro

## 🚨 Considerações Importantes

### **Ordem de Numeração**
- **V1-V9**: Migrações comuns
- **V10+**: Migrações específicas Oracle
- **V11+**: Migrações específicas H2
- **V12+**: Migrações específicas MySQL (futuro)

### **Compatibilidade**
- Migrações comuns devem ser **compatíveis** com todos os bancos
- Migrações específicas podem usar **recursos nativos**
- Teste sempre em **ambiente similar** à produção

### **Backup e Rollback**
- Sempre faça **backup** antes de migrar
- Teste migrações em **ambiente de desenvolvimento**
- Use **dry-run** quando disponível

## 🔍 Troubleshooting

### **Erro: "Migration not found"**
- Verifique se o diretório específico existe
- Confirme a numeração das migrações
- Valide a detecção do banco nos logs

### **Erro: "Database not detected"**
- Verifique a string de conexão
- Confirme o driver do banco
- Veja os logs de detecção

### **Erro: "Migration failed"**
- Verifique compatibilidade da sintaxe SQL
- Confirme se está no diretório correto
- Teste a migração manualmente

## 📞 Suporte

Para dúvidas ou problemas:
1. Verifique os **logs de detecção**
2. Confirme a **estrutura de diretórios**
3. Teste com **migrações simples** primeiro
4. Consulte a **documentação específica** do banco

---

**Resultado:** Sistema inteligente que executa automaticamente as migrações corretas para cada tipo de banco! 🎉 