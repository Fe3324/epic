# Exemplos de Procedures Oracle com Verificações

Este arquivo contém exemplos práticos de como criar procedures no Oracle Database com verificações de existência, para evitar erros de execução.

## ✅ Principais Recursos Demonstrados

### 1. **Verificações de Existência**
- Verificar se tabelas existem antes de criar
- Verificar se procedures/functions existem antes de recriar
- Verificar se triggers existem antes de criar

### 2. **Procedures com Validações**
- Criação de usuários com validações completas
- Atualização de último login com auditoria
- Limpeza de dados antigos com modo dry-run

### 3. **Functions Úteis**
- Validação de força de senha
- Formatação de dados
- Cálculos customizados

### 4. **Triggers Condicionais**
- Triggers que só funcionam se dependências existirem
- Auditoria automática
- Logs de sistema

## 📋 Exemplo 1: Verificação de Tabela

```sql
-- Verificar se tabela existe antes de criar
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM user_tables
    WHERE table_name = 'SYSTEM_LOGS';
    
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE '
            CREATE TABLE system_logs (
                id NUMBER PRIMARY KEY,
                log_level VARCHAR2(10) NOT NULL,
                message VARCHAR2(1000) NOT NULL,
                details CLOB,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )';
        
        DBMS_OUTPUT.PUT_LINE('Tabela system_logs criada');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Tabela system_logs já existe');
    END IF;
END;
/
```

## 📋 Exemplo 2: Procedure com Validações

```sql
-- Procedure para criar usuário com validações
CREATE OR REPLACE PROCEDURE create_user_safe(
    p_username IN VARCHAR2,
    p_email IN VARCHAR2,
    p_password IN VARCHAR2,
    p_result OUT VARCHAR2
) AS
    v_user_exists NUMBER := 0;
    v_email_exists NUMBER := 0;
    v_user_id NUMBER;
BEGIN
    -- Verificar se usuário já existe
    SELECT COUNT(*) INTO v_user_exists 
    FROM users 
    WHERE username = p_username;
    
    IF v_user_exists > 0 THEN
        p_result := 'ERRO: Usuário já existe';
        RETURN;
    END IF;
    
    -- Verificar se email já existe
    SELECT COUNT(*) INTO v_email_exists 
    FROM users 
    WHERE email = p_email;
    
    IF v_email_exists > 0 THEN
        p_result := 'ERRO: Email já está em uso';
        RETURN;
    END IF;
    
    -- Validar formato do email
    IF NOT REGEXP_LIKE(p_email, '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$') THEN
        p_result := 'ERRO: Email inválido';
        RETURN;
    END IF;
    
    -- Inserir usuário
    INSERT INTO users (username, email, password, enabled)
    VALUES (p_username, p_email, p_password, 1)
    RETURNING id INTO v_user_id;
    
    p_result := 'SUCESSO: Usuário criado com ID ' || v_user_id;
    
    COMMIT;
    
EXCEPTION
    WHEN OTHERS THEN
        p_result := 'ERRO: ' || SQLERRM;
        ROLLBACK;
END;
/
```

## 📋 Exemplo 3: Function para Validação

```sql
-- Function para validar força da senha
CREATE OR REPLACE FUNCTION validate_password_strength(p_password VARCHAR2) 
RETURN VARCHAR2 AS
    v_score NUMBER := 0;
BEGIN
    -- Verificar comprimento mínimo
    IF LENGTH(p_password) >= 8 THEN
        v_score := v_score + 1;
    END IF;
    
    -- Verificar letras maiúsculas
    IF REGEXP_LIKE(p_password, '[A-Z]') THEN
        v_score := v_score + 1;
    END IF;
    
    -- Verificar números
    IF REGEXP_LIKE(p_password, '[0-9]') THEN
        v_score := v_score + 1;
    END IF;
    
    -- Verificar caracteres especiais
    IF REGEXP_LIKE(p_password, '[^A-Za-z0-9]') THEN
        v_score := v_score + 1;
    END IF;
    
    -- Retornar classificação
    RETURN CASE 
        WHEN v_score >= 4 THEN 'FORTE'
        WHEN v_score >= 3 THEN 'MODERADA'
        WHEN v_score >= 2 THEN 'FRACA'
        ELSE 'MUITO_FRACA'
    END;
END;
/
```

## 📋 Exemplo 4: Trigger Condicional

```sql
-- Trigger que só funciona se tabela de auditoria existir
CREATE OR REPLACE TRIGGER user_audit_trigger
    AFTER INSERT OR UPDATE OR DELETE ON users
    FOR EACH ROW
DECLARE
    v_table_exists NUMBER := 0;
    v_action VARCHAR2(10);
BEGIN
    -- Verificar se tabela de auditoria existe
    SELECT COUNT(*) INTO v_table_exists
    FROM user_tables
    WHERE table_name = 'USER_AUDIT';
    
    IF v_table_exists > 0 THEN
        -- Determinar ação
        IF INSERTING THEN
            v_action := 'INSERT';
        ELSIF UPDATING THEN
            v_action := 'UPDATE';
        ELSIF DELETING THEN
            v_action := 'DELETE';
        END IF;
        
        -- Inserir log de auditoria
        INSERT INTO user_audit (user_id, action, change_date)
        VALUES (COALESCE(:NEW.id, :OLD.id), v_action, CURRENT_TIMESTAMP);
    END IF;
    
EXCEPTION
    WHEN OTHERS THEN
        NULL; -- Não bloquear operação principal
END;
/
```

## 📋 Exemplo 5: Procedure de Manutenção

```sql
-- Procedure para limpeza com dry-run
CREATE OR REPLACE PROCEDURE cleanup_old_data(
    p_days_old IN NUMBER DEFAULT 365,
    p_dry_run IN NUMBER DEFAULT 1,
    p_result OUT VARCHAR2
) AS
    v_count NUMBER;
BEGIN
    -- Contar registros que seriam removidos
    SELECT COUNT(*) INTO v_count
    FROM users
    WHERE enabled = 0 
    AND created_at < CURRENT_TIMESTAMP - INTERVAL p_days_old DAY;
    
    IF p_dry_run = 1 THEN
        p_result := 'DRY RUN: Seria removido ' || v_count || ' usuários';
    ELSE
        -- Executar limpeza real
        DELETE FROM users
        WHERE enabled = 0 
        AND created_at < CURRENT_TIMESTAMP - INTERVAL p_days_old DAY;
        
        p_result := 'SUCESSO: Removidos ' || v_count || ' usuários';
        COMMIT;
    END IF;
    
EXCEPTION
    WHEN OTHERS THEN
        p_result := 'ERRO: ' || SQLERRM;
        ROLLBACK;
END;
/
```

## 🚀 Como Usar

### 1. **Testar as Procedures**
```sql
DECLARE
    v_result VARCHAR2(500);
BEGIN
    -- Criar usuário
    create_user_safe('teste', 'teste@email.com', 'MinhaSenh@123', v_result);
    DBMS_OUTPUT.PUT_LINE(v_result);
    
    -- Testar força de senha
    DBMS_OUTPUT.PUT_LINE('Força: ' || validate_password_strength('MinhaSenh@123'));
    
    -- Limpeza (dry run)
    cleanup_old_data(365, 1, v_result);
    DBMS_OUTPUT.PUT_LINE(v_result);
END;
/
```

### 2. **Verificar Objetos Criados**
```sql
-- Listar procedures criadas
SELECT object_name, object_type, status
FROM user_objects
WHERE object_type IN ('PROCEDURE', 'FUNCTION', 'TRIGGER')
ORDER BY object_name;
```

### 3. **Monitorar Logs**
```sql
-- Ver logs do sistema
SELECT * FROM system_logs
ORDER BY created_at DESC;

-- Ver auditoria de usuários
SELECT * FROM user_audit
ORDER BY change_date DESC;
```

## ⚠️ Vantagens das Verificações

1. **Evita Erros**: Não falha se objeto já existe
2. **Reutilizável**: Pode executar múltiplas vezes
3. **Segurança**: Validações robustas antes de executar
4. **Manutenção**: Procedures para limpeza automática
5. **Auditoria**: Logs completos das operações

## 📝 Adaptação para seu Projeto

Para adaptar estes exemplos ao seu projeto Epic:

1. **Ajuste os nomes das tabelas** conforme seu schema
2. **Modifique as validações** conforme suas regras de negócio
3. **Adicione campos específicos** do seu domínio
4. **Implemente logs** conforme sua necessidade
5. **Teste em ambiente** de desenvolvimento primeiro

## 🔧 Integração com Flyway

Para usar com Flyway, coloque estes códigos em migrações:

```sql
-- V4__Create_procedures.sql
-- Seus procedures aqui...

-- V5__Create_functions.sql  
-- Suas functions aqui...

-- V6__Create_triggers.sql
-- Seus triggers aqui...
```

Dessa forma, o Flyway aplicará as procedures automaticamente quando a aplicação subir! 