package com.app.epic.config;

import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Callback de segurança do Flyway que verifica operações perigosas nos scripts SQL
 */
@Component
public class FlywaySecurityCallback implements Callback {

    private static final Logger logger = Logger.getLogger(FlywaySecurityCallback.class.getName());

    @Value("${flyway.security.validation.enabled:true}")
    private boolean securityValidationEnabled;

    @Value("${flyway.security.validation.mode:strict}")
    private String validationMode;

    @Value("${flyway.security.validation.verbose:true}")
    private boolean verboseLogging;

    @Value("${flyway.security.blocked-operations:DELETE,TRUNCATE,DROP}")
    private String blockedOperations;

    @Value("${flyway.security.allowed-operations:}")
    private String allowedOperations;

    @Value("${flyway.security.bypass.environments:}")
    private String bypassEnvironments;

    @Value("${flyway.security.current.environment:production}")
    private String currentEnvironment;

    // Padrões regex para detectar operações perigosas (serão construídos dinamicamente)
    private List<Pattern> dangerousPatterns;

    // Operações permitidas mesmo contendo palavras-chave perigosas
    private static final List<Pattern> ALLOWED_PATTERNS = Arrays.asList(
        Pattern.compile("\\bCREATE\\s+TABLE\\s+IF\\s+NOT\\s+EXISTS\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bCREATE\\s+INDEX\\s+IF\\s+NOT\\s+EXISTS\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bCREATE\\s+VIEW\\s+IF\\s+NOT\\s+EXISTS\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bCREATE\\s+.*\\s+IF\\s+NOT\\s+EXISTS\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bALTER\\s+TABLE\\s+.*\\s+DROP\\s+CONSTRAINT\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bALTER\\s+TABLE\\s+.*\\s+DROP\\s+COLUMN\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bALTER\\s+TABLE\\s+.*\\s+DROP\\s+INDEX\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bON\\s+DELETE\\s+CASCADE\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bON\\s+DELETE\\s+SET\\s+NULL\\b", Pattern.CASE_INSENSITIVE)
    );

    @Override
    public boolean supports(Event event, Context context) {
        // Intercepta antes da execução das migrações
        return event == Event.BEFORE_EACH_MIGRATE;
    }

    @Override
    public boolean canHandleInTransaction(Event event, Context context) {
        return true;
    }

    @Override
    public void handle(Event event, Context context) {
        if (event == Event.BEFORE_EACH_MIGRATE) {
            initializeDangerousPatterns();
            validateMigrationScript(context);
        }
    }

    private void initializeDangerousPatterns() {
        if (dangerousPatterns == null) {
            dangerousPatterns = Arrays.stream(blockedOperations.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .map(this::createPatternForOperation)
                .collect(Collectors.toList());
        }
    }

    private Pattern createPatternForOperation(String operation) {
        switch (operation) {
            case "DELETE":
                return Pattern.compile("\\bDELETE\\s+FROM\\b", Pattern.CASE_INSENSITIVE);
            case "TRUNCATE":
                return Pattern.compile("\\bTRUNCATE\\s+TABLE\\b", Pattern.CASE_INSENSITIVE);
            case "DROP":
                return Pattern.compile("\\bDROP\\s+(TABLE|DATABASE|SCHEMA|VIEW|INDEX|FUNCTION|PROCEDURE)\\b", Pattern.CASE_INSENSITIVE);
            default:
                return Pattern.compile("\\b" + Pattern.quote(operation) + "\\b", Pattern.CASE_INSENSITIVE);
        }
    }

    private void validateMigrationScript(Context context) {
        // Verificar se validação está habilitada
        if (!securityValidationEnabled) {
            if (verboseLogging) {
                logger.info("🔧 Validação de segurança desabilitada");
            }
            return;
        }

        // Verificar se ambiente atual está na lista de bypass
        if (shouldBypassValidation()) {
            if (verboseLogging) {
                logger.info("🔧 Validação de segurança ignorada para ambiente: " + currentEnvironment);
            }
            return;
        }

        try {
            String scriptContent = readScriptContent(context);
            if (scriptContent != null && !scriptContent.trim().isEmpty()) {
                validateScriptSafety(scriptContent, context.getMigrationInfo().getDescription());
            }
        } catch (Exception e) {
            logger.severe("Erro ao validar script de migração: " + e.getMessage());
            throw new RuntimeException("Falha na validação de segurança da migração", e);
        }
    }

    private boolean shouldBypassValidation() {
        if (bypassEnvironments.trim().isEmpty()) {
            return false;
        }
        
        return Arrays.stream(bypassEnvironments.split(","))
            .map(String::trim)
            .anyMatch(env -> env.equalsIgnoreCase(currentEnvironment));
    }

    private String readScriptContent(Context context) {
        try {
            // Tentar obter o conteúdo do script
            if (context.getMigrationInfo() != null && 
                context.getMigrationInfo().getScript() != null) {
                
                return context.getMigrationInfo().getScript();
            }
            
            // Se não conseguir obter diretamente, não falha
            if (verboseLogging) {
                logger.warning("Não foi possível obter o conteúdo do script para validação");
            }
            return null;
            
        } catch (Exception e) {
            if (verboseLogging) {
                logger.warning("Erro ao ler conteúdo do script: " + e.getMessage());
            }
            return null;
        }
    }

    private void validateScriptSafety(String scriptContent, String migrationDescription) {
        if (verboseLogging) {
            logger.info("🔍 Validando segurança da migração: " + migrationDescription);
        }
        
        // Remover comentários para análise mais precisa
        String cleanedScript = removeComments(scriptContent);
        
        // Verificar se há operações permitidas primeiro
        boolean hasAllowedOperations = ALLOWED_PATTERNS.stream()
            .anyMatch(pattern -> pattern.matcher(cleanedScript).find());
        
        // Verificar operações explicitamente permitidas
        if (!allowedOperations.trim().isEmpty()) {
            boolean hasExplicitlyAllowedOperations = Arrays.stream(allowedOperations.split(","))
                .map(String::trim)
                .anyMatch(op -> cleanedScript.toUpperCase().contains(op.toUpperCase()));
            
            hasAllowedOperations = hasAllowedOperations || hasExplicitlyAllowedOperations;
        }
        
        if (hasAllowedOperations) {
            if (verboseLogging) {
                logger.info("✅ Migração contém operações permitidas: " + migrationDescription);
            }
            return;
        }
        
        // Verificar operações perigosas
        for (Pattern dangerousPattern : dangerousPatterns) {
            if (dangerousPattern.matcher(cleanedScript).find()) {
                handleDangerousOperation(migrationDescription, dangerousPattern);
                return;
            }
        }
        
        if (verboseLogging) {
            logger.info("✅ Migração validada com sucesso: " + migrationDescription);
        }
    }

    private void handleDangerousOperation(String migrationDescription, Pattern dangerousPattern) {
        String errorMessage = String.format(
            "🚨 OPERAÇÃO PERIGOSA DETECTADA na migração '%s'!\n" +
            "Script contém operação potencialmente destrutiva: %s\n" +
            "Para sua segurança, esta migração foi bloqueada.\n\n" +
            "OPÇÕES PARA RESOLVER:\n" +
            "1. Configure 'flyway.security.validation.enabled=false' para desabilitar completamente\n" +
            "2. Adicione a operação em 'flyway.security.allowed-operations'\n" +
            "3. Configure 'flyway.security.current.environment' para um ambiente em 'flyway.security.bypass.environments'\n" +
            "4. Mude 'flyway.security.validation.mode' para 'permissive'\n",
            migrationDescription,
            dangerousPattern.pattern()
        );
        
        if ("permissive".equalsIgnoreCase(validationMode)) {
            logger.warning(errorMessage);
            logger.warning("⚠️  Continuando execução devido ao modo PERMISSIVE");
        } else {
            logger.severe(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    private String removeComments(String script) {
        // Remove comentários SQL de linha única (--)
        String withoutLineComments = script.replaceAll("--.*$", "");
        
        // Remove comentários SQL de múltiplas linhas (/* */)
        String withoutBlockComments = withoutLineComments.replaceAll("/\\*[\\s\\S]*?\\*/", "");
        
        return withoutBlockComments;
    }

    @Override
    public String getCallbackName() {
        return "FlywaySecurityCallback";
    }
} 