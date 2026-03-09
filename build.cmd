@echo off
@setlocal

REM =====================================================
REM  Mini Depo v2.0 — Build Script
REM  Java ve Maven'i otomatik bulur, projeyi derler.
REM =====================================================

echo.
echo ========================================
echo   Mini Depo v2.0 — Build Script
echo ========================================
echo.

REM --- JAVA_HOME Tespiti ---
if not "%JAVA_HOME%" == "" (
    echo [OK] JAVA_HOME zaten set: %JAVA_HOME%
    goto :javaFound
)

REM IntelliJ IDEA 2025.2 bundled JBR
if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2025.2\jbr" (
    set "JAVA_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2025.2\jbr"
    echo [OK] IntelliJ JBR bulundu: %JAVA_HOME%
    goto :javaFound
)

REM IntelliJ IDEA Community
if exist "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.2\jbr" (
    set "JAVA_HOME=C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.2\jbr"
    echo [OK] IntelliJ CE JBR bulundu: %JAVA_HOME%
    goto :javaFound
)

REM Standart Java konumları
for /d %%G in ("C:\Program Files\Java\jdk*") do (
    set "JAVA_HOME=%%G"
    echo [OK] JDK bulundu: %%G
    goto :javaFound
)

REM Eclipse Adoptium / Temurin
for /d %%G in ("C:\Program Files\Eclipse Adoptium\jdk*") do (
    set "JAVA_HOME=%%G"
    echo [OK] Adoptium JDK bulundu: %%G
    goto :javaFound
)

REM Oracle JDK
for /d %%G in ("C:\Program Files\Oracle\Java\jdk*") do (
    set "JAVA_HOME=%%G"
    echo [OK] Oracle JDK bulundu: %%G
    goto :javaFound
)

REM Kullanici .jdks dizini (IntelliJ indirmeleri)
for /d %%G in ("%USERPROFILE%\.jdks\openjdk*") do (
    set "JAVA_HOME=%%G"
    echo [OK] .jdks JDK bulundu: %%G
    goto :javaFound
)

echo [HATA] Java bulunamadi!
echo   Cozum: JAVA_HOME ortam degiskenini ayarlayin.
echo   Ornek: set JAVA_HOME=C:\Program Files\Java\jdk-17
exit /B 1

:javaFound
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo [OK] Java versiyonu:
"%JAVA_HOME%\bin\java.exe" -version 2>&1
echo.

REM --- Maven Tespiti ---
where mvn >nul 2>nul
if %ERRORLEVEL% == 0 (
    echo [OK] Maven sistem PATH'inde bulundu.
    goto :mavenFound
)

REM Maven wrapper kullan
if exist "%~dp0mvnw.cmd" (
    echo [INFO] Sistem Maven'i bulunamadi, Maven Wrapper kullaniliyor...
    set "MVN_CMD=%~dp0mvnw.cmd"
    goto :runBuild
)

echo [HATA] Maven bulunamadi!
echo   Cozum: Maven yukleyin veya mvnw.cmd dosyasinin varligini kontrol edin.
exit /B 1

:mavenFound
set "MVN_CMD=mvn"

:runBuild
echo.
echo [BUILD] Proje derleniyor...
echo ========================================
echo.

%MVN_CMD% clean compile -f "%~dp0pom.xml"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [HATA] Build basarisiz!
    exit /B 1
)

echo.
echo ========================================
echo [OK] Build basarili!
echo ========================================
echo.
echo Calistirmak icin:
echo   %MVN_CMD% exec:java -Dexec.mainClass="com.minidepo.Main" -f "%~dp0pom.xml"
echo.

@endlocal

