@echo off
REM Cria o banco "restaurante" no PostgreSQL. Rode uma vez, depois de instalar o PostgreSQL.
REM Vai pedir a senha do usuario "postgres" definida na instalacao do PostgreSQL.
title OHMELETE - criar banco de dados
cd /d "%~dp0"

set PSQL=
where psql >nul 2>nul && set PSQL=psql
if not defined PSQL (
    for /d %%D in ("C:\Program Files\PostgreSQL\*") do (
        if exist "%%D\bin\psql.exe" set "PSQL=%%D\bin\psql.exe"
    )
)
if not defined PSQL (
    echo Nao encontrei o PostgreSQL neste computador.
    echo Instale-o seguindo o README, secao "Instale o PostgreSQL", e rode este arquivo de novo.
    pause
    exit /b 1
)

echo Usando: %PSQL%
echo Digite a senha do usuario "postgres" que voce escolheu ao instalar o PostgreSQL.
echo (A senha nao aparece enquanto voce digita. Depois aperte Enter.)
echo.
"%PSQL%" -U postgres -h localhost -p 5432 -v ON_ERROR_STOP=1 -f banco\criar-banco.sql
if errorlevel 1 (
    echo.
    echo Nao foi possivel criar o banco. Confira a senha do "postgres" e se o PostgreSQL esta ligado.
    pause
    exit /b 1
)
echo.
echo Banco criado. Agora e so dar dois cliques no iniciar.bat.
pause
