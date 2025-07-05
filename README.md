# Epic Application

## 📋 Descrição

Epic é uma aplicação Spring Boot desenvolvida para demonstrar boas práticas de desenvolvimento com **controle avançado de versão de banco de dados** usando Flyway. A aplicação oferece uma base sólida para desenvolvimento de sistemas web com **autenticação JWT**, **autorização baseada em roles**, **gerenciamento completo de usuários**, **APIs REST padronizadas** e **sistema de segurança para migrações**.

## 🚀 Tecnologias Utilizadas

- **Java 17** - Linguagem de programação
- **Spring Boot 3.2.1** - Framework principal
- **Spring Web** - Para desenvolvimento de APIs REST
- **Spring Security** - Para autenticação e autorização
- **Spring Data JPA** - Para acesso e persistência de dados
- **JWT (JSON Web Token)** - Para autenticação stateless
- **H2 Database** - Banco de dados para desenvolvimento e testes
- **Oracle Database** - Banco de dados para produção (suporte)
- **Flyway** - Controle de versão e migração de banco de dados
- **Flyway Security System** - Sistema customizado de segurança para migrações
- **Lombok** - Para redução de código boilerplate
- **Maven** - Gerenciamento de dependências e build
- **JUnit 5** - Framework de testes
- **MapStruct** - Mapeamento entre DTOs e Entidades

## ✨ Funcionalidades Principais

### 🔐 **Sistema de Autenticação e Autorização**
- ✅ **JWT Authentication** - Tokens stateless seguros
- ✅ **Role-based Authorization** - Controle granular de acesso
- ✅ **Refresh Tokens** - Renovação automática de tokens
- ✅ **Password Encryption** - BCrypt para hash seguro
- ✅ **Login/Logout** - Endpoints seguros de autenticação

### 👥 **Gerenciamento de Usuários**
- ✅ **CRUD completo** - Criar, listar, atualizar, deletar usuários
- ✅ **Busca avançada** - Por nome, email, username, role
- ✅ **Perfis de usuário** - Campos pessoais, telefone, endereços
- ✅ **Sistema de roles** - ADMIN, USER, MODERATOR
- ✅ **Validações robustas** - Email, telefone, dados pessoais

### 🏠 **Sistema de Endereços**
- ✅ **Múltiplos endereços** - Por usuário
- ✅ **Endereço principal** - Flag de endereço primário
- ✅ **Validação de CEP** - Formato brasileiro
- ✅ **Busca por localização** - Cidade, estado, país

### 🛡️ **Sistema de Segurança para Migrações Flyway**
- ✅ **Validação automática** - Bloqueia operações perigosas (DELETE, TRUNCATE, DROP)
- ✅ **Configuração flexível** - Modo strict ou permissive
- ✅ **Bypass por ambiente** - Desenvolvimento vs produção
- ✅ **Logs detalhados** - Auditoria completa das validações
- ✅ **Operações específicas permitidas** - Configuração granular

### 🗄️ **Sistema Inteligente de Banco de Dados**
- ✅ **Detecção automática** - H2, Oracle, MySQL, PostgreSQL
- ✅ **Migrações específicas** - Por tipo de banco
- ✅ **Configuração automática** - Zero configuração manual
- ✅ **Procedures robustas** - Com verificações de existência

### 🌐 **APIs REST Padronizadas**
- ✅ **Estrutura uniforme** - ApiResponse para todas as respostas
- ✅ **Paginação** - PageResponse para listagens
- ✅ **Tratamento de erros** - GlobalExceptionHandler
- ✅ **Validação automática** - Bean Validation
- ✅ **Documentação OpenAPI** - Swagger UI (futuro)

## 📋 Pré-requisitos

Antes de executar a aplicação, certifique-se de ter instalado:

- **Java 17** ou superior
- **Maven 3.6+** (opcional, pois o projeto inclui Maven Wrapper)
- **Git** para clonar o repositório
- **Oracle Database** (opcional, para produção)

## 🔧 Instalação e Execução

### 1. Clone o repositório
```bash
git clone https://github.com/Fe3324/epic.git
cd epic
```

### 2. Execute a aplicação

#### Usando Maven Wrapper (Recomendado)
```bash
# No Windows
./mvnw.cmd spring-boot:run

# No Linux/Mac
./mvnw spring-boot:run
```

#### Usando Maven instalado localmente
```bash
mvn spring-boot:run
```

### 3. Acesse a aplicação
A aplicação estará disponível em: `http://localhost:8080`

## 🏗️ Estrutura do Projeto

```
epic/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/app/epic/
│   │   │       ├── config/
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   ├── JwtProperties.java
│   │   │       │   ├── FlywayConfig.java
│   │   │       │   └── FlywaySecurityCallback.java
│   │   │       ├── controller/
│   │   │       │   ├── TestController.java
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── UserController.java
│   │   │       │   ├── RoleController.java
│   │   │       │   ├── AddressController.java
│   │   │       │   └── AdminController.java
│   │   │       ├── dto/
│   │   │       │   ├── ApiResponse.java
│   │   │       │   ├── PageResponse.java
│   │   │       │   ├── AuthResponse.java
│   │   │       │   ├── LoginRequest.java
│   │   │       │   ├── RegisterRequest.java
│   │   │       │   └── [UserDTO, RoleDTO, AddressDTO...]
│   │   │       ├── entity/
│   │   │       │   ├── User.java
│   │   │       │   ├── Role.java
│   │   │       │   └── Address.java
│   │   │       ├── service/
│   │   │       │   ├── AuthService.java
│   │   │       │   ├── UserService.java
│   │   │       │   ├── RoleService.java
│   │   │       │   ├── AddressService.java
│   │   │       │   └── CustomUserDetailsService.java
│   │   │       ├── repository/
│   │   │       │   ├── UserRepository.java
│   │   │       │   ├── RoleRepository.java
│   │   │       │   └── AddressRepository.java
│   │   │       ├── security/
│   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │       │   └── JwtUtil.java
│   │   │       ├── mapper/
│   │   │       │   ├── UserMapper.java
│   │   │       │   ├── RoleMapper.java
│   │   │       │   └── AddressMapper.java
│   │   │       └── EpicApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-oracle.properties
│   │       ├── db/
│   │       │   └── migration/
│   │       │       ├── V1__Create_initial_schema.sql
│   │       │       ├── V2__Insert_initial_data.sql
│   │       │       ├── V3__Add_user_profile_fields.sql
│   │       │       ├── V4__Add_procedures_and_validations.sql
│   │       │       ├── oracle/
│   │       │       │   └── V10__Oracle_procedures.sql
│   │       │       ├── h2/
│   │       │       │   └── V11__H2_specific_features.sql
│   │       │       └── V99__EXAMPLE_DANGEROUS_MIGRATION.sql.disabled
│   │       ├── static/
│   │       └── templates/
│   └── test/
│       └── java/
│           └── com/app/epic/
│               ├── config/
│               ├── controller/
│               ├── entity/
│               ├── repository/
│               ├── flyway/
│               ├── EpicApplicationTests.java
│               └── EpicTestSuite.java
├── docs/
│   ├── API_DOCUMENTATION.md
│   ├── ADMIN_REGISTRATION_GUIDE.md
│   ├── JWT_AUTHENTICATION_GUIDE.md
│   ├── FLYWAY_SECURITY_GUIDE.md
│   ├── COMANDOS_TESTE_MIGRACAO.md
│   ├── PROBLEMAS_RESOLVIDOS.md
│   └── TESTING.md
├── scripts/
│   └── create-admin.ps1
├── data/
│   └── testdb.mv.db
├── target/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

### Descrição dos Componentes

#### 📁 Diretórios Principais
- **config/** - Configurações de segurança, JWT, Flyway
- **controller/** - Controllers REST para todas as funcionalidades
- **dto/** - Data Transfer Objects e respostas padronizadas
- **service/** - Lógica de negócio e serviços
- **security/** - Sistema JWT e filtros de segurança
- **repository/** - Repositories JPA com queries customizadas
- **mapper/** - Mapeadores entre DTOs e entidades
- **exception/** - Tratamento global de exceções

#### 🔐 Sistema de Segurança
- **JWT Authentication** - Tokens stateless seguros
- **Role-based Authorization** - Controle por roles (ADMIN, USER)
- **Password Encryption** - BCrypt para senhas
- **Security Filters** - Filtros customizados para JWT

#### 🛡️ Sistema de Segurança do Flyway
- **FlywayConfig** - Configuração inteligente com detecção automática
- **FlywaySecurityCallback** - Validação de operações perigosas
- **Migrações específicas** - Por tipo de banco de dados

#### 🔍 **Sistema de Detecção Automática de Banco**

#### **Bancos Suportados:**
- ✅ **H2** - Desenvolvimento e testes
- ✅ **Oracle** - Produção empresarial
- ✅ **MySQL** - Produção web
- ✅ **PostgreSQL** - Produção open source

#### **Funcionamento:**
```java
// Detecção automática durante inicialização
DatabaseType type = detectDatabaseType();

// Configuração específica para cada banco
switch (type) {
    case H2:
        locations.add("classpath:db/migration/h2");
        break;
    case ORACLE:
        locations.add("classpath:db/migration/oracle");
        break;
}
```

### Migrações Implementadas

#### V1 - Schema Inicial (`V1__Create_initial_schema.sql`)
- ✅ Criação das tabelas: `users`, `roles`, `user_roles`
- ✅ Definição de chaves primárias e estrangeiras
- ✅ Criação de índices para performance

#### V2 - Dados Iniciais (`V2__Insert_initial_data.sql`)
- ✅ Inserção de roles básicos: ADMIN, USER, MODERATOR
- ✅ Criação de usuários padrão (admin/user)
- ✅ Configuração de senhas criptografadas

#### V3 - Campos de Perfil (`V3__Add_user_profile_fields.sql`)
- ✅ Adição de campos pessoais: `first_name`, `last_name`, `phone`, `birth_date`, `last_login`
- ✅ Criação da tabela `addresses`
- ✅ Atualização de dados existentes

#### V4 - Procedures e Validações (`V4__Add_procedures_and_validations.sql`)
- ✅ Adição de índices para performance
- ✅ Criação de views para consultas complexas
- ✅ Procedures de validação e utilitários

#### **NOVAS MIGRAÇÕES ESPECÍFICAS:**

#### V10 - Procedures Oracle (`oracle/V10__Oracle_procedures.sql`)
- ✅ Procedures PL/SQL nativas do Oracle
- ✅ Functions específicas do Oracle
- ✅ Otimizações para ambiente Oracle

#### V11 - Features H2 (`h2/V11__H2_specific_features.sql`)
- ✅ Functions Java específicas do H2
- ✅ Configurações de cache H2
- ✅ Views otimizadas para desenvolvimento

## 🔄 Flyway - Controle de Versão do Banco

### 🛡️ **NOVO: Sistema de Segurança para Migrações**

O sistema implementa **validação automática** que previne operações perigosas nas migrações:

#### **Operações Bloqueadas Automaticamente:**
- ❌ `DELETE FROM` - Deletar registros
- ❌ `TRUNCATE TABLE` - Limpar tabelas
- ❌ `DROP TABLE/INDEX/VIEW` - Remover objetos

#### **Configurações de Segurança:**
```properties
# Validação habilitada por padrão
# flyway.security.validation.enabled=true

# Modo strict (bloqueia) ou permissive (apenas avisa)
# flyway.security.validation.mode=strict

# Operações bloqueadas (configurável)
# flyway.security.blocked-operations=DELETE,TRUNCATE,DROP

# Bypass para ambientes específicos
# flyway.security.bypass.environments=test,development
```

#### **Como Funciona:**
1. **Intercepta migrações** antes da execução
2. **Analisa o script SQL** removendo comentários
3. **Detecta padrões perigosos** via regex
4. **Bloqueia ou avisa** conforme configuração
5. **Registra logs detalhados** para auditoria

### 🔍 **Sistema de Detecção Automática de Banco**

#### **Bancos Suportados:**
- ✅ **H2** - Desenvolvimento e testes
- ✅ **Oracle** - Produção empresarial
- ✅ **MySQL** - Produção web
- ✅ **PostgreSQL** - Produção open source

#### **Funcionamento:**
```java
// Detecção automática durante inicialização
DatabaseType type = detectDatabaseType();

// Configuração específica para cada banco
switch (type) {
    case H2:
        locations.add("classpath:db/migration/h2");
        break;
    case ORACLE:
        locations.add("classpath:db/migration/oracle");
        break;
}
```

### Migrações Implementadas

#### V1 - Schema Inicial (`V1__Create_initial_schema.sql`)
- ✅ Criação das tabelas: `users`, `roles`, `user_roles`
- ✅ Definição de chaves primárias e estrangeiras
- ✅ Criação de índices para performance

#### V2 - Dados Iniciais (`V2__Insert_initial_data.sql`)
- ✅ Inserção de roles básicos: ADMIN, USER, MODERATOR
- ✅ Criação de usuários padrão (admin/user)
- ✅ Configuração de senhas criptografadas

#### V3 - Campos de Perfil (`V3__Add_user_profile_fields.sql`)
- ✅ Adição de campos pessoais: `first_name`, `last_name`, `phone`, `birth_date`, `last_login`
- ✅ Criação da tabela `addresses`
- ✅ Atualização de dados existentes

#### V4 - Procedures e Validações (`V4__Add_procedures_and_validations.sql`)
- ✅ Adição de índices para performance
- ✅ Criação de views para consultas complexas
- ✅ Procedures de validação e utilitários

#### **NOVAS MIGRAÇÕES ESPECÍFICAS:**

#### V10 - Procedures Oracle (`oracle/V10__Oracle_procedures.sql`)
- ✅ Procedures PL/SQL nativas do Oracle
- ✅ Functions específicas do Oracle
- ✅ Otimizações para ambiente Oracle

#### V11 - Features H2 (`h2/V11__H2_specific_features.sql`)
- ✅ Functions Java específicas do H2
- ✅ Configurações de cache H2
- ✅ Views otimizadas para desenvolvimento

## 🔐 **NOVO: Sistema de Autenticação JWT**

### Configuração JWT

```properties
# Configurações JWT
jwt.secret=YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXo...
jwt.expiration=86400000    # 24 horas
jwt.refresh-expiration=604800000  # 7 dias
```

### Endpoints de Autenticação

#### POST /auth/login
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Resposta:**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@epic.com",
      "roles": ["ADMIN"]
    }
  }
}
```

#### POST /auth/register
```json
{
  "username": "novo.usuario",
  "email": "novo@epic.com",
  "password": "senha123",
  "confirmPassword": "senha123",
  "firstName": "Novo",
  "lastName": "Usuário"
}
```

#### POST /auth/refresh
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Sistema de Autorização

#### Roles Disponíveis:
- **ADMIN**: Acesso total ao sistema
- **USER**: Acesso básico aos recursos
- **MODERATOR**: Privilégios específicos

#### Proteção de Endpoints:
```java
// Apenas administradores
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin/users")

// Usuários autenticados
@PreAuthorize("isAuthenticated()")
@GetMapping("/api/profile")

// Acesso público
@GetMapping("/public/info")
```

## 🌐 **NOVO: APIs REST Completas**

### Estrutura de Resposta Padronizada

Todas as APIs retornam o formato `ApiResponse<T>`:

```json
{
  "success": true,
  "message": "Operação realizada com sucesso",
  "data": { /* dados específicos */ },
  "timestamp": "2025-01-07T10:30:00",
  "status": 200
}
```

### APIs Implementadas

#### 👥 **Users API** (`/api/users`)
```bash
GET    /api/users              # Listar usuários (paginado)
GET    /api/users/{id}         # Buscar por ID
GET    /api/users/username/{username}  # Buscar por username
GET    /api/users/email/{email}        # Buscar por email
GET    /api/users/role/{roleName}      # Buscar por role
GET    /api/users/search?name={name}   # Buscar por nome
POST   /api/users              # Criar usuário
PUT    /api/users/{id}         # Atualizar usuário
DELETE /api/users/{id}         # Deletar usuário
PATCH  /api/users/{id}/last-login     # Atualizar último login
```

#### 🛡️ **Roles API** (`/api/roles`) - *Apenas ADMIN*
```bash
GET    /api/roles              # Listar roles (paginado)
GET    /api/roles/all          # Listar todas (sem paginação)
GET    /api/roles/{id}         # Buscar por ID
GET    /api/roles/name/{name}  # Buscar por nome
GET    /api/roles/search?name={name}  # Buscar roles
POST   /api/roles              # Criar role
PUT    /api/roles/{id}         # Atualizar role
DELETE /api/roles/{id}         # Deletar role
```

#### 🏠 **Addresses API** (`/api/addresses`)
```bash
GET    /api/addresses          # Listar endereços (paginado)
GET    /api/addresses/{id}     # Buscar por ID
GET    /api/addresses/user/{userId}    # Endereços do usuário
GET    /api/addresses/user/{userId}/primary  # Endereço principal
GET    /api/addresses/city/{city}     # Buscar por cidade
GET    /api/addresses/state/{state}   # Buscar por estado
GET    /api/addresses/search?city={city}&state={state}
POST   /api/addresses          # Criar endereço
PUT    /api/addresses/{id}     # Atualizar endereço
DELETE /api/addresses/{id}     # Deletar endereço
PATCH  /api/addresses/user/{userId}/primary/{addressId}  # Definir principal
```

#### 👑 **Admin API** (`/admin/*`) - *Apenas ADMIN*
```bash
POST   /admin/register-admin         # Registrar novo admin
POST   /admin/promote-user/{userId}  # Promover usuário a admin
DELETE /admin/demote-user/{userId}   # Remover role admin
```

#### 🔐 **Auth API** (`/auth/*`) - *Público*
```bash
POST   /auth/login               # Login
POST   /auth/register            # Registro
POST   /auth/refresh             # Refresh token
POST   /auth/logout              # Logout
GET    /auth/me                  # Perfil atual
GET    /auth/validate            # Validar token
POST   /auth/register-first-admin # Primeiro admin (temporário)
```

### Validações Automáticas

#### Usuários:
- **username**: 3-50 caracteres, alfanumérico + . _ -
- **email**: formato válido, único
- **password**: mínimo 6 caracteres
- **phone**: formato internacional
- **birthDate**: data no passado

#### Endereços:
- **street**: obrigatório, máximo 255 caracteres
- **city/state**: obrigatórios, máximo 100 caracteres
- **zipCode**: formato CEP brasileiro (12345-678)

### Paginação e Busca

Todas as listagens suportam:
```bash
# Paginação
GET /api/users?page=0&size=20&sort=username,asc

# Busca
GET /api/users/search?name=João
GET /api/addresses/search?city=São Paulo&state=SP
```

**Resposta Paginada:**
```json
{
  "success": true,
  "data": {
    "content": [/* itens */],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5,
    "first": true,
    "last": false
  }
}
```

## 🧪 **Testes Expandidos**

### Nova Suite de Testes (51 testes)

- ✅ **Testes Unitários**: 35 testes
- ✅ **Testes de Integração**: 11 testes  
- ✅ **Testes de Sistema**: 5 testes
- ✅ **Taxa de Sucesso**: 100%

### Categorias de Teste:
- **Entidades JPA**: User, Address, Role
- **Repositories**: Queries customizadas e derivadas
- **Controllers**: Endpoints REST e integração
- **Segurança**: JWT, autenticação, autorização
- **Flyway**: Migrações e detecção de banco
- **Configurações**: Security, properties

```bash
# Executar todos os testes
./mvnw test

# Testes específicos
./mvnw test -Dtest=UserTest
./mvnw test -Dtest="*SecurityTest"
./mvnw test -Dtest="*FlywayTest"
```

## 📚 **Documentação Completa**

### Guias Disponíveis:

#### 🔐 **Segurança e Autenticação**
- **[JWT_AUTHENTICATION_GUIDE.md](JWT_AUTHENTICATION_GUIDE.md)** - Guia completo de JWT
- **[ADMIN_REGISTRATION_GUIDE.md](ADMIN_REGISTRATION_GUIDE.md)** - Como registrar administradores
- **[FLYWAY_SECURITY_GUIDE.md](FLYWAY_SECURITY_GUIDE.md)** - Sistema de segurança para migrações

#### 🌐 **APIs e Desenvolvimento**
- **[API_DOCUMENTATION.md](API_DOCUMENTATION.md)** - Documentação completa das APIs
- **[TESTING.md](TESTING.md)** - Documentação de testes (51 testes)

#### 🔧 **Operações e Troubleshooting**
- **[COMANDOS_TESTE_MIGRACAO.md](COMANDOS_TESTE_MIGRACAO.md)** - Testes de migração
- **[PROBLEMAS_RESOLVIDOS.md](PROBLEMAS_RESOLVIDOS.md)** - Soluções implementadas
- **[GIT_GUIDE.md](GIT_GUIDE.md)** - Guia de versionamento

## 🎯 **Funcionalidades em Destaque**

### 🛡️ **Sistema de Segurança Flyway**
```bash
# Bloqueia automaticamente migrações perigosas
# Exemplo de migração bloqueada:
DELETE FROM users WHERE active = false;  # ❌ BLOQUEADO
TRUNCATE TABLE logs;                      # ❌ BLOQUEADO  
DROP TABLE temp_data;                     # ❌ BLOQUEADO
```

### 🔍 **Detecção Automática de Banco**
```bash
# Logs durante inicialização:
=== DETECÇÃO DO BANCO DE DADOS ===
Banco detectado: H2
=== CONFIGURAÇÃO DO FLYWAY ===
Locais de migração:
  - classpath:db/migration
  - classpath:db/migration/h2
```

### 🔐 **JWT Stateless**
```bash
# Headers automáticos em todas as requisições autenticadas
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 📊 **APIs Padronizadas**
```bash
# Todas as respostas seguem o mesmo padrão
{
  "success": true,
  "message": "Operação realizada com sucesso",
  "data": { /* dados */ },
  "timestamp": "2025-01-07T10:30:00",
  "status": 200
}
```

## ⚙️ **Comandos de Execução**

### Execução Básica

```bash
# Compilar projeto
./mvnw clean compile

# Executar aplicação (H2 - Desenvolvimento)
./mvnw spring-boot:run

# Executar com Oracle (Produção)
./mvnw spring-boot:run --spring.profiles.active=oracle

# Executar testes
./mvnw test

# Build completo
./mvnw clean package
```

### Verificações do Sistema

```bash
# Verificar se aplicação está rodando
curl http://localhost:8080/

# Testar autenticação
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Acessar console H2 (desenvolvimento)
# http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:file:./data/testdb
# User: sa / Password: password
```

### Comandos de Migração

```bash
# Ver status das migrações
./mvnw flyway:info

# Executar migrações pendentes
./mvnw flyway:migrate

# Validar migrações aplicadas
./mvnw flyway:validate

# Limpar banco (cuidado!)
./mvnw flyway:clean
```

## 🛠️ Desenvolvimento

### Configuração do Ambiente

1. **IDE Recomendadas**:
   - IntelliJ IDEA
   - Eclipse with Spring Tools
   - Visual Studio Code with Spring Boot Extension

2. **Plugins Úteis**:
   - Lombok Plugin
   - Spring Boot DevTools
   - Database Navigator
   - Spring Security Plugin

### Desenvolvimento Local

```bash
# Modo de desenvolvimento com reload automático
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev"

# Debug mode
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# Executar com logs detalhados
./mvnw spring-boot:run -Dlogging.level.com.app.epic=DEBUG

# Executar com perfil específico
./mvnw spring-boot:run -Dspring.profiles.active=oracle
```

### Estrutura de Packages Implementada

```
com.app.epic/
├── config/          # Configurações (Security, JWT, Flyway)
├── controller/      # Controllers REST (Auth, User, Role, Address, Admin)
├── service/         # Lógica de negócio (Auth, User, Role, Address)
├── repository/      # Repositories JPA com queries customizadas
├── entity/          # Entidades JPA (User, Role, Address)
├── dto/             # DTOs padronizados (Request, Response, API)
├── security/        # Sistema JWT (Filter, Util, UserDetails)
├── mapper/          # Mapeadores DTO ↔ Entity
├── exception/       # Tratamento global de exceções
└── EpicApplication  # Classe principal
```

### Configurações por Ambiente

#### Desenvolvimento (H2)
```properties
# application.properties (padrão)
spring.datasource.url=jdbc:h2:file:./data/testdb
spring.h2.console.enabled=true
spring.jpa.show-sql=true
spring.flyway.enabled=false  # Usa FlywayConfig customizado
```

#### Produção (Oracle)
```properties
# application-oracle.properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
spring.datasource.username=epic_user
spring.datasource.password=epic_password
spring.jpa.database-platform=org.hibernate.dialect.Oracle12cDialect
```

#### Testes
```properties
# application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.flyway.clean-disabled=false
spring.h2.console.enabled=false
```

## 🔧 Troubleshooting

### Problemas Comuns e Soluções

#### 1. **Erro de Lock no Banco H2**
**Sintoma:** `Database may be already in use`
```bash
# Parar aplicação completamente
taskkill /f /im java.exe

# Verificar se não há processos Java rodando
jps

# Remover arquivos de lock
rm data/testdb.*.lock

# Reiniciar aplicação
./mvnw spring-boot:run
```

#### 2. **Flyway Migration Failed**
**Sintoma:** `FlywayException: Migration failed`
```bash
# Verificar status das migrações
./mvnw flyway:info

# Ver histórico completo
curl http://localhost:8080/h2-console
# SELECT * FROM flyway_schema_history ORDER BY installed_rank;

# Reparar metadados se necessário
./mvnw flyway:repair
```

#### 3. **Sistema de Segurança Flyway Bloqueando Migração**
**Sintoma:** `[FLYWAY SECURITY] Operação perigosa detectada`
```bash
# Verificar logs detalhados
./mvnw spring-boot:run | grep "FLYWAY SECURITY"

# Para desenvolvimento, usar modo permissivo
# Adicionar no application.properties:
# flyway.security.validation.mode=permissive

# Para permitir operação específica:
# flyway.security.allowed-operations=DROP INDEX
```

#### 4. **JWT Token Inválido**
**Sintoma:** `401 Unauthorized`
```bash
# Verificar token no header
Authorization: Bearer [seu-token]

# Validar token
curl -H "Authorization: Bearer [token]" http://localhost:8080/auth/validate

# Renovar token
curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"[refresh-token]"}'
```

#### 5. **Porta 8080 em Uso**
**Sintoma:** `Port 8080 was already in use`
```bash
# Windows - Verificar processo usando a porta
netstat -ano | findstr :8080

# Linux/Mac - Verificar processo
lsof -i :8080

# Alterar porta da aplicação
./mvnw spring-boot:run -Dserver.port=8081

# Ou no application.properties:
# server.port=8081
```

#### 6. **Erro de Detecção de Banco**
**Sintoma:** `DatabaseType não detectado`
```bash
# Verificar logs de detecção
./mvnw spring-boot:run | grep "DETECÇÃO DO BANCO"

# Forçar tipo de banco específico
# No FlywayConfig.java, ajustar detectDatabaseType()

# Verificar JDBC URL
./mvnw spring-boot:run -Dlogging.level.org.springframework.jdbc=DEBUG
```

### Comandos de Debug

```bash
# Logs detalhados da aplicação
./mvnw spring-boot:run -Dlogging.level.com.app.epic=DEBUG

# Logs SQL do Hibernate
./mvnw spring-boot:run -Dlogging.level.org.hibernate.SQL=DEBUG

# Logs do Spring Security
./mvnw spring-boot:run -Dlogging.level.org.springframework.security=DEBUG

# Logs do Flyway
./mvnw spring-boot:run -Dlogging.level.org.flywaydb=DEBUG

# Todos os logs detalhados
./mvnw spring-boot:run -Dlogging.level.root=DEBUG
```

## 🚀 **Roadmap e Próximas Funcionalidades**

### 🎯 **Versão 1.1 - Planejada**
- [ ] **Swagger/OpenAPI 3** - Documentação interativa das APIs
- [ ] **Rate Limiting** - Controle de taxa de requisições
- [ ] **Audit Logs** - Sistema de auditoria completo
- [ ] **Email Service** - Envio de emails transacionais
- [ ] **Password Reset** - Recuperação de senha via email

### 🎯 **Versão 1.2 - Planejada**
- [ ] **File Upload** - Sistema de upload de arquivos
- [ ] **Notifications** - Sistema de notificações push
- [ ] **Dashboard Admin** - Interface web administrativa
- [ ] **Metrics & Monitoring** - Actuator + Micrometer
- [ ] **Docker Support** - Containerização completa

### 🎯 **Versão 2.0 - Futuro**
- [ ] **Microservices** - Arquitetura distribuída
- [ ] **Event Sourcing** - Padrão de eventos
- [ ] **GraphQL API** - Alternativa ao REST
- [ ] **React Frontend** - Interface web moderna
- [ ] **Kubernetes** - Orquestração de containers

## 🤝 Contribuindo

### Como Contribuir

1. **Fork** o projeto no GitHub
2. **Crie** uma branch para sua feature (`git checkout -b feature/NovaFuncionalidade`)
3. **Implemente** a funcionalidade com testes
4. **Execute** todos os testes (`./mvnw test`)
5. **Commit** suas mudanças (`git commit -m 'feat: adiciona nova funcionalidade'`)
6. **Push** para a branch (`git push origin feature/NovaFuncionalidade`)
7. **Abra** um Pull Request

### Convenções de Commit (Conventional Commits)

```bash
# Novos recursos
git commit -m "feat: adiciona endpoint de logout JWT"

# Correções de bugs
git commit -m "fix: corrige erro de validação de email duplicado"

# Documentação
git commit -m "docs: atualiza README com guia de JWT"

# Refatoração
git commit -m "refactor: melhora estrutura do SecurityConfig"

# Testes
git commit -m "test: adiciona testes para AuthService"

# Melhorias de performance
git commit -m "perf: otimiza queries do UserRepository"

# Mudanças de build
git commit -m "build: atualiza dependências do Maven"

# Mudanças de configuração
git commit -m "chore: configura profiles de desenvolvimento"
```

### Diretrizes de Contribuição

#### **Código:**
- ✅ Seguir padrões Java 17
- ✅ Usar Lombok para reduzir boilerplate
- ✅ Implementar testes unitários e de integração
- ✅ Documentar métodos públicos
- ✅ Validar com Bean Validation

#### **APIs:**
- ✅ Usar estrutura `ApiResponse<T>` padronizada
- ✅ Implementar paginação para listagens
- ✅ Adicionar validações robustas
- ✅ Documentar endpoints

#### **Segurança:**
- ✅ Proteger endpoints sensíveis com JWT
- ✅ Usar roles apropriadas (ADMIN, USER)
- ✅ Validar operações do Flyway
- ✅ Criptografar senhas com BCrypt

#### **Testes:**
- ✅ Cobertura mínima de 80%
- ✅ Testes unitários para service/repository
- ✅ Testes de integração para controllers
- ✅ Testes do sistema de segurança

## 📊 **Métricas do Projeto**

### **Estatísticas Atuais:**
- 📦 **52 classes Java** implementadas
- 🧪 **51 testes** com 100% de sucesso
- 🛡️ **10+ endpoints** protegidos com JWT
- 🗄️ **4 migrações** principais + específicas
- 📚 **8 guias** de documentação
- ⚡ **~25s** tempo de execução dos testes

### **Tecnologias e Dependências:**
- ☕ **Java 17** - Language version
- 🍃 **Spring Boot 3.2.1** - Framework
- 🔒 **Spring Security 6** - Security framework  
- 🗃️ **JPA/Hibernate** - ORM
- 🛡️ **JWT** - Authentication
- 🦋 **Flyway** - Database migration
- 🗄️ **H2/Oracle** - Database support
- 🧪 **JUnit 5** - Testing framework

## 📄 Licença

Este projeto está sob a **licença MIT**. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

```
MIT License

Copyright (c) 2025 Felipe

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

## 📞 Contato e Suporte

### **Autor Principal**
- 👤 **Nome**: Felipe
- 🐙 **GitHub**: [@Fe3324](https://github.com/Fe3324)
- 📧 **Email**: fe3324@example.com
- 💼 **LinkedIn**: [Felipe Silva](https://linkedin.com/in/fe3324)

### **Comunidade e Suporte**
- 🐛 **Issues**: [GitHub Issues](https://github.com/Fe3324/epic/issues)
- 💬 **Discussões**: [GitHub Discussions](https://github.com/Fe3324/epic/discussions)
- 📖 **Wiki**: [Project Wiki](https://github.com/Fe3324/epic/wiki)
- 🔄 **Pull Requests**: [Contribuições](https://github.com/Fe3324/epic/pulls)

### **Documentação Adicional**
- 📚 **Guias**: Consulte os arquivos `.md` na raiz do projeto
- 🎥 **Demos**: Videos e capturas de tela em breve
- 📖 **Tutoriais**: Tutoriais passo-a-passo em desenvolvimento

## 🙏 Agradecimentos

### **Tecnologias e Comunidades**
- 🍃 **[Spring Boot Community](https://spring.io/community)** - Framework excepcional
- 🦋 **[Flyway Team](https://flywaydb.org/)** - Migração de banco robusta
- 🗄️ **[H2 Database](https://h2database.com/)** - Banco leve e rápido
- 🧪 **[JUnit Team](https://junit.org/)** - Framework de testes confiável
- 🔧 **[Lombok Project](https://projectlombok.org/)** - Redução de boilerplate
- 🔒 **[Auth0 JWT](https://jwt.io/)** - Padrão JWT robusto

### **Inspirações e Referências**
- 📖 **Spring Boot Reference Documentation**
- 🏗️ **Clean Architecture Principles**
- 🔐 **OAuth 2.0 and JWT Best Practices**
- 🧪 **Test-Driven Development (TDD)**
- 🏢 **Enterprise Integration Patterns**

### **Ferramentas de Desenvolvimento**
- 💻 **IntelliJ IDEA** - IDE principal
- 🐙 **Git & GitHub** - Controle de versão
- 🔧 **Maven** - Gerenciamento de dependências
- 🐳 **Docker** (planejado) - Containerização
- ☁️ **AWS/Cloud** (futuro) - Deploy

---

## 🎉 **Status Final**

### **🚀 Sistema Completamente Funcional!**

✅ **51 testes passando** com 100% de sucesso  
✅ **Sistema JWT** implementado e funcionando  
✅ **APIs REST** completas e padronizadas  
✅ **Segurança Flyway** protegendo migrações  
✅ **Detecção automática** H2 vs Oracle  
✅ **Documentação completa** com 8+ guias  

### **💡 Pronto para:**
- 🚀 **Deploy em produção**
- 🔄 **Desenvolvimento contínuo**
- 👥 **Colaboração em equipe**
- 📈 **Escalabilidade empresarial**

---

<div align="center">

**🎯 Epic Application**  
*Sistema robusto de autenticação e gerenciamento com Spring Boot*

**Desenvolvido com ❤️ por [Felipe](https://github.com/Fe3324)**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Tests](https://img.shields.io/badge/Tests-51%2F51-success.svg)](src/test)
[![Security](https://img.shields.io/badge/Security-JWT%20%2B%20Flyway-red.svg)](FLYWAY_SECURITY_GUIDE.md)

</div>
