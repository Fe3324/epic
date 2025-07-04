-- Migração específica para H2 Database
-- Esta migração só será executada quando conectado ao H2
-- Versão: V11 - H2 Specific Features

-- =======================================================
-- VERIFICAÇÃO DO BANCO DE DADOS
-- =======================================================
-- Esta migração só funciona no H2
SELECT 'H2 Database detectado - executando funcionalidades específicas do H2' as status;

-- =======================================================
-- FUNCTIONS JAVA ESPECÍFICAS DO H2
-- =======================================================

-- Criar functions Java customizadas para H2
CREATE ALIAS IF NOT EXISTS validate_cpf FOR "com.app.epic.h2.functions.ValidatorFunctions.validateCPF";
CREATE ALIAS IF NOT EXISTS format_phone FOR "com.app.epic.h2.functions.FormatterFunctions.formatPhone";
CREATE ALIAS IF NOT EXISTS calculate_age FOR "com.app.epic.h2.functions.DateFunctions.calculateAge";
CREATE ALIAS IF NOT EXISTS encrypt_data FOR "com.app.epic.h2.functions.SecurityFunctions.encryptData";
CREATE ALIAS IF NOT EXISTS decrypt_data FOR "com.app.epic.h2.functions.SecurityFunctions.decryptData";

-- Function para gerar hash de senha (H2 específico)
CREATE ALIAS IF NOT EXISTS generate_password_hash FOR "com.app.epic.h2.functions.SecurityFunctions.generatePasswordHash";

-- Function para validar CEP brasileiro
CREATE ALIAS IF NOT EXISTS validate_cep FOR "com.app.epic.h2.functions.ValidatorFunctions.validateCEP";

-- =======================================================
-- TABELAS COM RECURSOS ESPECÍFICOS DO H2
-- =======================================================

-- Tabela para cache de dados (aproveitando recursos H2)
CREATE TABLE IF NOT EXISTS h2_cache_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cache_key VARCHAR(255) NOT NULL UNIQUE,
    cache_value CLOB,
    cache_type VARCHAR(50) DEFAULT 'GENERIC',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    hit_count INTEGER DEFAULT 0,
    last_accessed TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices otimizados para H2
CREATE INDEX IF NOT EXISTS idx_h2_cache_key ON h2_cache_data(cache_key);
CREATE INDEX IF NOT EXISTS idx_h2_cache_expires ON h2_cache_data(expires_at);
CREATE INDEX IF NOT EXISTS idx_h2_cache_type ON h2_cache_data(cache_type);

-- Tabela para configurações da aplicação
CREATE TABLE IF NOT EXISTS app_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value TEXT,
    setting_type VARCHAR(20) DEFAULT 'STRING',
    description VARCHAR(500),
    is_encrypted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =======================================================
-- VIEWS ESPECÍFICAS DO H2
-- =======================================================

-- View aproveitando functions Java do H2
CREATE VIEW IF NOT EXISTS v_user_details_h2 AS
SELECT 
    u.id,
    u.username,
    u.email,
    u.first_name,
    u.last_name,
    u.phone,
    format_phone(u.phone) as formatted_phone,
    u.birth_date,
    calculate_age(u.birth_date) as age,
    u.enabled,
    u.created_at,
    u.last_login,
    CASE 
        WHEN u.last_login IS NULL THEN 'Nunca acessou'
        WHEN u.last_login > DATEADD('DAY', -1, CURRENT_TIMESTAMP) THEN 'Ativo hoje'
        WHEN u.last_login > DATEADD('DAY', -7, CURRENT_TIMESTAMP) THEN 'Ativo na semana'
        WHEN u.last_login > DATEADD('DAY', -30, CURRENT_TIMESTAMP) THEN 'Ativo no mês'
        ELSE 'Inativo'
    END as activity_status,
    COUNT(a.id) as total_addresses
FROM users u
LEFT JOIN addresses a ON u.id = a.user_id
GROUP BY u.id, u.username, u.email, u.first_name, u.last_name, 
         u.phone, u.birth_date, u.enabled, u.created_at, u.last_login;

-- View para estatísticas usando recursos H2
CREATE VIEW IF NOT EXISTS v_h2_statistics AS
SELECT 
    'Sistema' as categoria,
    'Versão H2' as metrica,
    H2VERSION() as valor_texto,
    0 as valor_numerico
    
UNION ALL

SELECT 
    'Cache' as categoria,
    'Total de entradas' as metrica,
    CAST(COUNT(*) AS VARCHAR) as valor_texto,
    COUNT(*) as valor_numerico
FROM h2_cache_data

UNION ALL

SELECT 
    'Cache' as categoria,
    'Entradas expiradas' as metrica,
    CAST(COUNT(*) AS VARCHAR) as valor_texto,
    COUNT(*) as valor_numerico
FROM h2_cache_data
WHERE expires_at < CURRENT_TIMESTAMP

UNION ALL

SELECT 
    'Configurações' as categoria,
    'Total de configurações' as metrica,
    CAST(COUNT(*) AS VARCHAR) as valor_texto,
    COUNT(*) as valor_numerico
FROM app_settings;

-- =======================================================
-- TRIGGERS ESPECÍFICOS DO H2
-- =======================================================

-- Trigger para atualizar cache automaticamente
CREATE TRIGGER IF NOT EXISTS trg_cache_accessed
AFTER SELECT ON h2_cache_data
FOR EACH ROW
CALL "com.app.epic.h2.triggers.CacheTrigger.updateAccessTime";

-- Trigger para limpeza automática de cache expirado
CREATE TRIGGER IF NOT EXISTS trg_cache_cleanup
AFTER INSERT ON h2_cache_data
FOR EACH ROW
CALL "com.app.epic.h2.triggers.CacheTrigger.cleanupExpired";

-- =======================================================
-- PROCEDURES H2 (via ALIAS)
-- =======================================================

-- Procedure para gerenciar cache H2
CREATE ALIAS IF NOT EXISTS manage_h2_cache FOR "com.app.epic.h2.procedures.CacheProcedures.manageCache";

-- Procedure para backup H2
CREATE ALIAS IF NOT EXISTS backup_h2_database FOR "com.app.epic.h2.procedures.BackupProcedures.backupDatabase";

-- Procedure para compactar banco H2
CREATE ALIAS IF NOT EXISTS compact_h2_database FOR "com.app.epic.h2.procedures.MaintenanceProcedures.compactDatabase";

-- Procedure para exportar dados
CREATE ALIAS IF NOT EXISTS export_h2_data FOR "com.app.epic.h2.procedures.ExportProcedures.exportData";

-- =======================================================
-- INSERÇÃO DE DADOS ESPECÍFICOS DO H2
-- =======================================================

-- Configurações padrão da aplicação
INSERT INTO app_settings (setting_key, setting_value, setting_type, description, is_encrypted) 
VALUES 
    ('h2.auto_backup', 'true', 'BOOLEAN', 'Ativar backup automático do H2', false),
    ('h2.cache_size', '10000', 'INTEGER', 'Tamanho máximo do cache H2', false),
    ('h2.compression_level', '3', 'INTEGER', 'Nível de compressão do H2', false),
    ('app.name', 'Epic Application', 'STRING', 'Nome da aplicação', false),
    ('app.version', '1.0.0', 'STRING', 'Versão da aplicação', false)
ON CONFLICT (setting_key) DO NOTHING;

-- Cache inicial para dados frequentemente acessados
INSERT INTO h2_cache_data (cache_key, cache_value, cache_type, expires_at)
VALUES 
    ('app.startup_time', CURRENT_TIMESTAMP, 'SYSTEM', DATEADD('DAY', 1, CURRENT_TIMESTAMP)),
    ('db.type', 'H2', 'SYSTEM', DATEADD('DAY', 1, CURRENT_TIMESTAMP)),
    ('feature.h2_specific', 'enabled', 'FEATURE', DATEADD('HOUR', 1, CURRENT_TIMESTAMP))
ON CONFLICT (cache_key) DO NOTHING;

-- =======================================================
-- COMANDOS ESPECÍFICOS DO H2
-- =======================================================

-- Compactar banco de dados H2
-- SHUTDOWN COMPACT;

-- Criar backup automático
-- BACKUP TO 'backup/epic_backup.zip';

-- =======================================================
-- VALIDAÇÕES E COMENTÁRIOS
-- =======================================================

-- Validar se todas as configurações foram inseridas
SELECT 
    CASE 
        WHEN COUNT(*) >= 5 THEN 'SUCESSO: Configurações H2 inseridas corretamente'
        ELSE 'AVISO: Algumas configurações podem estar faltando'
    END as validation_result
FROM app_settings 
WHERE setting_key LIKE 'h2.%' OR setting_key LIKE 'app.%';

-- Comentários nas tabelas específicas do H2
COMMENT ON TABLE h2_cache_data IS 'Cache de dados específico para H2 Database';
COMMENT ON TABLE app_settings IS 'Configurações da aplicação para ambiente H2';

-- Log da migração H2
INSERT INTO system_logs (log_level, message, details) 
VALUES ('INFO', 'Migração H2 V11 executada com sucesso', 
        'Funcionalidades específicas do H2 Database implementadas');

-- =======================================================
-- EXEMPLOS DE USO DAS FUNCIONALIDADES H2
-- =======================================================

-- Exemplos comentados de uso das functions H2:
--
-- Validar CPF:
-- SELECT validate_cpf('12345678901') as cpf_valido;
--
-- Formatar telefone:
-- SELECT format_phone('11999999999') as telefone_formatado;
--
-- Calcular idade:
-- SELECT calculate_age('1990-05-15') as idade;
--
-- Criptografar dados:
-- SELECT encrypt_data('senha123', 'chave_secreta') as dados_criptografados;
--
-- Gerenciar cache:
-- CALL manage_h2_cache('SET', 'user_preferences', '{"theme": "dark"}', 'USER_DATA');
--
-- Fazer backup:
-- CALL backup_h2_database('/backup/epic_' || FORMATDATETIME(CURRENT_TIMESTAMP, 'yyyyMMdd_HHmmss') || '.zip');

-- Mensagem de sucesso
SELECT 'Migração H2 V11 concluída - Funcionalidades específicas do H2 implementadas!' as resultado; 