@echo off
setlocal enabledelayedexpansion

REM Установите жесткий режим остановки скрипта при ошибках
set "ERROR_LEVEL=0"

REM 1. Собрать проект с помощью Gradle
echo Building all Gradle modules...
call gradlew.bat build
set ERROR_LEVEL=%ERRORLEVEL%
if %ERROR_LEVEL% neq 0 (
    echo Gradle build failed.
    exit /b 1
) else (
    echo Gradle build succeeded.
)

REM 2. Запушить изменения через Docker Compose
echo Pushing changes using Docker Compose...
docker-compose build
set ERROR_LEVEL=%ERRORLEVEL%
if %ERROR_LEVEL% neq 0 (
    echo Docker Compose build failed.
    exit /b 1
)

docker-compose push
set ERROR_LEVEL=%ERRORLEVEL%
if %ERROR_LEVEL% neq 0 (
    echo Docker Compose push failed.
    exit /b 1
) else (
    echo Docker Compose build and push succeeded.
)

REM 3. Подключиться по SSH и выполнить команды
echo Connecting via SSH and updating Docker containers on the remote server...
(
echo cd /path/to/your/docker-compose/project
echo docker-compose down
echo docker-compose pull
echo docker-compose up -d
) | ssh ser@vm -pw ser123

set ERROR_LEVEL=%ERRORLEVEL%
if %ERROR_LEVEL% neq 0 (
    echo Failed to update Docker containers on the remote server.
    exit /b 1
) else (
    echo Docker containers updated successfully on the remote server.
)

endlocal