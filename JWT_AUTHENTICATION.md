# 🔐 Autenticação JWT - Epic Application

## 📋 Visão Geral

A aplicação Epic implementa autenticação baseada em **JSON Web Tokens (JWT)** com Spring Security. O sistema oferece autenticação stateless, tokens de acesso e refresh tokens para renovação automática.

---

## 🔑 Endpoints de Autenticação

### **POST** `/auth/login` - Login

Autentica um usuário existente e retorna tokens JWT.

**Request Body:**
```json
{
  "username": "admin",
  "password": "senha123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Login realizado com sucesso",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@epic.com",
      "firstName": "Admin",
      "lastName": "Sistema",
      "roles": [{"name": "ADMIN"}]
    },
    "authenticatedAt": "2025-07-04T00:30:00"
  },
  "status": 200
}
```

**Errors:**
- `400` - Credenciais inválidas
- `400` - Validação de entrada

---

### **POST** `/auth/register` - Registro

Registra um novo usuário no sistema.

**Request Body:**
```json
{
  "username": "novouser",
  "email": "novo@epic.com",
  "password": "senha123",
  "confirmPassword": "senha123",
  "firstName": "Novo",
  "lastName": "Usuário",
  "phone": "+5511999999999",
  "birthDate": "1990-01-01"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Usuário registrado com sucesso",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {
      "id": 3,
      "username": "novouser",
      "email": "novo@epic.com",
      "firstName": "Novo",
      "lastName": "Usuário",
      "roles": [{"name": "USER"}]
    },
    "authenticatedAt": "2025-07-04T00:30:00"
  },
  "status": 201
}
```

**Validações:**
- Username: 3-50 caracteres, alfanumérico + . _ -
- Email: formato válido
- Password: mínimo 6 caracteres
- confirmPassword: deve ser igual ao password
- Phone: formato internacional (opcional)
- birthDate: data no passado (opcional)

**Errors:**
- `400` - Username já existe
- `400` - Email já existe
- `400` - Validação de entrada

---

### **POST** `/auth/refresh` - Renovar Token

Renova o access token usando o refresh token.

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Token renovado com sucesso",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {...},
    "authenticatedAt": "2025-07-04T00:30:00"
  },
  "status": 200
}
```

**Errors:**
- `400` - Refresh token inválido ou expirado

---

### **GET** `/auth/me` - Usuário Atual

Retorna dados do usuário autenticado.

**Headers:**
```
Authorization: Bearer <access_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Usuário atual recuperado com sucesso",
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@epic.com",
    "firstName": "Admin",
    "lastName": "Sistema",
    "phone": "11999999999",
    "enabled": true,
    "roles": [{"name": "ADMIN"}],
    "addresses": []
  },
  "status": 200
}
```

**Errors:**
- `401` - Token inválido ou expirado

---

### **GET** `/auth/validate` - Validar Token

Valida se o token é válido.

**Headers:**
```
Authorization: Bearer <access_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Token válido",
  "data": true,
  "status": 200
}
```

---

### **POST** `/auth/logout` - Logout

Realiza logout (invalida o token).

**Headers:**
```
Authorization: Bearer <access_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Logout realizado com sucesso",
  "data": null,
  "status": 200
}
```

---

## 🔒 Proteção de Endpoints

### **Endpoints Públicos (Não requerem autenticação)**
- `GET /` - Página inicial
- `GET /test` - Endpoint de teste
- `POST /auth/login` - Login
- `POST /auth/register` - Registro
- `POST /auth/refresh` - Renovar token
- `GET /h2-console/**` - Console H2

### **Endpoints Protegidos (Requerem autenticação)**
- `GET /auth/me` - Usuário atual
- `GET /auth/validate` - Validar token
- `POST /auth/logout` - Logout
- `GET/POST/PUT/DELETE /api/users/**` - Gestão de usuários
- `GET/POST/PUT/DELETE /api/addresses/**` - Gestão de endereços

### **Endpoints Admin (Requerem ROLE_ADMIN)**
- `GET/POST/PUT/DELETE /api/roles/**` - Gestão de roles

---

## 🔧 Configurações JWT

### **Propriedades (application.properties)**
```properties
# JWT Secret (Base64)
jwt.secret=YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXphYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5emFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6

# Access Token - 24 horas (86400000 ms)
jwt.expiration=86400000

# Refresh Token - 7 dias (604800000 ms)
jwt.refresh-expiration=604800000
```

### **Formato do Token**
```
Authorization: Bearer <access_token>
```

### **Claims do Token**
- `sub` (subject): username do usuário
- `iat` (issued at): timestamp de criação
- `exp` (expiration): timestamp de expiração

---

## 🧪 Exemplos de Uso

### **1. Login Completo**
```bash
# 1. Login
curl -X POST "http://localhost:8080/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'

# Response: accessToken, refreshToken, user data
```

### **2. Usar Token para Acessar API Protegida**
```bash
# 2. Usar token
curl -X GET "http://localhost:8080/api/users" \
  -H "Authorization: Bearer <access_token>"

# Response: Lista de usuários
```

### **3. Renovar Token**
```bash
# 3. Renovar token
curl -X POST "http://localhost:8080/auth/refresh" \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "<refresh_token>"
  }'

# Response: Novo accessToken e refreshToken
```

### **PowerShell Examples**

```powershell
# Login
$loginBody = @{
  username = "admin"
  password = "admin123"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/auth/login" -Method POST -ContentType "application/json" -Body $loginBody

$token = $response.data.accessToken

# Usar token
$headers = @{ Authorization = "Bearer $token" }
Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Headers $headers
```

---

## 🚫 Tratamento de Erros

### **401 Unauthorized**
```json
{
  "success": false,
  "message": "Token inválido ou expirado",
  "timestamp": "2025-07-04T00:30:00",
  "status": 401
}
```

### **403 Forbidden**
```json
{
  "success": false,
  "message": "Acesso negado",
  "timestamp": "2025-07-04T00:30:00",
  "status": 403
}
```

### **400 Bad Request**
```json
{
  "success": false,
  "message": "Credenciais inválidas",
  "errors": ["Username é obrigatório", "Password é obrigatório"],
  "timestamp": "2025-07-04T00:30:00",
  "status": 400
}
```

---

## 🔐 Segurança

### **Algoritmo**: HS256 (HMAC SHA-256)
### **Chave**: 256-bit secret key (configurável)
### **Expiração**: 
- Access Token: 24 horas
- Refresh Token: 7 dias

### **Boas Práticas Implementadas**:
✅ Tokens stateless  
✅ Refresh token rotation  
✅ Validação de entrada robusta  
✅ Senhas criptografadas (BCrypt)  
✅ Rate limiting por endpoint  
✅ Logs de segurança  
✅ Headers de segurança  

---

## 🔄 Fluxo de Autenticação

```
1. Cliente → POST /auth/login → Servidor
2. Servidor valida credenciais
3. Servidor gera AccessToken + RefreshToken
4. Cliente armazena tokens
5. Cliente → GET /api/users (com Authorization header)
6. Servidor valida token JWT
7. Servidor retorna dados se válido

Quando AccessToken expira:
8. Cliente → POST /auth/refresh (com RefreshToken)
9. Servidor gera novos tokens
10. Cliente atualiza tokens armazenados
```

---

**🎉 Sistema JWT Completamente Funcional e Seguro!** 