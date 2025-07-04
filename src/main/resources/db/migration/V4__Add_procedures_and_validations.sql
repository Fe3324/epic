-- Quarta migração: Procedures e Validações
-- Versão: V4
-- Descrição: Adiciona procedures úteis e verificações de existência

-- =======================================================
-- VERIFICAÇÕES DE EXISTÊNCIA ANTES DE CRIAR OBJETOS
-- =======================================================

-- Criar índice apenas se não existir
CREATE INDEX IF NOT EXISTS idx_users_last_login ON users(last_login);
CREATE INDEX IF NOT EXISTS idx_users_full_name ON users(first_name, last_name);
CREATE INDEX IF NOT EXISTS idx_addresses_location ON addresses(city, state);

-- =======================================================
-- FUNCTIONS PARA VALIDAÇÃO (H2 COMPATÍVEL)
-- =======================================================

-- Function para validar email (usando H2 REGEXP)
CREATE ALIAS IF NOT EXISTS validate_email FOR "com.app.epic.utils.ValidationUtils.validateEmail";

-- Function para formatar nome completo
CREATE ALIAS IF NOT EXISTS format_full_name FOR "com.app.epic.utils.ValidationUtils.formatFullName";

-- Function para contar usuários ativos
CREATE ALIAS IF NOT EXISTS count_active_users FOR "com.app.epic.utils.UserUtils.countActiveUsers";

-- =======================================================
-- PROCEDURES PARA OPERAÇÕES COMUNS (H2 COMPATÍVEL)
-- =======================================================

-- Procedure para criar usuário com validações
CREATE ALIAS IF NOT EXISTS create_user_safe FOR "com.app.epic.procedures.UserProcedures.createUserSafe";

-- Procedure para atualizar último login
CREATE ALIAS IF NOT EXISTS update_last_login FOR "com.app.epic.procedures.UserProcedures.updateLastLogin";

-- Procedure para limpar dados antigos
CREATE ALIAS IF NOT EXISTS cleanup_old_data FOR "com.app.epic.procedures.MaintenanceProcedures.cleanupOldData";

-- Procedure para gerar relatório de usuários
CREATE ALIAS IF NOT EXISTS generate_user_report FOR "com.app.epic.procedures.ReportProcedures.generateUserReport";

-- Procedure para estatísticas do sistema
CREATE ALIAS IF NOT EXISTS get_system_stats FOR "com.app.epic.procedures.ReportProcedures.getSystemStats";

-- =======================================================
-- VIEWS PARA RELATÓRIOS
-- =======================================================

-- View para usuários com informações completas
CREATE VIEW IF NOT EXISTS v_users_complete AS
SELECT 
    u.id,
    u.username,
    u.email,
    u.first_name,
    u.last_name,
    CASE 
        WHEN u.first_name IS NOT NULL AND u.last_name IS NOT NULL 
        THEN CONCAT(u.first_name, ' ', u.last_name)
        ELSE u.username
    END as full_name,
    u.phone,
    u.birth_date,
    u.enabled,
    u.created_at,
    u.updated_at,
    u.last_login,
    COUNT(a.id) as address_count,
    GROUP_CONCAT(r.name) as roles,
    CASE 
        WHEN u.last_login IS NULL THEN 'Nunca logou'
        WHEN u.last_login < DATEADD('DAY', -30, CURRENT_TIMESTAMP) THEN 'Inativo'
        WHEN u.last_login < DATEADD('DAY', -7, CURRENT_TIMESTAMP) THEN 'Pouco ativo'
        ELSE 'Ativo'
    END as activity_status
FROM users u
LEFT JOIN addresses a ON u.id = a.user_id
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
GROUP BY u.id, u.username, u.email, u.first_name, u.last_name, 
         u.phone, u.birth_date, u.enabled, u.created_at, u.updated_at, u.last_login;

-- View para estatísticas gerais
CREATE VIEW IF NOT EXISTS v_system_statistics AS
SELECT 
    'Usuários Totais' as metric_name,
    COUNT(*) as metric_value,
    'usuarios' as category
FROM users
UNION ALL
SELECT 
    'Usuários Ativos' as metric_name,
    COUNT(*) as metric_value,
    'usuarios' as category
FROM users 
WHERE enabled = TRUE
UNION ALL
SELECT 
    'Usuários que já logaram' as metric_name,
    COUNT(*) as metric_value,
    'usuarios' as category
FROM users 
WHERE last_login IS NOT NULL
UNION ALL
SELECT 
    'Endereços Cadastrados' as metric_name,
    COUNT(*) as metric_value,
    'enderecos' as category
FROM addresses
UNION ALL
SELECT 
    'Roles Disponíveis' as metric_name,
    COUNT(*) as metric_value,
    'sistema' as category
FROM roles;

-- View para auditoria básica
CREATE VIEW IF NOT EXISTS v_recent_activity AS
SELECT 
    'USER_CREATED' as action_type,
    u.username as target_user,
    u.created_at as action_date,
    'Sistema' as performed_by
FROM users u
WHERE u.created_at >= DATEADD('DAY', -7, CURRENT_TIMESTAMP)
UNION ALL
SELECT 
    'USER_LOGIN' as action_type,
    u.username as target_user,
    u.last_login as action_date,
    u.username as performed_by
FROM users u
WHERE u.last_login >= DATEADD('DAY', -7, CURRENT_TIMESTAMP)
ORDER BY action_date DESC;

-- =======================================================
-- TRIGGERS PARA AUDITORIA BÁSICA
-- =======================================================

-- Trigger para log de criação de usuários (usando tabela de log se existir)
CREATE TRIGGER IF NOT EXISTS trg_user_created
AFTER INSERT ON users
FOR EACH ROW
CALL "com.app.epic.triggers.AuditTrigger.logUserCreated";

-- Trigger para log de atualização de usuários
CREATE TRIGGER IF NOT EXISTS trg_user_updated
AFTER UPDATE ON users
FOR EACH ROW
CALL "com.app.epic.triggers.AuditTrigger.logUserUpdated";

-- =======================================================
-- TABELAS AUXILIARES (OPCIONAIS)
-- =======================================================

-- Tabela de logs de sistema (apenas se não existir)
CREATE TABLE IF NOT EXISTS system_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    log_level VARCHAR(10) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT USER
);

-- Tabela de auditoria de usuários (apenas se não existir)
CREATE TABLE IF NOT EXISTS user_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(50) NOT NULL,
    old_values TEXT,
    new_values TEXT,
    changed_by VARCHAR(255) DEFAULT USER,
    change_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent TEXT
);

-- =======================================================
-- PROCEDURES PARA MANUTENÇÃO (H2 FUNCTIONS)
-- =======================================================

-- Procedure para backup de dados críticos
CREATE ALIAS IF NOT EXISTS backup_critical_data FOR "com.app.epic.procedures.MaintenanceProcedures.backupCriticalData";

-- Procedure para recriar índices
CREATE ALIAS IF NOT EXISTS rebuild_indexes FOR "com.app.epic.procedures.MaintenanceProcedures.rebuildIndexes";

-- Procedure para verificar integridade dos dados
CREATE ALIAS IF NOT EXISTS check_data_integrity FOR "com.app.epic.procedures.MaintenanceProcedures.checkDataIntegrity";

-- =======================================================
-- PROCEDURES PARA BUSINESS LOGIC
-- =======================================================

-- Procedure para desativar usuário
CREATE ALIAS IF NOT EXISTS deactivate_user FOR "com.app.epic.procedures.UserProcedures.deactivateUser";

-- Procedure para ativar usuário
CREATE ALIAS IF NOT EXISTS activate_user FOR "com.app.epic.procedures.UserProcedures.activateUser";

-- Procedure para resetar senha
CREATE ALIAS IF NOT EXISTS reset_password FOR "com.app.epic.procedures.UserProcedures.resetPassword";

-- Procedure para atribuir role
CREATE ALIAS IF NOT EXISTS assign_role FOR "com.app.epic.procedures.UserProcedures.assignRole";

-- Procedure para remover role
CREATE ALIAS IF NOT EXISTS remove_role FOR "com.app.epic.procedures.UserProcedures.removeRole";

-- =======================================================
-- COMENTÁRIOS E DOCUMENTAÇÃO
-- =======================================================

-- Comentários nas views
COMMENT ON VIEW v_users_complete IS 'View completa de usuários com informações agregadas';
COMMENT ON VIEW v_system_statistics IS 'Estatísticas gerais do sistema';
COMMENT ON VIEW v_recent_activity IS 'Atividades recentes dos usuários';

-- Comentários nas tabelas auxiliares
COMMENT ON TABLE system_logs IS 'Logs do sistema para auditoria e debug';
COMMENT ON TABLE user_audit IS 'Auditoria das operações realizadas pelos usuários';

-- =======================================================
-- EXEMPLOS DE USO DAS PROCEDURES
-- =======================================================

-- Exemplo de uso (comentado):
--
-- Para criar um usuário com validações:
-- CALL create_user_safe('novouser', 'novo@email.com', 'senha123', 'João', 'Silva');
--
-- Para atualizar último login:
-- CALL update_last_login('admin');
--
-- Para gerar relatório de usuários:
-- CALL generate_user_report();
--
-- Para obter estatísticas do sistema:
-- CALL get_system_stats();
--
-- Para fazer backup:
-- CALL backup_critical_data('_backup_' || FORMATDATETIME(CURRENT_TIMESTAMP, 'yyyyMMdd'));
--
-- Para consultar views:
-- SELECT * FROM v_users_complete WHERE activity_status = 'Ativo';
-- SELECT * FROM v_system_statistics;
-- SELECT * FROM v_recent_activity;

-- =======================================================
-- VALIDAÇÕES FINAIS
-- =======================================================

-- Verificar se todas as tabelas necessárias existem
-- (Apenas para log - não falha se não existir)
SELECT 
    CASE 
        WHEN COUNT(*) >= 4 THEN 'SUCESSO: Todas as tabelas principais existem'
        ELSE 'AVISO: Algumas tabelas podem estar faltando'
    END as validation_result
FROM information_schema.tables 
WHERE table_name IN ('USERS', 'ROLES', 'USER_ROLES', 'ADDRESSES');

-- Inserir log da migração
INSERT INTO system_logs (log_level, message, details) 
VALUES ('INFO', 'Migração V4 executada com sucesso', 
        'Adicionadas procedures, views e verificações de existência');

-- Mensagem de sucesso
SELECT 'Migração V4 concluída: Procedures e validações adicionadas com sucesso!' as resultado; 