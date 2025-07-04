# Epic Application

## 📋 Descrição

Epic é uma aplicação Spring Boot desenvolvida para demonstrar boas práticas de desenvolvimento com controle de versão de banco de dados usando Flyway. A aplicação oferece uma base sólida para desenvolvimento de sistemas web com autenticação, autorização e gerenciamento de usuários.

## 🚀 Tecnologias Utilizadas

- **Java 17** - Linguagem de programação
- **Spring Boot 3.2.1** - Framework principal
- **Spring Web** - Para desenvolvimento de APIs REST
- **Spring Security** - Para autenticação e autorização
- **Spring Data JPA** - Para acesso e persistência de dados
- **H2 Database** - Banco de dados para desenvolvimento e testes
- **Flyway** - Controle de versão e migração de banco de dados
- **Lombok** - Para redução de código boilerplate
- **Maven** - Gerenciamento de dependências e build
- **JUnit 5** - Framework de testes

## 📋 Pré-requisitos

Antes de executar a aplicação, certifique-se de ter instalado:

- **Java 17** ou superior
- **Maven 3.6+** (opcional, pois o projeto inclui Maven Wrapper)
- **Git** para clonar o repositório

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
│   │   │       │   └── SecurityConfig.java
│   │   │       ├── controller/
│   │   │       │   └── TestController.java
│   │   │       └── EpicApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── db/
│   │       │   └── migration/
│   │       │       ├── V1__Create_initial_schema.sql
│   │       │       ├── V2__Insert_initial_data.sql
│   │       │       └── V3__Add_user_profile_fields.sql
│   │       ├── static/
│   │       └── templates/
│   └── test/
│       └── java/
│           └── com/app/epic/
│               └── EpicApplicationTests.java
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
- **src/main/java/** - Código fonte principal da aplicação
- **src/main/resources/** - Recursos da aplicação (configurações, migrações, assets)
- **src/test/java/** - Testes unitários e de integração
- **data/** - Arquivos do banco de dados H2
- **target/** - Diretório de build (gerado automaticamente)

#### 📄 Classes Principais
- **EpicApplication.java** - Classe principal com método main
- **SecurityConfig.java** - Configuração de segurança da aplicação
- **TestController.java** - Controller REST com endpoints de teste

## ⚙️ Configurações da Aplicação

### Arquivo application.properties

```properties
spring.application.name=epic

# Configurações do Banco H2
spring.datasource.url=jdbc:h2:file:./data/testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password

# Configurações do H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.h2.console.settings.web-allow-others=true
spring.h2.console.settings.trace=false

# Configurações do JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Configurações do Flyway
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration
spring.flyway.sql-migration-prefix=V
spring.flyway.sql-migration-separator=__
spring.flyway.sql-migration-suffixes=.sql

# Permitir referências circulares (temporário)
spring.main.allow-circular-references=true
```

### Detalhes das Configurações

#### 🗄️ Banco de Dados H2
- **Tipo**: Banco de dados em arquivo (`jdbc:h2:file:./data/testdb`)
- **Console Web**: Habilitado em `/h2-console`
- **Usuário**: `sa`
- **Senha**: `password`

#### 🔄 Flyway
- **Localização**: `classpath:db/migration`
- **Padrão de arquivos**: `V{versão}__{descrição}.sql`
- **Baseline**: Habilitado para permitir migrações em banco existente

#### 🔐 JPA/Hibernate
- **Dialeto**: H2Dialect
- **DDL**: Validação apenas (não cria/altera tabelas)
- **SQL Logging**: Habilitado com formatação

## 🗄️ Banco de Dados

### Console H2

Para acessar o console web do H2:

1. **URL**: `http://localhost:8080/h2-console`
2. **Configurações de Conexão**:
   - **JDBC URL**: `jdbc:h2:file:./data/testdb`
   - **User Name**: `sa`
   - **Password**: `password`

### Schema do Banco

#### Tabela: users
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    birth_date DATE,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### Tabela: roles
```sql
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Tabela: user_roles
```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);
```

#### Tabela: addresses
```sql
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
```

### Dados Iniciais

#### Usuários Padrão
- **Administrador**:
  - **Username**: `admin`
  - **Email**: `admin@epic.com`
  - **Password**: `admin123`
  - **Role**: ADMIN

- **Usuário Comum**:
  - **Username**: `user`
  - **Email**: `user@epic.com`
  - **Password**: `admin123`
  - **Role**: USER

#### Roles Disponíveis
- **ADMIN**: Administrador do sistema com acesso total
- **USER**: Usuário comum do sistema
- **MODERATOR**: Moderador com privilégios específicos

## 🔄 Flyway - Controle de Versão do Banco

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

### Comandos Úteis do Flyway

```bash
# Verificar status das migrações
./mvnw flyway:info

# Executar migrações pendentes
./mvnw flyway:migrate

# Validar migrações
./mvnw flyway:validate

# Reparar metadados (se necessário)
./mvnw flyway:repair

# Limpar banco de dados (cuidado!)
./mvnw flyway:clean
```

### Criando Novas Migrações

1. **Criar arquivo** em `src/main/resources/db/migration/`
2. **Nomenclatura**: `V{número}__{descrição}.sql`
3. **Exemplo**: `V4__Add_audit_table.sql`

```sql
-- V4__Add_audit_table.sql
CREATE TABLE audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    table_name VARCHAR(255) NOT NULL,
    operation VARCHAR(50) NOT NULL,
    user_id BIGINT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    old_values TEXT,
    new_values TEXT
);
```

## 🔒 Configuração de Segurança

### SecurityConfig.java

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
                .disable()
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            )
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(form -> form.disable());
        
        return http.build();
    }
}
```

### Características de Segurança

- **Encoder de Senha**: BCrypt para hash seguro
- **Console H2**: Acesso permitido com configuração de frames
- **CSRF**: Desabilitado para desenvolvimento
- **Autorização**: Todos os endpoints permitem acesso (configuração para desenvolvimento)

## 🌐 Endpoints da API

### TestController

#### GET /
- **Descrição**: Endpoint raiz da aplicação
- **Resposta**: `"Epic Application is running! Try accessing /h2-console"`
- **Exemplo**:
  ```bash
  curl http://localhost:8080/
  ```

#### GET /test
- **Descrição**: Endpoint de teste
- **Resposta**: `"Test endpoint is working!"`
- **Exemplo**:
  ```bash
  curl http://localhost:8080/test
  ```

### Endpoints Administrativos

#### GET /h2-console
- **Descrição**: Console web do banco H2
- **Uso**: Interface gráfica para gerenciar o banco
- **Acesso**: `http://localhost:8080/h2-console`

## 🧪 Testes

### Executar Testes

```bash
# Todos os testes
./mvnw test

# Testes específicos
./mvnw test -Dtest=EpicApplicationTests

# Testes com relatório detalhado
./mvnw test -Dtest=EpicApplicationTests -X
```

### Testes Implementados

#### EpicApplicationTests
- **Teste de Contexto**: Verifica se a aplicação carrega corretamente
- **Cobertura**: Teste básico de inicialização

## 📦 Build e Empacotamento

### Gerar JAR Executável

```bash
# Build completo
./mvnw clean package

# Pular testes
./mvnw clean package -DskipTests

# Build com perfil específico
./mvnw clean package -Pproduction
```

### Executar JAR

```bash
# Executar JAR gerado
java -jar target/epic-0.0.1-SNAPSHOT.jar

# Com perfil específico
java -jar -Dspring.profiles.active=prod target/epic-0.0.1-SNAPSHOT.jar

# Com configurações customizadas
java -jar -Dserver.port=8081 target/epic-0.0.1-SNAPSHOT.jar
```

## 🚀 Deploy

### Variáveis de Ambiente

```bash
# Perfil da aplicação
export SPRING_PROFILES_ACTIVE=prod

# Porta da aplicação
export SERVER_PORT=8080

# Configurações de banco
export SPRING_DATASOURCE_URL=jdbc:h2:file:./data/epic
export SPRING_DATASOURCE_USERNAME=sa
export SPRING_DATASOURCE_PASSWORD=password

# Configurações do Flyway
export SPRING_FLYWAY_ENABLED=true
export SPRING_FLYWAY_BASELINE_ON_MIGRATE=true
```

### Docker (Futuro)

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/epic-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
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

### Desenvolvimento Local

```bash
# Modo de desenvolvimento com reload automático
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev"

# Debug mode
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### Estrutura de Packages Recomendada

```
com.app.epic/
├── config/          # Configurações
├── controller/      # Controllers REST
├── service/         # Lógica de negócio
├── repository/      # Acesso a dados
├── entity/          # Entidades JPA
├── dto/             # Data Transfer Objects
├── exception/       # Exceções customizadas
└── util/            # Utilitários
```

## 🔧 Troubleshooting

### Problemas Comuns

#### 1. Erro de Lock no Banco H2
```bash
# Parar aplicação completamente
# Verificar se não há processos Java rodando
# Reiniciar aplicação
```

#### 2. Flyway Migration Failed
```bash
# Verificar se migração já foi aplicada
./mvnw flyway:info

# Reparar metadados se necessário
./mvnw flyway:repair
```

#### 3. Porta 8080 em Uso
```bash
# Verificar processo usando a porta
netstat -ano | findstr :8080

# Alterar porta da aplicação
java -jar -Dserver.port=8081 target/epic-0.0.1-SNAPSHOT.jar
```

## 🤝 Contribuindo

1. **Fork** o projeto
2. **Crie** uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. **Commit** suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. **Push** para a branch (`git push origin feature/AmazingFeature`)
5. **Abra** um Pull Request

### Convenções de Commit

```bash
# Novos recursos
git commit -m "feat: adiciona endpoint de logout"

# Correções
git commit -m "fix: corrige erro de validação de email"

# Documentação
git commit -m "docs: atualiza README com instruções de deploy"

# Refatoração
git commit -m "refactor: melhora estrutura do SecurityConfig"

# Testes
git commit -m "test: adiciona testes para UserService"
```

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 📞 Contato

- **Autor**: Felipe
- **GitHub**: [@Fe3324](https://github.com/Fe3324)
- **Email**: fe3324@example.com

## 🙏 Agradecimentos

- Spring Boot Community
- Flyway Team
- H2 Database
- Lombok Project

---

**Epic Application** - Desenvolvido com ❤️ por Felipe
