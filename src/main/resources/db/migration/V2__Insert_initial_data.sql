-- Segunda migração: Inserção de dados iniciais
-- Versão: V2
-- Descrição: Inserção de dados básicos para funcionamento da aplicação

-- Inserção de perfis básicos
INSERT INTO roles (name, description) VALUES
    ('ADMIN', 'Administrador do sistema com acesso total'),
    ('USER', 'Usuário comum do sistema'),
    ('MODERATOR', 'Moderador com privilégios específicos');

-- Inserção de usuário administrador padrão
-- Senha: admin123 (deve ser alterada em produção)
INSERT INTO users (username, email, password, enabled) VALUES
    ('admin', 'admin@epic.com', '$2a$10$7KvNzJQ9iYGhbhYQQJFzpeVRZhGfJdKFJ1t6JgZzFgxGjqgTQOlWW', TRUE),
    ('user', 'user@epic.com', '$2a$10$7KvNzJQ9iYGhbhYQQJFzpeVRZhGfJdKFJ1t6JgZzFgxGjqgTQOlWW', TRUE);

-- Atribuição de perfis aos usuários
INSERT INTO user_roles (user_id, role_id) VALUES
    (1, 1), -- admin com perfil ADMIN
    (2, 2); -- user com perfil USER 