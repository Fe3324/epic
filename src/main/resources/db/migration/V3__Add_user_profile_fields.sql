-- Terceira migração: Adição de campos de perfil do usuário
-- Versão: V3
-- Descrição: Adiciona campos para perfil completo do usuário

-- Adição de novos campos na tabela users
ALTER TABLE users ADD COLUMN first_name VARCHAR(100);
ALTER TABLE users ADD COLUMN last_name VARCHAR(100);
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
ALTER TABLE users ADD COLUMN birth_date DATE;
ALTER TABLE users ADD COLUMN last_login TIMESTAMP;

-- Criação de nova tabela para endereços
CREATE TABLE addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    zip_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) DEFAULT 'Brasil',
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Índices para a nova tabela
CREATE INDEX idx_addresses_user_id ON addresses(user_id);
CREATE INDEX idx_addresses_primary ON addresses(is_primary);

-- Atualização dos usuários existentes com dados de exemplo
UPDATE users SET 
    first_name = 'Admin',
    last_name = 'Sistema',
    phone = '11999999999'
WHERE username = 'admin';

UPDATE users SET 
    first_name = 'Usuário',
    last_name = 'Teste',
    phone = '11888888888'
WHERE username = 'user'; 