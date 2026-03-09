@echo off
@setlocal

REM =====================================================
REM  Mini Depo v2.0 — Run Script
REM  Projeyi derleyip çalıştırır.
REM =====================================================

REM --- JAVA_HOME Tespiti (build.cmd ile aynı mantık) ---
if not "%JAVA_HOME%" == "" goto :javaOK

if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2025.2\jbr" (
    set "JAVA_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2025.2\jbr"
    goto :javaOK
)
if exist "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.2\jbr" (
    set "JAVA_HOME=C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.2\jbr"
    goto :javaOK
)
for /d %%G in ("C:\Program Files\Java\jdk*") do (
    set "JAVA_HOME=%%G"
    goto :javaOK
)
for /d %%G in ("C:\Program Files\Eclipse Adoptium\jdk*") do (
    set "JAVA_HOME=%%G"
    goto :javaOK
)
for /d %%G in ("%USERPROFILE%\.jdks\openjdk*") do (
    set "JAVA_HOME=%%G"
    goto :javaOK
)

echo [HATA] Java bulunamadi! JAVA_HOME ortam degiskenini ayarlayin.
exit /B 1

:javaOK
set "PATH=%JAVA_HOME%\bin;%PATH%"

REM --- Maven Tespiti ---
where mvn >nul 2>nul
if %ERRORLEVEL% == 0 (
    set "MVN_CMD=mvn"
) else (
    set "MVN_CMD=%~dp0mvnw.cmd"
)

echo.
echo  Mini Depo v2.0 baslatiliyor...
echo.

%MVN_CMD% -q compile exec:java -Dexec.mainClass="com.minidepo.Main" -f "%~dp0pom.xml"

@endlocal

