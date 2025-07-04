@echo off
echo ===== TESTANDO REGISTRO DE ADMIN =====
echo.

echo 1. Testando se aplicacao esta funcionando...
curl -X GET "http://localhost:8080/test"
echo.
echo.

echo 2. Registrando usuario normal...
curl -X POST "http://localhost:8080/auth/register" -H "Content-Type: application/json" -d "{\"username\":\"testeadmin\",\"email\":\"teste@admin.com\",\"password\":\"admin123\",\"confirmPassword\":\"admin123\",\"firstName\":\"Teste\",\"lastName\":\"Admin\"}"
echo.
echo.

echo 3. Tentando registrar admin via endpoint temporario...
curl -X POST "http://localhost:8080/auth/register-first-admin" -H "Content-Type: application/json" -d "{\"username\":\"superadmin\",\"email\":\"super@admin.com\",\"password\":\"admin123\",\"confirmPassword\":\"admin123\",\"firstName\":\"Super\",\"lastName\":\"Admin\"}"
echo.
echo.

echo 4. Tentando login com admin existente...
curl -X POST "http://localhost:8080/auth/login" -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"admin123\"}"
echo.
echo.

echo ===== CONCLUIDO =====
pause 