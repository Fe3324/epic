-- Migração específica para Oracle Database
-- Esta migração só será executada quando conectado ao Oracle
-- Versão: V10 - Oracle Procedures

-- =======================================================
-- VERIFICAÇÃO DO BANCO DE DADOS
-- =======================================================
-- Esta migração só funciona no Oracle
SELECT 'Oracle Database detectado - executando procedures específicas' as status FROM dual;

-- =======================================================
-- PROCEDURES NATIVAS DO ORACLE
-- =======================================================

-- Procedure para criar usuário com validações Oracle
CREATE OR REPLACE PROCEDURE create_user_oracle(
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
    v_password_strength VARCHAR2(20);
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
    
    -- Validar email usando Oracle REGEXP
    IF NOT REGEXP_LIKE(p_email, '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$') THEN
        p_result := 'ERRO: Email inválido';
        RETURN;
    END IF;
    
    -- Validar força da senha
    v_password_strength := validate_password_strength_oracle(p_password);
    
    IF v_password_strength = 'MUITO_FRACA' THEN
        p_result := 'ERRO: Senha muito fraca';
        RETURN;
    END IF;
    
    -- Inserir usuário
    INSERT INTO users (username, email, password, first_name, last_name, enabled)
    VALUES (p_username, p_email, p_password, p_first_name, p_last_name, 1)
    RETURNING id INTO v_user_id;
    
    -- Buscar role USER
    SELECT id INTO v_role_id 
    FROM roles 
    WHERE name = 'USER'
    AND ROWNUM = 1;
    
    -- Atribuir role USER por padrão
    INSERT INTO user_roles (user_id, role_id)
    VALUES (v_user_id, v_role_id);
    
    p_result := 'SUCESSO: Usuário criado com ID ' || v_user_id || ' (força senha: ' || v_password_strength || ')';
    
    COMMIT;
    
EXCEPTION
    WHEN OTHERS THEN
        p_result := 'ERRO: ' || SQLERRM;
        ROLLBACK;
END;
/

-- Function Oracle para validar força da senha
CREATE OR REPLACE FUNCTION validate_password_strength_oracle(p_password VARCHAR2) 
RETURN VARCHAR2 AS
    v_score NUMBER := 0;
BEGIN
    -- Comprimento mínimo
    IF LENGTH(p_password) >= 8 THEN
        v_score := v_score + 1;
    END IF;
    
    -- Letras maiúsculas
    IF REGEXP_LIKE(p_password, '[A-Z]') THEN
        v_score := v_score + 1;
    END IF;
    
    -- Letras minúsculas
    IF REGEXP_LIKE(p_password, '[a-z]') THEN
        v_score := v_score + 1;
    END IF;
    
    -- Números
    IF REGEXP_LIKE(p_password, '[0-9]') THEN
        v_score := v_score + 1;
    END IF;
    
    -- Caracteres especiais
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

-- Procedure Oracle para relatório avançado
CREATE OR REPLACE PROCEDURE generate_user_report_oracle(
    p_cursor OUT SYS_REFCURSOR,
    p_include_inactive IN NUMBER DEFAULT 0
) AS
BEGIN
    IF p_include_inactive = 1 THEN
        OPEN p_cursor FOR
            SELECT 
                u.id,
                u.username,
                u.email,
                u.first_name || ' ' || u.last_name as full_name,
                u.phone,
                u.enabled,
                u.created_at,
                u.last_login,
                COUNT(a.id) as address_count,
                LISTAGG(r.name, ', ') WITHIN GROUP (ORDER BY r.name) as roles,
                CASE 
                    WHEN u.enabled = 0 THEN 'DESABILITADO'
                    WHEN u.last_login IS NULL THEN 'NUNCA_LOGOU'
                    WHEN u.last_login < CURRENT_TIMESTAMP - INTERVAL '30' DAY THEN 'INATIVO'
                    WHEN u.last_login < CURRENT_TIMESTAMP - INTERVAL '7' DAY THEN 'POUCO_ATIVO'
                    ELSE 'ATIVO'
                END as status
            FROM users u
            LEFT JOIN addresses a ON u.id = a.user_id
            LEFT JOIN user_roles ur ON u.id = ur.user_id
            LEFT JOIN roles r ON ur.role_id = r.id
            GROUP BY u.id, u.username, u.email, u.first_name, u.last_name, 
                     u.phone, u.enabled, u.created_at, u.last_login
            ORDER BY u.created_at DESC;
    ELSE
        OPEN p_cursor FOR
            SELECT 
                u.id,
                u.username,
                u.email,
                u.first_name || ' ' || u.last_name as full_name,
                u.phone,
                u.enabled,
                u.created_at,
                u.last_login,
                COUNT(a.id) as address_count,
                LISTAGG(r.name, ', ') WITHIN GROUP (ORDER BY r.name) as roles,
                CASE 
                    WHEN u.last_login IS NULL THEN 'NUNCA_LOGOU'
                    WHEN u.last_login < CURRENT_TIMESTAMP - INTERVAL '30' DAY THEN 'INATIVO'
                    WHEN u.last_login < CURRENT_TIMESTAMP - INTERVAL '7' DAY THEN 'POUCO_ATIVO'
                    ELSE 'ATIVO'
                END as status
            FROM users u
            LEFT JOIN addresses a ON u.id = a.user_id
            LEFT JOIN user_roles ur ON u.id = ur.user_id
            LEFT JOIN roles r ON ur.role_id = r.id
            WHERE u.enabled = 1
            GROUP BY u.id, u.username, u.email, u.first_name, u.last_name, 
                     u.phone, u.enabled, u.created_at, u.last_login
            ORDER BY u.created_at DESC;
    END IF;
END;
/

-- Package Oracle para estatísticas avançadas
CREATE OR REPLACE PACKAGE stats_oracle_pkg AS
    PROCEDURE get_detailed_stats(
        p_cursor OUT SYS_REFCURSOR
    );
    
    FUNCTION get_user_activity_score(p_user_id NUMBER) RETURN NUMBER;
    
    PROCEDURE cleanup_old_data_oracle(
        p_days_old IN NUMBER DEFAULT 365,
        p_dry_run IN NUMBER DEFAULT 1,
        p_result OUT VARCHAR2
    );
END stats_oracle_pkg;
/

CREATE OR REPLACE PACKAGE BODY stats_oracle_pkg AS
    
    PROCEDURE get_detailed_stats(
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT 
                'Usuários' as categoria,
                'Total' as metrica,
                COUNT(*) as valor
            FROM users
            
            UNION ALL
            
            SELECT 
                'Usuários' as categoria,
                'Ativos' as metrica,
                COUNT(*) as valor
            FROM users 
            WHERE enabled = 1
            
            UNION ALL
            
            SELECT 
                'Usuários' as categoria,
                'Logaram hoje' as metrica,
                COUNT(*) as valor
            FROM users 
            WHERE last_login >= TRUNC(SYSDATE)
            
            UNION ALL
            
            SELECT 
                'Usuários' as categoria,
                'Logaram esta semana' as metrica,
                COUNT(*) as valor
            FROM users 
            WHERE last_login >= TRUNC(SYSDATE) - 7
            
            UNION ALL
            
            SELECT 
                'Dados' as categoria,
                'Endereços' as metrica,
                COUNT(*) as valor
            FROM addresses
            
            UNION ALL
            
            SELECT 
                'Dados' as categoria,
                'Roles' as metrica,
                COUNT(*) as valor
            FROM roles
            
            ORDER BY categoria, metrica;
    END;
    
    FUNCTION get_user_activity_score(p_user_id NUMBER) RETURN NUMBER AS
        v_score NUMBER := 0;
        v_last_login DATE;
        v_login_count NUMBER;
    BEGIN
        SELECT last_login INTO v_last_login
        FROM users
        WHERE id = p_user_id;
        
        -- Pontuação baseada no último login
        IF v_last_login IS NOT NULL THEN
            IF v_last_login >= SYSDATE - 1 THEN
                v_score := v_score + 10; -- Logou hoje
            ELSIF v_last_login >= SYSDATE - 7 THEN
                v_score := v_score + 5;  -- Logou esta semana
            ELSIF v_last_login >= SYSDATE - 30 THEN
                v_score := v_score + 2;  -- Logou este mês
            END IF;
        END IF;
        
        RETURN v_score;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN 0;
        WHEN OTHERS THEN
            RETURN 0;
    END;
    
    PROCEDURE cleanup_old_data_oracle(
        p_days_old IN NUMBER DEFAULT 365,
        p_dry_run IN NUMBER DEFAULT 1,
        p_result OUT VARCHAR2
    ) AS
        v_user_count NUMBER;
        v_audit_count NUMBER;
    BEGIN
        -- Contar registros antigos
        SELECT COUNT(*) INTO v_user_count
        FROM users
        WHERE enabled = 0 
        AND created_at < CURRENT_TIMESTAMP - INTERVAL p_days_old DAY;
        
        -- Contar logs de auditoria antigos
        SELECT COUNT(*) INTO v_audit_count
        FROM user_audit
        WHERE change_date < CURRENT_TIMESTAMP - INTERVAL p_days_old DAY;
        
        IF p_dry_run = 1 THEN
            p_result := 'DRY RUN - Seria removido: ' || v_user_count || ' usuários, ' || v_audit_count || ' logs de auditoria';
        ELSE
            -- Executar limpeza
            DELETE FROM users
            WHERE enabled = 0 
            AND created_at < CURRENT_TIMESTAMP - INTERVAL p_days_old DAY;
            
            DELETE FROM user_audit
            WHERE change_date < CURRENT_TIMESTAMP - INTERVAL p_days_old DAY;
            
            p_result := 'SUCESSO - Removidos: ' || v_user_count || ' usuários, ' || v_audit_count || ' logs de auditoria';
            COMMIT;
        END IF;
        
    EXCEPTION
        WHEN OTHERS THEN
            p_result := 'ERRO: ' || SQLERRM;
            ROLLBACK;
    END;
    
END stats_oracle_pkg;
/

-- Trigger Oracle avançado para auditoria
CREATE OR REPLACE TRIGGER user_audit_oracle_trigger
    AFTER INSERT OR UPDATE OR DELETE ON users
    FOR EACH ROW
DECLARE
    v_action VARCHAR2(10);
    v_old_values CLOB;
    v_new_values CLOB;
    v_client_info VARCHAR2(64);
BEGIN
    -- Capturar informações do cliente
    SELECT SYS_CONTEXT('USERENV', 'CLIENT_INFO') INTO v_client_info FROM dual;
    
    -- Determinar ação
    IF INSERTING THEN
        v_action := 'INSERT';
        v_new_values := 'username: ' || :NEW.username || 
                       ', email: ' || :NEW.email ||
                       ', enabled: ' || :NEW.enabled;
    ELSIF UPDATING THEN
        v_action := 'UPDATE';
        v_old_values := 'username: ' || :OLD.username || 
                       ', email: ' || :OLD.email ||
                       ', enabled: ' || :OLD.enabled;
        v_new_values := 'username: ' || :NEW.username || 
                       ', email: ' || :NEW.email ||
                       ', enabled: ' || :NEW.enabled;
    ELSIF DELETING THEN
        v_action := 'DELETE';
        v_old_values := 'username: ' || :OLD.username || 
                       ', email: ' || :OLD.email ||
                       ', enabled: ' || :OLD.enabled;
    END IF;
    
    -- Inserir no log de auditoria
    INSERT INTO user_audit (user_id, action, old_values, new_values, changed_by, ip_address)
    VALUES (
        COALESCE(:NEW.id, :OLD.id), 
        v_action, 
        v_old_values, 
        v_new_values,
        USER,
        SYS_CONTEXT('USERENV', 'IP_ADDRESS')
    );
    
EXCEPTION
    WHEN OTHERS THEN
        -- Log do erro, mas não bloquear operação
        INSERT INTO system_logs (log_level, message, details)
        VALUES ('ERROR', 'Erro no trigger de auditoria', SQLERRM);
END;
/

-- Comentários Oracle
COMMENT ON PROCEDURE create_user_oracle IS 'Procedure Oracle para criar usuário com validações avançadas';
COMMENT ON FUNCTION validate_password_strength_oracle IS 'Function Oracle para validar força da senha';
COMMENT ON PROCEDURE generate_user_report_oracle IS 'Relatório avançado de usuários usando recursos Oracle';
COMMENT ON PACKAGE stats_oracle_pkg IS 'Package Oracle para estatísticas e manutenção avançada';

-- Log da migração Oracle
INSERT INTO system_logs (log_level, message, details)
VALUES ('INFO', 'Migração Oracle V10 executada com sucesso', 
        'Procedures, functions, packages e triggers Oracle nativos criados');

-- Mensagem de sucesso
SELECT 'Migração Oracle V10 concluída - Procedures nativas criadas!' as resultado FROM dual; 