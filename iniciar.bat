@echo off
REM Liga o sistema OHMELETE e abre o navegador quando estiver pronto.
REM Basta dar dois cliques neste arquivo. Para desligar, feche esta janela.
title OHMELETE - servidor
cd /d "%~dp0"

REM Se o sistema ja estiver rodando, so abre o navegador
powershell -NoProfile -Command "try { Invoke-WebRequest -UseBasicParsing -TimeoutSec 3 http://localhost:8080 | Out-Null; exit 0 } catch { exit 1 }"
if %errorlevel%==0 (
    echo O sistema ja esta ligado. Abrindo http://localhost:8080 ...
    start "" http://localhost:8080
    timeout /t 5 >nul
    exit /b
)

if not exist target\uds mkdir target\uds

REM Usa os certificados do Windows (necessario quando antivirus como o Avast inspeciona HTTPS)
set MAVEN_OPTS=-Djavax.net.ssl.trustStoreType=Windows-ROOT

echo Ligando o sistema. Na primeira vez pode levar alguns minutos.
echo O navegador abre sozinho quando estiver pronto. Para desligar, feche esta janela.
echo.

REM Espera o sistema responder e abre o navegador, em paralelo
start "" /b powershell -NoProfile -WindowStyle Hidden -Command "for ($i=0; $i -lt 300; $i++) { try { Invoke-WebRequest -UseBasicParsing -TimeoutSec 2 http://localhost:8080 | Out-Null; Start-Process 'http://localhost:8080'; break } catch { Start-Sleep 2 } }"

call "%~dp0mvnw.cmd" spring-boot:run "-Dspring-boot.run.jvmArguments=-Djdk.net.unixdomain.tmpdir=target/uds"

echo.
echo O sistema parou. Se apareceu algum erro acima, tire um print desta janela.
pause
