package com.app.epic.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class FlywayConfig {

    @Value("${spring.flyway.locations:classpath:db/migration}")
    private String defaultLocations;

    private boolean flywayEnabled = true;

    @Autowired
    private DataSource dataSource;

    @Bean
    @Primary
    @DependsOn("dataSource")
    public Flyway flyway() {
        if (!flywayEnabled) {
            return null;
        }

        DatabaseType databaseType = detectDatabaseType();
        List<String> locations = buildMigrationLocations(databaseType);
        
        FluentConfiguration config = Flyway.configure()
                .dataSource(dataSource)
                .locations(locations.toArray(new String[0]))
                .baselineOnMigrate(true)
                .validateOnMigrate(true)
                .outOfOrder(false)
                .cleanDisabled(true);

        // Configurações específicas por banco
        configureDatabaseSpecific(config, databaseType);

        Flyway flyway = config.load();
        
        // Log das configurações
        logFlywayConfiguration(databaseType, locations);
        
        return flyway;
    }

    private DatabaseType detectDatabaseType() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String databaseProductName = metaData.getDatabaseProductName().toLowerCase();
            String databaseProductVersion = metaData.getDatabaseProductVersion();
            
            System.out.println("=== DETECÇÃO DO BANCO DE DADOS ===");
            System.out.println("Produto: " + databaseProductName);
            System.out.println("Versão: " + databaseProductVersion);
            System.out.println("Driver: " + metaData.getDriverName());
            System.out.println("URL: " + metaData.getURL());
            
            if (databaseProductName.contains("oracle")) {
                System.out.println("Banco detectado: ORACLE");
                return DatabaseType.ORACLE;
            } else if (databaseProductName.contains("h2")) {
                System.out.println("Banco detectado: H2");
                return DatabaseType.H2;
            } else if (databaseProductName.contains("mysql")) {
                System.out.println("Banco detectado: MYSQL");
                return DatabaseType.MYSQL;
            } else if (databaseProductName.contains("postgresql")) {
                System.out.println("Banco detectado: POSTGRESQL");
                return DatabaseType.POSTGRESQL;
            } else {
                System.out.println("Banco detectado: GENÉRICO (" + databaseProductName + ")");
                return DatabaseType.GENERIC;
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao detectar tipo do banco: " + e.getMessage());
            return DatabaseType.GENERIC;
        }
    }

    private List<String> buildMigrationLocations(DatabaseType databaseType) {
        List<String> locations = new ArrayList<>();
        
        // Migrações comuns (sempre incluídas)
        locations.add("classpath:db/migration");
        
        // Migrações específicas por banco
        switch (databaseType) {
            case ORACLE:
                locations.add("classpath:db/migration/oracle");
                locations.add("classpath:db/migration/oracle-specific");
                break;
            case H2:
                locations.add("classpath:db/migration/h2");
                locations.add("classpath:db/migration/h2-specific");
                break;
            case MYSQL:
                locations.add("classpath:db/migration/mysql");
                break;
            case POSTGRESQL:
                locations.add("classpath:db/migration/postgresql");
                break;
            default:
                // Apenas migrações genéricas
                break;
        }
        
        return locations;
    }

    private void configureDatabaseSpecific(FluentConfiguration config, DatabaseType databaseType) {
        switch (databaseType) {
            case ORACLE:
                config.sqlMigrationPrefix("V")
                      .sqlMigrationSeparator("__")
                      .sqlMigrationSuffixes(".sql")
                      .placeholderReplacement(true)
                      .placeholders(java.util.Map.of(
                          "schema", "EPIC_SCHEMA",
                          "tablespace", "USERS"
                      ))
                      .schemas("EPIC_SCHEMA");
                break;
                
            case H2:
                config.sqlMigrationPrefix("V")
                      .sqlMigrationSeparator("__")
                      .sqlMigrationSuffixes(".sql")
                      .placeholderReplacement(true);
                break;
                
            case MYSQL:
                config.sqlMigrationPrefix("V")
                      .sqlMigrationSeparator("__")
                      .sqlMigrationSuffixes(".sql")
                      .placeholderReplacement(true)
                      .placeholders(java.util.Map.of(
                          "engine", "InnoDB",
                          "charset", "utf8mb4"
                      ));
                break;
                
            default:
                // Configuração padrão
                break;
        }
    }

    private void logFlywayConfiguration(DatabaseType databaseType, List<String> locations) {
        System.out.println("=== CONFIGURAÇÃO DO FLYWAY ===");
        System.out.println("Tipo de banco: " + databaseType);
        System.out.println("Locais de migração:");
        for (String location : locations) {
            System.out.println("  - " + location);
        }
        System.out.println("================================");
    }

    public enum DatabaseType {
        ORACLE,
        H2,
        MYSQL,
        POSTGRESQL,
        GENERIC
    }
} 