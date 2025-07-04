# 🚀 Documentação das APIs REST - Epic Application

## 📋 Endpoints Disponíveis

### 🏠 Aplicação Base
- **GET** `/` - Informações da aplicação
- **GET** `/test` - Endpoint de teste

---

## 👥 API de Usuários (`/api/users`)

### 📝 Listagem e Busca

#### **GET** `/api/users` - Listar todos os usuários (paginado)
**Query Parameters:**
- `page` (int) - Número da página (default: 0)
- `size` (int) - Tamanho da página (default: 20)
- `sort` (string) - Campo para ordenação (default: username)

**Resposta:**
```json
{
  "success": true,
  "message": "Usuários listados com sucesso",
  "data": {
    "content": [...],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5,
    "first": true,
    "last": false,
    "empty": false
  },
  "timestamp": "2025-07-04T00:10:00",
  "status": 200
}
```

#### **GET** `/api/users/{id}` - Buscar usuário por ID
**Resposta:**
```json
{
  "success": true,
  "message": "Usuário encontrado com sucesso",
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@epic.com",
    "firstName": "Admin",
    "lastName": "Sistema",
    "phone": "11999999999",
    "birthDate": "1990-01-01",
    "lastLogin": "2025-07-04T00:10:00",
    "enabled": true,
    "createdAt": "2025-07-04T00:00:00",
    "updatedAt": "2025-07-04T00:10:00",
    "roles": [...],
    "addresses": [...]
  },
  "timestamp": "2025-07-04T00:10:00",
  "status": 200
}
```

#### **GET** `/api/users/username/{username}` - Buscar por username

#### **GET** `/api/users/email/{email}` - Buscar por email

#### **GET** `/api/users/role/{roleName}` - Buscar usuários por role

#### **GET** `/api/users/search?name={name}` - Buscar por nome

### ✏️ Operações CRUD

#### **POST** `/api/users` - Criar usuário
**Request Body:**
```json
{
  "username": "novo.usuario",
  "email": "novo@epic.com",
  "password": "senha123",
  "firstName": "Novo",
  "lastName": "Usuário",
  "phone": "11888888888",
  "birthDate": "1995-05-15",
  "enabled": true,
  "roleIds": [1, 2]
}
```

**Resposta (201 Created):**
```json
{
  "success": true,
  "message": "Recurso criado com sucesso",
  "data": {...},
  "timestamp": "2025-07-04T00:10:00",
  "status": 201
}
```

#### **PUT** `/api/users/{id}` - Atualizar usuário
**Request Body:** (todos os campos opcionais)
```json
{
  "username": "usuario.atualizado",
  "email": "atualizado@epic.com",
  "firstName": "Nome Atualizado",
  "enabled": false,
  "roleIds": [2]
}
```

#### **DELETE** `/api/users/{id}` - Deletar usuário

#### **PATCH** `/api/users/{id}/last-login` - Atualizar último login

---

## 🛡️ API de Roles (`/api/roles`)

### 📝 Listagem e Busca

#### **GET** `/api/roles` - Listar todas as roles (paginado)

#### **GET** `/api/roles/all` - Listar todas as roles (sem paginação)

#### **GET** `/api/roles/{id}` - Buscar role por ID

#### **GET** `/api/roles/name/{name}` - Buscar role por nome

#### **GET** `/api/roles/search?name={name}` - Buscar roles por nome

### ✏️ Operações CRUD

#### **POST** `/api/roles` - Criar role
**Request Body:**
```json
{
  "name": "MANAGER",
  "description": "Gerente com privilégios especiais"
}
```

#### **PUT** `/api/roles/{id}` - Atualizar role
**Request Body:**
```json
{
  "name": "ADMIN_MANAGER",
  "description": "Administrador e Gerente"
}
```

#### **DELETE** `/api/roles/{id}` - Deletar role

---

## 🏠 API de Endereços (`/api/addresses`)

### 📝 Listagem e Busca

#### **GET** `/api/addresses` - Listar todos os endereços (paginado)

#### **GET** `/api/addresses/{id}` - Buscar endereço por ID

#### **GET** `/api/addresses/user/{userId}` - Buscar endereços de um usuário

#### **GET** `/api/addresses/user/{userId}/primary` - Buscar endereço principal do usuário

#### **GET** `/api/addresses/city/{city}` - Buscar endereços por cidade

#### **GET** `/api/addresses/state/{state}` - Buscar endereços por estado

#### **GET** `/api/addresses/search?city={city}&state={state}` - Buscar por localização

### ✏️ Operações CRUD

#### **POST** `/api/addresses` - Criar endereço
**Request Body:**
```json
{
  "street": "Av. Paulista, 1000",
  "city": "São Paulo",
  "state": "SP",
  "zipCode": "01310-100",
  "country": "Brasil",
  "isPrimary": true,
  "userId": 1
}
```

#### **PUT** `/api/addresses/{id}` - Atualizar endereço
**Request Body:**
```json
{
  "street": "Rua Augusta, 500",
  "city": "São Paulo",
  "isPrimary": false
}
```

#### **DELETE** `/api/addresses/{id}` - Deletar endereço

#### **PATCH** `/api/addresses/user/{userId}/primary/{addressId}` - Definir endereço principal

---

## 🔧 Estrutura de Resposta Padronizada

### ✅ Sucesso
```json
{
  "success": true,
  "message": "Operação realizada com sucesso",
  "data": {...},
  "timestamp": "2025-07-04T00:10:00",
  "status": 200
}
```

### ❌ Erro
```json
{
  "success": false,
  "message": "Descrição do erro",
  "errors": ["Lista de erros específicos"],
  "timestamp": "2025-07-04T00:10:00",
  "path": "/api/users",
  "status": 400
}
```

### 📄 Paginação
```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5,
  "first": true,
  "last": false,
  "empty": false
}
```

---

## 🧪 Exemplos de Teste

### Usando cURL:

```bash
# Listar usuários
curl -X GET "http://localhost:8080/api/users"

# Criar usuário
curl -X POST "http://localhost:8080/api/users" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teste",
    "email": "teste@epic.com", 
    "password": "senha123",
    "firstName": "Teste",
    "lastName": "Silva"
  }'

# Buscar usuário por ID
curl -X GET "http://localhost:8080/api/users/1"

# Atualizar usuário
curl -X PUT "http://localhost:8080/api/users/1" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Nome Atualizado"
  }'

# Deletar usuário
curl -X DELETE "http://localhost:8080/api/users/1"
```

### Usando PowerShell:

```powershell
# Listar usuários
Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method GET

# Criar usuário
$body = @{
  username = "teste"
  email = "teste@epic.com"
  password = "senha123"
  firstName = "Teste"
  lastName = "Silva"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method POST -ContentType "application/json" -Body $body

# Buscar usuário
Invoke-RestMethod -Uri "http://localhost:8080/api/users/1" -Method GET
```

---

## 🔐 Validações Implementadas

### Usuários:
- **username**: 3-50 caracteres, alfanumérico + . _ -
- **email**: formato de email válido
- **password**: mínimo 6 caracteres
- **phone**: formato de telefone internacional
- **birthDate**: data no passado

### Roles:
- **name**: 2-50 caracteres, MAIÚSCULAS + _
- **description**: máximo 255 caracteres

### Endereços:
- **street**: obrigatório, máximo 255 caracteres
- **city**: obrigatório, máximo 100 caracteres
- **state**: obrigatório, máximo 100 caracteres
- **zipCode**: formato CEP brasileiro (12345-678)
- **country**: máximo 100 caracteres

---

## 🎯 Status Codes

- **200** - OK (sucesso)
- **201** - Created (criado)
- **400** - Bad Request (erro de validação)
- **404** - Not Found (não encontrado)
- **409** - Conflict (conflito, ex: email já existe)
- **500** - Internal Server Error (erro interno)

---

## 🔄 Funcionalidades Especiais

### Usuários:
- Busca por nome (first_name ou last_name)
- Busca por role
- Atualização de último login
- Relacionamento com roles e endereços

### Roles:
- Validação antes da exclusão (não pode excluir se há usuários)
- Busca por nome

### Endereços:
- Endereço principal por usuário
- Busca por localização
- Relacionamento com usuário

---

**🎉 Todas as APIs estão funcionando com validação, tratamento de erro e resposta padronizada em JSON!** 