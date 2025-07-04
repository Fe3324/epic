# 👑 GUIA COMPLETO: COMO REGISTRAR USUÁRIO ADMIN

## 🎯 MÉTODO 1: REGISTRO + H2 CONSOLE (MAIS SIMPLES)

### Passo 1: Registrar usuário normal
```bash
# Use este JSON no Postman ou ferramenta similar
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "username": "meuadmin",
  "email": "admin@meusite.com",
  "password": "admin123",
  "confirmPassword": "admin123",
  "firstName": "Meu",
  "lastName": "Admin"
}
```

### Passo 2: Promover a admin via H2 Console
1. Acesse: `http://localhost:8080/h2-console`
2. Configurações de conexão:
   - JDBC URL: `jdbc:h2:file:./data/testdb`
   - Username: `sa`
   - Password: `password`

3. Execute os SQLs:
```sql
-- Ver o ID do usuário criado
SELECT id, username FROM users WHERE username = 'meuadmin';

-- Adicionar role ADMIN ao usuário (substitua 4 pelo ID real)
INSERT INTO user_roles (user_id, role_id) VALUES (4, 1);

-- Verificar se funcionou
SELECT u.username, r.name 
FROM users u 
JOIN user_roles ur ON u.id = ur.user_id 
JOIN roles r ON r.id = ur.role_id 
WHERE u.username = 'meuadmin';
```

### Passo 3: Testar acesso admin
```bash
# Login como admin
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "meuadmin",
  "password": "admin123"
}

# Testar acesso a endpoint admin (use o token recebido)
GET http://localhost:8080/api/roles
Authorization: Bearer SEU_TOKEN_AQUI
```

---

## 🎯 MÉTODO 2: USANDO ADMIN EXISTENTE

Se você souber as credenciais do admin existente:

```bash
# Login como admin existente
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

# Verificar se funciona
GET http://localhost:8080/api/roles
Authorization: Bearer SEU_TOKEN_ADMIN
```

---

## 🎯 MÉTODO 3: ENDPOINT TEMPORÁRIO (DESENVOLVIMENTO)

Para desenvolvimento, foi criado um endpoint público temporário:

```bash
# Registrar primeiro admin (endpoint público)
POST http://localhost:8080/auth/register-first-admin
Content-Type: application/json

{
  "username": "superadmin",
  "email": "admin@epic.com",
  "password": "admin123",
  "confirmPassword": "admin123",
  "firstName": "Super",
  "lastName": "Admin"
}
```

---

## 🎯 MÉTODO 4: ENDPOINT ADMIN PROFISSIONAL

Para usar o endpoint admin (precisa estar logado como admin):

```bash
# 1. Login como admin existente
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

# 2. Registrar novo admin
POST http://localhost:8080/admin/register-admin
Content-Type: application/json
Authorization: Bearer SEU_TOKEN_ADMIN

{
  "username": "novoadmin",
  "email": "novoadmin@epic.com",
  "password": "admin123",
  "confirmPassword": "admin123",
  "firstName": "Novo",
  "lastName": "Admin"
}
```

---

## 🎯 MÉTODO 5: PROMOVER USUÁRIO EXISTENTE

Para promover um usuário normal a admin:

```bash
# Login como admin
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

# Promover usuário (substitua 5 pelo ID do usuário)
POST http://localhost:8080/admin/promote-user/5
Authorization: Bearer SEU_TOKEN_ADMIN
```

---

## 🔧 ENDPOINTS CRIADOS

### AdminController (`/admin/*`)
- `POST /admin/register-admin` - Registrar novo admin
- `POST /admin/promote-user/{userId}` - Promover usuário a admin
- `DELETE /admin/demote-user/{userId}` - Remover admin de usuário

### AuthController (`/auth/*`)
- `POST /auth/register-first-admin` - Endpoint temporário para primeiro admin

---

## 🚀 ESTRUTURA DE ROLES

- **ADMIN (ID: 1)**: Acesso total ao sistema
- **USER (ID: 2)**: Acesso básico

### Proteção de Endpoints:
- `/api/roles/**` - Apenas ADMIN
- `/admin/**` - Apenas ADMIN  
- `/api/users/**` - Usuários autenticados
- `/api/addresses/**` - Usuários autenticados

---

## 📋 TESTANDO O ACESSO

### Endpoints para testar se é admin:
```bash
# Listar roles (só admin)
GET http://localhost:8080/api/roles
Authorization: Bearer SEU_TOKEN

# Listar usuários (qualquer usuário logado)
GET http://localhost:8080/api/users
Authorization: Bearer SEU_TOKEN

# Endpoint admin específico
GET http://localhost:8080/admin/promote-user/1
Authorization: Bearer SEU_TOKEN
```

### Credenciais padrão do sistema:
- **admin** / **admin123** (se ainda existir)
- **user** / **admin123** (usuário normal)

---

## ⚠️ IMPORTANTE

1. **Remover endpoint temporário** após criar o primeiro admin
2. **Alterar senhas padrão** em produção
3. **Configurar HTTPS** em produção
4. **Implementar rate limiting** para endpoints de autenticação

---

## 🔍 TROUBLESHOOTING

### Erro 403 (Forbidden):
- Verificar se o token está correto
- Confirmar se o usuário tem role ADMIN
- Verificar se o endpoint está protegido

### Erro 500 (Internal Server Error):
- Verificar logs da aplicação
- Confirmar se os IDs das roles estão corretos
- Verificar se há problemas na configuração JWT

### Erro 400 (Bad Request):
- Verificar formato JSON
- Confirmar se todos os campos obrigatórios estão presentes
- Verificar se as senhas coincidem 