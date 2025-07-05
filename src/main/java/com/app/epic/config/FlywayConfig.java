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
import java.util.logging.Logger;

@Configuration
public class FlywayConfig {

    private static final Logger logger = Logger.getLogger(FlywayConfig.class.getName());

    @Value("${spring.flyway.locations:classpath:db/migration}")
    private String defaultLocations;

    @Value("${spring.flyway.enabled:true}")
    private boolean flywayEnabled = true;

    @Autowired
    private DataSource dataSource;

    @Bean
    @Primary
    @DependsOn("dataSource")
    public Flyway flyway() {
        if (!flywayEnabled) {
            logger.info("Flyway está desabilitado via configuração");
            return null;
        }

        try {
            DatabaseType databaseType = detectDatabaseType();
            List<String> locations = buildMigrationLocations(databaseType);
            
            FluentConfiguration config = Flyway.configure()
                    .dataSource(dataSource)
                    .locations(locations.toArray(new String[0]))
                    .baselineOnMigrate(true)
                    .validateOnMigrate(true)
                    .cleanDisabled(true)
                    .callbacks(new FlywaySecurityCallback());

            configureDatabaseSpecific(config, databaseType);
            logFlywayConfiguration(databaseType, locations);

            Flyway flyway = config.load();
            
            flyway.migrate();
            
            logger.info("Migrações Flyway executadas com sucesso!");
            return flyway;
            
        } catch (Exception e) {
            logger.severe("Erro ao configurar Flyway: " + e.getMessage());
            throw new RuntimeException("Falha na configuração do Flyway", e);
        }
    }

    private DatabaseType detectDatabaseType() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String databaseProduct = metaData.getDatabaseProductName().toLowerCase();
            
            logger.info("=== DETECÇÃO DO BANCO DE DADOS ===");
            logger.info("Produto: " + databaseProduct);
            
            if (databaseProduct.contains("oracle")) {
                logger.info("Banco detectado: ORACLE");
                return DatabaseType.ORACLE;
            } else if (databaseProduct.contains("h2")) {
                logger.info("Banco detectado: H2");
                return DatabaseType.H2;
            } else if (databaseProduct.contains("mysql")) {
                logger.info("Banco detectado: MYSQL");
                return DatabaseType.MYSQL;
            } else if (databaseProduct.contains("postgresql")) {
                logger.info("Banco detectado: POSTGRESQL");
                return DatabaseType.POSTGRESQL;
            } else {
                logger.warning("Banco não identificado, usando configuração genérica");
                return DatabaseType.GENERIC;
            }
        } catch (SQLException e) {
            logger.warning("Erro ao detectar tipo do banco: " + e.getMessage());
            return DatabaseType.GENERIC;
        }
    }

    private List<String> buildMigrationLocations(DatabaseType databaseType) {
        List<String> locations = new ArrayList<>();
        locations.add("classpath:db/migration");
        
        switch (databaseType) {
            case ORACLE:
                locations.add("classpath:db/migration/oracle");
                break;
            case H2:
                locations.add("classpath:db/migration/h2");
                break;
            case MYSQL:
                locations.add("classpath:db/migration/mysql");
                break;
            case POSTGRESQL:
                locations.add("classpath:db/migration/postgresql");
                break;
            default:
                // Apenas o diretório padrão
                break;
        }
        
        return locations;
    }

    private void configureDatabaseSpecific(FluentConfiguration config, DatabaseType databaseType) {
        switch (databaseType) {
            case ORACLE:
                config.sqlMigrationSeparator("__")
                      .placeholderReplacement(true)
                      .placeholders(java.util.Map.of("tablespace", "USERS"));
                break;
            case H2:
                config.sqlMigrationSeparator("__")
                      .placeholderReplacement(true)
                      .placeholders(java.util.Map.of("mode", "MySQL"));
                break;
            case MYSQL:
                config.sqlMigrationSeparator("__")
                      .placeholderReplacement(true)
                      .placeholders(java.util.Map.of("engine", "InnoDB"));
                break;
            case POSTGRESQL:
                config.sqlMigrationSeparator("__")
                      .placeholderReplacement(true)
                      .placeholders(java.util.Map.of("schema", "public"));
                break;
            default:
                // Configuração padrão
                break;
        }
    }

    private void logFlywayConfiguration(DatabaseType databaseType, List<String> locations) {
        logger.info("=== CONFIGURAÇÃO DO FLYWAY ===");
        logger.info("Tipo de banco: " + databaseType);
        logger.info("Locais de migração:");
        locations.forEach(location -> logger.info("  - " + location));
    }

    public enum DatabaseType {
        ORACLE,
        H2,
        MYSQL,
        POSTGRESQL,
        GENERIC
    }
} 