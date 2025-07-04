-- ===============================================================
-- EXEMPLO DE PROCEDURES ORACLE COM VERIFICAÇÕES DE EXISTÊNCIA
-- ===============================================================
-- Este arquivo contém exemplos de como criar procedures no Oracle
-- com verificações para não executar caso já existam

-- =======================================================
-- VERIFICAÇÕES DE EXISTÊNCIA (ORACLE)
-- =======================================================

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
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                created_by VARCHAR2(255) DEFAULT USER
            )';
        
        EXECUTE IMMEDIATE 'CREATE SEQUENCE system_logs_seq';
        
        EXECUTE IMMEDIATE '
            CREATE OR REPLACE TRIGGER system_logs_trigger
            BEFORE INSERT ON system_logs
            FOR EACH ROW
            BEGIN
                IF :NEW.id IS NULL THEN
                    :NEW.id := system_logs_seq.NEXTVAL;
                END IF;
            END;';
        
        DBMS_OUTPUT.PUT_LINE('Tabela system_logs criada com sucesso');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Tabela system_logs já existe');
    END IF;
END;
/

-- =======================================================
-- PROCEDURE PARA CRIAR USUÁRIO COM VALIDAÇÕES
-- =======================================================

-- Verificar se procedure existe antes de criar
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM user_procedures
    WHERE object_name = 'CREATE_USER_SAFE';
    
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE '
            CREATE OR REPLACE PROCEDURE create_user_safe(
                p_username IN VARCHAR2,
                p_email IN VARCHAR2,
                p_password IN VARCHAR2,
                p_first_name IN VARCHAR2 DEFAULT NULL,
                p_last_name IN VARCHAR2 DEFAULT NULL,
                p_result OUT VARCHAR2
            ) AS
                v_user_exists NUMBER := 0;
                v_email_exists NUMBER := 0;
                v_user_id NUMBER;
                v_role_id NUMBER;
            BEGIN
                -- Verificar se usuário já existe
                SELECT COUNT(*) INTO v_user_exists 
                FROM users 
                WHERE username = p_username;
                
                IF v_user_exists > 0 THEN
                    p_result := ''ERRO: Usuário já existe'';
                    RETURN;
                END IF;
                
                -- Verificar se email já existe
                SELECT COUNT(*) INTO v_email_exists 
                FROM users 
                WHERE email = p_email;
                
                IF v_email_exists > 0 THEN
                    p_result := ''ERRO: Email já está em uso'';
                    RETURN;
                END IF;
                
                -- Validar email
                IF NOT REGEXP_LIKE(p_email, ''^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'') THEN
                    p_result := ''ERRO: Email inválido'';
                    RETURN;
                END IF;
                
                -- Inserir usuário
                INSERT INTO users (username, email, password, first_name, last_name, enabled)
                VALUES (p_username, p_email, p_password, p_first_name, p_last_name, 1)
                RETURNING id INTO v_user_id;
                
                -- Buscar role USER
                SELECT id INTO v_role_id 
                FROM roles 
                WHERE name = ''USER''
                AND ROWNUM = 1;
                
                -- Atribuir role USER por padrão
                INSERT INTO user_roles (user_id, role_id)
                VALUES (v_user_id, v_role_id);
                
                p_result := ''SUCESSO: Usuário criado com ID '' || v_user_id;
                
                COMMIT;
                
            EXCEPTION
                WHEN OTHERS THEN
                    p_result := ''ERRO: '' || SQLERRM;
                    ROLLBACK;
            END;';
        
        DBMS_OUTPUT.PUT_LINE('Procedure create_user_safe criada com sucesso');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Procedure create_user_safe já existe');
    END IF;
END;
/

-- =======================================================
-- FUNCTION PARA VALIDAR FORÇA DA SENHA
-- =======================================================

-- Verificar se function existe antes de criar
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM user_procedures
    WHERE object_name = 'VALIDATE_PASSWORD_STRENGTH';
    
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE '
            CREATE OR REPLACE FUNCTION validate_password_strength(p_password VARCHAR2) 
            RETURN VARCHAR2 AS
                v_score NUMBER := 0;
                v_result VARCHAR2(100);
            BEGIN
                -- Verificar comprimento mínimo
                IF LENGTH(p_password) >= 8 THEN
                    v_score := v_score + 1;
                END IF;
                
                -- Verificar letras maiúsculas
                IF REGEXP_LIKE(p_password, ''[A-Z]'') THEN
                    v_score := v_score + 1;
                END IF;
                
                -- Verificar letras minúsculas
                IF REGEXP_LIKE(p_password, ''[a-z]'') THEN
                    v_score := v_score + 1;
                END IF;
                
                -- Verificar números
                IF REGEXP_LIKE(p_password, ''[0-9]'') THEN
                    v_score := v_score + 1;
                END IF;
                
                -- Verificar caracteres especiais
                IF REGEXP_LIKE(p_password, ''[^A-Za-z0-9]'') THEN
                    v_score := v_score + 1;
                END IF;
                
                -- Classificar força
                CASE 
                    WHEN v_score >= 4 THEN v_result := ''FORTE'';
                    WHEN v_score >= 3 THEN v_result := ''MODERADA'';
                    WHEN v_score >= 2 THEN v_result := ''FRACA'';
                    ELSE v_result := ''MUITO_FRACA'';
                END CASE;
                
                RETURN v_result;
            END;';
        
        DBMS_OUTPUT.PUT_LINE('Function validate_password_strength criada com sucesso');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Function validate_password_strength já existe');
    END IF;
END;
/

-- =======================================================
-- PROCEDURE PARA ATUALIZAR ÚLTIMO LOGIN
-- =======================================================

-- Verificar se procedure existe antes de criar
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM user_procedures
    WHERE object_name = 'UPDATE_LAST_LOGIN';
    
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE '
            CREATE OR REPLACE PROCEDURE update_last_login(
                p_username IN VARCHAR2,
                p_ip_address IN VARCHAR2 DEFAULT NULL,
                p_result OUT VARCHAR2
            ) AS
                v_user_id NUMBER;
                v_old_login TIMESTAMP;
            BEGIN
                -- Buscar usuário e último login
                SELECT id, last_login INTO v_user_id, v_old_login
                FROM users 
                WHERE username = p_username AND enabled = 1;
                
                -- Atualizar último login
                UPDATE users 
                SET last_login = CURRENT_TIMESTAMP
                WHERE id = v_user_id;
                
                -- Registrar na auditoria (se tabela existir)
                DECLARE
                    v_audit_exists NUMBER;
                BEGIN
                    SELECT COUNT(*) INTO v_audit_exists
                    FROM user_tables
                    WHERE table_name = ''USER_AUDIT'';
                    
                    IF v_audit_exists > 0 THEN
                        INSERT INTO user_audit (user_id, action, old_values, new_values, ip_address)
                        VALUES (v_user_id, ''LOGIN'', 
                                ''last_login: '' || TO_CHAR(v_old_login, ''YYYY-MM-DD HH24:MI:SS''),
                                ''last_login: '' || TO_CHAR(CURRENT_TIMESTAMP, ''YYYY-MM-DD HH24:MI:SS''),
                                p_ip_address);
                    END IF;
                END;
                
                p_result := ''SUCESSO: Login atualizado para '' || p_username;
                
                COMMIT;
                
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    p_result := ''ERRO: Usuário não encontrado ou inativo'';
                WHEN OTHERS THEN
                    p_result := ''ERRO: '' || SQLERRM;
                    ROLLBACK;
            END;';
        
        DBMS_OUTPUT.PUT_LINE('Procedure update_last_login criada com sucesso');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Procedure update_last_login já existe');
    END IF;
END;
/

-- =======================================================
-- PROCEDURE PARA LIMPEZA DE DADOS ANTIGOS
-- =======================================================

-- Verificar se procedure existe antes de criar
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM user_procedures
    WHERE object_name = 'CLEANUP_OLD_DATA';
    
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE '
            CREATE OR REPLACE PROCEDURE cleanup_old_data(
                p_days_old IN NUMBER DEFAULT 365,
                p_dry_run IN NUMBER DEFAULT 1,
                p_result OUT VARCHAR2
            ) AS
                v_user_count NUMBER;
                v_log_count NUMBER;
                v_audit_count NUMBER;
            BEGIN
                -- Contar usuários desabilitados antigos
                SELECT COUNT(*) INTO v_user_count
                FROM users
                WHERE enabled = 0 
                AND created_at < CURRENT_TIMESTAMP - INTERVAL p_days_old DAY;
                
                -- Contar logs antigos (se tabela existir)
                DECLARE
                    v_table_exists NUMBER;
                BEGIN
                    SELECT COUNT(*) INTO v_table_exists
                    FROM user_tables
                    WHERE table_name = ''SYSTEM_LOGS'';
                    
                    IF v_table_exists > 0 THEN
                        SELECT COUNT(*) INTO v_log_count
                        FROM system_logs
                        WHERE created_at < CURRENT_TIMESTAMP - INTERVAL p_days_old DAY;
                    ELSE
                        v_log_count := 0;
                    END IF;
                END;
                
                -- Contar registros de auditoria antigos (se tabela existir)
                DECLARE
                    v_table_exists NUMBER;
                BEGIN
                    SELECT COUNT(*) INTO v_table_exists
                    FROM user_tables
                    WHERE table_name = ''USER_AUDIT'';
                    
                    IF v_table_exists > 0 THEN
                        SELECT COUNT(*) INTO v_audit_count
                        FROM user_audit
                        WHERE change_date < CURRENT_TIMESTAMP - INTERVAL p_days_old DAY;
                    ELSE
                        v_audit_count := 0;
                    END IF;
                END;
                
                IF p_dry_run = 1 THEN
                    p_result := ''DRY RUN: Seria removido - '' || 
                               v_user_count || '' usuários, '' ||
                               v_log_count || '' logs, '' ||
                               v_audit_count || '' registros de auditoria'';
                ELSE
                    -- Executar limpeza real
                    DELETE FROM users
                    WHERE enabled = 0 
                    AND created_at < CURRENT_TIMESTAMP - INTERVAL p_days_old DAY;
                    
                    -- Limpar logs se tabela existir
                    DECLARE
                        v_table_exists NUMBER;
                    BEGIN
                        SELECT COUNT(*) INTO v_table_exists
                        FROM user_tables
                        WHERE table_name = ''SYSTEM_LOGS'';
                        
                        IF v_table_exists > 0 THEN
                            DELETE FROM system_logs
                            WHERE created_at < CURRENT_TIMESTAMP - INTERVAL p_days_old DAY;
                        END IF;
                    END;
                    
                    -- Limpar auditoria se tabela existir
                    DECLARE
                        v_table_exists NUMBER;
                    BEGIN
                        SELECT COUNT(*) INTO v_table_exists
                        FROM user_tables
                        WHERE table_name = ''USER_AUDIT'';
                        
                        IF v_table_exists > 0 THEN
                            DELETE FROM user_audit
                            WHERE change_date < CURRENT_TIMESTAMP - INTERVAL p_days_old DAY;
                        END IF;
                    END;
                    
                    p_result := ''SUCESSO: Removidos '' || 
                               v_user_count || '' usuários, '' ||
                               v_log_count || '' logs, '' ||
                               v_audit_count || '' registros de auditoria'';
                    
                    COMMIT;
                END IF;
                
            EXCEPTION
                WHEN OTHERS THEN
                    p_result := ''ERRO: '' || SQLERRM;
                    ROLLBACK;
            END;';
        
        DBMS_OUTPUT.PUT_LINE('Procedure cleanup_old_data criada com sucesso');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Procedure cleanup_old_data já existe');
    END IF;
END;
/

-- =======================================================
-- TRIGGER PARA AUDITORIA (APENAS SE TABELA EXISTIR)
-- =======================================================

-- Verificar se trigger existe antes de criar
DECLARE
    v_count NUMBER;
    v_table_exists NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM user_triggers
    WHERE trigger_name = 'USER_AUDIT_TRIGGER';
    
    SELECT COUNT(*) INTO v_table_exists
    FROM user_tables
    WHERE table_name = 'USER_AUDIT';
    
    IF v_count = 0 AND v_table_exists > 0 THEN
        EXECUTE IMMEDIATE '
            CREATE OR REPLACE TRIGGER user_audit_trigger
                AFTER INSERT OR UPDATE OR DELETE ON users
                FOR EACH ROW
            DECLARE
                v_action VARCHAR2(10);
                v_old_values CLOB;
                v_new_values CLOB;
            BEGIN
                IF INSERTING THEN
                    v_action := ''INSERT'';
                    v_new_values := ''username: '' || :NEW.username || 
                                   '', email: '' || :NEW.email ||
                                   '', enabled: '' || :NEW.enabled;
                ELSIF UPDATING THEN
                    v_action := ''UPDATE'';
                    v_old_values := ''username: '' || :OLD.username || 
                                   '', email: '' || :OLD.email ||
                                   '', enabled: '' || :OLD.enabled;
                    v_new_values := ''username: '' || :NEW.username || 
                                   '', email: '' || :NEW.email ||
                                   '', enabled: '' || :NEW.enabled;
                ELSIF DELETING THEN
                    v_action := ''DELETE'';
                    v_old_values := ''username: '' || :OLD.username || 
                                   '', email: '' || :OLD.email ||
                                   '', enabled: '' || :OLD.enabled;
                END IF;
                
                INSERT INTO user_audit (user_id, action, old_values, new_values)
                VALUES (COALESCE(:NEW.id, :OLD.id), v_action, v_old_values, v_new_values);
                
            EXCEPTION
                WHEN OTHERS THEN
                    NULL; -- Não bloquear operação principal
            END;';
        
        DBMS_OUTPUT.PUT_LINE('Trigger user_audit_trigger criado com sucesso');
    ELSE
        IF v_count > 0 THEN
            DBMS_OUTPUT.PUT_LINE('Trigger user_audit_trigger já existe');
        ELSE
            DBMS_OUTPUT.PUT_LINE('Tabela user_audit não existe - trigger não criado');
        END IF;
    END IF;
END;
/

-- =======================================================
-- EXEMPLOS DE USO
-- =======================================================

-- Exemplo de uso das procedures:
--
-- DECLARE
--     v_result VARCHAR2(500);
-- BEGIN
--     -- Criar usuário
--     create_user_safe('teste', 'teste@email.com', 'MinhaSenh@123', 'João', 'Silva', v_result);
--     DBMS_OUTPUT.PUT_LINE(v_result);
--     
--     -- Atualizar login
--     update_last_login('teste', '192.168.1.1', v_result);
--     DBMS_OUTPUT.PUT_LINE(v_result);
--     
--     -- Testar força de senha
--     DBMS_OUTPUT.PUT_LINE('Força da senha: ' || validate_password_strength('MinhaSenh@123'));
--     
--     -- Limpeza (dry run)
--     cleanup_old_data(365, 1, v_result);
--     DBMS_OUTPUT.PUT_LINE(v_result);
-- END;
-- /

-- =======================================================
-- DOCUMENTAÇÃO
-- =======================================================

-- Este arquivo demonstra como:
-- 1. Verificar se objetos existem antes de criá-los
-- 2. Criar procedures com validações robustas
-- 3. Usar EXECUTE IMMEDIATE para criação dinâmica
-- 4. Implementar tratamento de erros adequado
-- 5. Fazer verificações condicionais de tabelas
-- 6. Criar triggers que só funcionam se dependências existirem

-- Para usar este código:
-- 1. Execute em um banco Oracle
-- 2. Ajuste os nomes das tabelas conforme necessário
-- 3. Teste as procedures com dados de exemplo
-- 4. Monitore os logs para verificar se tudo foi criado corretamente

DBMS_OUTPUT.PUT_LINE('=== EXEMPLO DE PROCEDURES ORACLE CONCLUÍDO ==='); 