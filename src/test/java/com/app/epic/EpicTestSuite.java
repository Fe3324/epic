package com.app.epic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Suite de testes para a aplicação Epic
 * 
 * Para executar todos os testes, use:
 * - mvn test
 * - ./mvnw test
 * 
 * Categorias de testes disponíveis:
 * - Testes unitários de entidades (UserTest, AddressTest)
 * - Testes de configuração (SecurityConfigTest)
 * - Testes de controllers (TestControllerTest, TestControllerIntegrationTest)
 * - Testes de repositories (UserRepositoryTest)
 * - Testes de migração Flyway (FlywayMigrationTest)
 * - Teste principal da aplicação (EpicApplicationTests)
 */
@DisplayName("Epic Application Test Suite Documentation")
public class EpicTestSuite {
    
    @Test
    @DisplayName("Documentação da suite de testes")
    void testSuiteDocumentation() {
        // Este teste serve apenas para documentar a estrutura de testes
        // Os testes reais estão em suas respectivas classes
        System.out.println("=== Epic Application Test Suite ===");
        System.out.println("Execute os seguintes comandos para rodar os testes:");
        System.out.println("- Todos os testes: ./mvnw test");
        System.out.println("- Testes específicos: ./mvnw test -Dtest=NomeDoTeste");
        System.out.println("- Com perfil de teste: ./mvnw test -Dspring.profiles.active=test");
    }
} 