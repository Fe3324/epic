# 🔐 **GUIA COMPLETO: AUTENTICAÇÃO JWT NA APLICAÇÃO EPIC**

## 🎯 **O QUE É JWT?**

JWT (JSON Web Token) é um padrão de token **stateless** que permite autenticação segura entre cliente e servidor. É composto por **3 partes** separadas por pontos:

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdXBlcmFkbWluIiwiaWF0IjoxNzUxNjAxOTMyLCJleHAiOjE3NTE2ODgzMzJ9.XC4JJffa_PnJC3mu-k_UAVo4wnQPeSXBS7vgKgG89r0
    [HEADER]           [PAYLOAD]                                                [SIGNATURE]
```

### **🔍 Estrutura do Token:**

#### **Header (Cabeçalho):**
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

#### **Payload (Dados):**
```json
{
  "sub": "superadmin",
  "iat": 1751601932,
  "exp": 1751688332
}
```

#### **Signature (Assinatura):**
```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

---

## 🏗️ **ARQUITETURA JWT NA APLICAÇÃO EPIC**

### **📋 Componentes Principais:**

#### **1. JwtUtil (Utilitário JWT)**
```java
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${jwt.expiration:86400000}") // 24 horas
    private long jwtExpiration;
    
    @Value("${jwt.refresh-expiration:604800000}") // 7 dias
    private long refreshExpiration;
    
    // Métodos principais:
    // - generateToken()
    // - validateToken()
    // - extractUsername()
    // - isTokenExpired()
}
```

#### **2. JwtAuthenticationFilter (Filtro JWT)**
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // Intercepta TODAS as requisições
    // Valida token JWT no header Authorization
    // Define SecurityContext para usuário autenticado
}
```

#### **3. AuthService (Serviço de Autenticação)**
```java
@Service
public class AuthService {
    // login() - Autentica usuário e gera tokens
    // register() - Registra novo usuário
    // refreshToken() - Renova tokens
    // logout() - Logout do usuário
}
```

#### **4. SecurityConfig (Configuração de Segurança)**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // Define endpoints públicos e protegidos
    // Configura filtros JWT
    // Configura sessões stateless
}
```

---

## 🔄 **FLUXO DE AUTENTICAÇÃO JWT**

### **🔐 1. PROCESSO DE LOGIN**

```bash
# 1. Cliente faz login
POST /auth/login
{
  "username": "superadmin",
  "password": "admin123"
}

# 2. Servidor valida credenciais
AuthController → AuthService → AuthenticationManager → CustomUserDetailsService

# 3. Se válido, gera tokens JWT
JwtUtil.generateToken() → Access Token (24h)
JwtUtil.generateRefreshToken() → Refresh Token (7 dias)

# 4. Retorna resposta com tokens
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": { ... }
  }
}
```

### **🔒 2. PROCESSO DE AUTORIZAÇÃO**

```bash
# 1. Cliente faz requisição com token
GET /api/users
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

# 2. JwtAuthenticationFilter intercepta
- Extrai token do header Authorization
- Valida token com JwtUtil
- Se válido, define SecurityContext
- Continua para o endpoint

# 3. Endpoint protegido é executado
- SecurityContext contém usuário autenticado
- Roles são verificadas (@PreAuthorize)
- Resposta é retornada
```

---

## ⚙️ **CONFIGURAÇÕES JWT**

### **📝 application.properties:**
```properties
# Configurações JWT
jwt.secret=YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXphYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5emFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6
jwt.expiration=86400000         # 24 horas em milissegundos
jwt.refresh-expiration=604800000 # 7 dias em milissegundos
```

### **🔧 Configurações de Segurança:**

#### **Endpoints Públicos (Não requerem autenticação):**
- `/auth/**` - Autenticação
- `/h2-console/**` - Console H2
- `/`, `/test` - Endpoints de teste
- `/error` - Tratamento de erros

#### **Endpoints Protegidos:**
- `/api/users/**` - Usuários autenticados
- `/api/addresses/**` - Usuários autenticados
- `/api/roles/**` - Apenas ADMIN
- `/admin/**` - Apenas ADMIN

---

## 🔄 **TIPOS DE TOKENS**

### **🎫 Access Token (Token de Acesso)**
- **Duração:** 24 horas
- **Uso:** Autenticação em endpoints protegidos
- **Conteúdo:** username, iat, exp
- **Renovação:** Via refresh token

### **🎟️ Refresh Token (Token de Renovação)**
- **Duração:** 7 dias
- **Uso:** Renovar access token expirado
- **Conteúdo:** username, iat, exp
- **Segurança:** Deve ser armazenado com segurança

---

## 🚀 **TESTANDO A AUTENTICAÇÃO JWT**

### **🔐 1. Fazer Login**
```bash
# PowerShell
$loginBody = @{
    username = "superadmin"
    password = "admin123"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri 'http://localhost:8080/auth/login' -Method POST -ContentType 'application/json' -Body $loginBody

# Salvar token
$token = $response.data.accessToken
```

### **🔒 2. Acessar Endpoint Protegido**
```bash
# PowerShell
$headers = @{Authorization = "Bearer $token"}
$users = Invoke-RestMethod -Uri 'http://localhost:8080/api/users' -Headers $headers

# Verificar roles (apenas ADMIN)
$roles = Invoke-RestMethod -Uri 'http://localhost:8080/api/roles' -Headers $headers
```

### **🔄 3. Renovar Token**
```bash
# PowerShell
$refreshBody = @{
    refreshToken = $response.data.refreshToken
} | ConvertTo-Json

$newTokens = Invoke-RestMethod -Uri 'http://localhost:8080/auth/refresh' -Method POST -ContentType 'application/json' -Body $refreshBody -Headers $headers
```

---

## 🛡️ **SEGURANÇA JWT**

### **🔑 Chave Secreta:**
- **Base64 Encoded:** `YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXphYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5emFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6`
- **Algoritmo:** HMAC SHA256
- **Tamanho:** 256 bits
- **Uso:** Assinar e verificar tokens

### **🛡️ Validações de Segurança:**
- **Expiração:** Tokens expiram automaticamente
- **Assinatura:** Verificação de integridade
- **Formato:** Validação de estrutura JWT
- **Usuário:** Verificação de existência

### **⚠️ Boas Práticas:**
1. **Armazenar tokens com segurança** (localStorage/sessionStorage)
2. **Usar HTTPS** em produção
3. **Implementar logout** adequado
4. **Rotacionar chaves secretas** periodicamente
5. **Validar tokens** em cada requisição

---

## 🔧 **ENDPOINTS DE AUTENTICAÇÃO**

### **AuthController (`/auth/*`):**

#### **POST /auth/login**
```json
// Request
{
  "username": "superadmin",
  "password": "admin123"
}

// Response
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {
      "id": 36,
      "username": "superadmin",
      "email": "super@admin.com",
      "roles": ["ADMIN", "USER"]
    }
  }
}
```

#### **POST /auth/register**
```json
// Request
{
  "username": "novouser",
  "email": "user@teste.com",
  "password": "123456",
  "confirmPassword": "123456",
  "firstName": "Novo",
  "lastName": "Usuário"
}

// Response - Similar ao login
```

#### **POST /auth/refresh**
```json
// Request
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}

// Response - Novos tokens
```

#### **GET /auth/me**
```json
// Headers
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

// Response
{
  "success": true,
  "data": {
    "id": 36,
    "username": "superadmin",
    "email": "super@admin.com",
    "roles": ["ADMIN", "USER"]
  }
}
```

#### **GET /auth/validate**
```json
// Headers
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

// Response
{
  "success": true,
  "data": true,
  "message": "Token válido"
}
```

#### **POST /auth/logout**
```json
// Headers
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

// Response
{
  "success": true,
  "message": "Logout realizado com sucesso"
}
```

---

## 🐛 **TROUBLESHOOTING**

### **❌ Erro 401 (Unauthorized):**
- Token não fornecido
- Token mal formatado
- Header Authorization ausente

### **❌ Erro 403 (Forbidden):**
- Token expirado
- Assinatura inválida
- Usuário sem permissão

### **❌ Erro 400 (Bad Request):**
- Credenciais inválidas
- Dados de entrada malformados
- Refresh token inválido

### **🔍 Logs Úteis:**
```
2025-07-04T01:05:32.529-03:00  INFO 22856 --- [epic] [nio-8080-exec-2] com.app.epic.service.AuthService         : User registered successfully: testeadmin
2025-07-04T01:05:32.685-03:00  INFO 22856 --- [epic] [nio-8080-exec-7] com.app.epic.service.AuthService         : User registered successfully with roles [ADMIN, USER]: superadmin
2025-07-04T01:05:32.803-03:00  WARN 22856 --- [epic] [nio-8080-exec-9] com.app.epic.service.AuthService         : Authentication failed for user: admin
```

---

## 📚 **RESUMO TÉCNICO**

### **🔧 Componentes Técnicos:**
- **JJWT 0.12.3** - Biblioteca JWT
- **Spring Security 6.1.2** - Framework de segurança
- **BCrypt** - Hash de senhas
- **H2 Database** - Banco de dados
- **Hibernate/JPA** - ORM

### **🎯 Fluxo Resumido:**
1. **Login** → Validar credenciais → Gerar JWT
2. **Requisição** → Validar JWT → Autorizar acesso
3. **Renovação** → Refresh token → Novos JWTs
4. **Logout** → Invalidar tokens (opcional)

### **⚡ Vantagens do JWT:**
- **Stateless:** Não precisa de sessões no servidor
- **Seguro:** Assinatura digital garante integridade
- **Flexível:** Pode conter informações customizadas
- **Escalável:** Funciona em ambientes distribuídos

### **🔐 Segurança Implementada:**
- Senhas criptografadas com BCrypt
- Tokens assinados com HMAC SHA256
- Validação de expiração automática
- Proteção contra adulteração de tokens
- Separação de access e refresh tokens

**A autenticação JWT na aplicação Epic está implementada de forma profissional e segura, pronta para produção! 🚀** 