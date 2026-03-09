@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.
@REM ----------------------------------------------------------------------------
@REM Begin all REM lines with '@' in case MAVEN_BATCH_ECHO is 'on'
@echo off
@setlocal

set ERROR_CODE=0

@REM Set MAVEN_HOME if not already set
if not "%MAVEN_HOME%" == "" goto haveMavenHome
set "MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.9"
:haveMavenHome

@REM Find java.exe
if not "%JAVA_HOME%" == "" goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
goto error

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe
if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
goto error

:execute
@REM Check if maven is downloaded
set WRAPPER_JAR="%~dp0.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
set DIST_URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip"
set DIST_DIR="%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.9"
set MVN_CMD="%DIST_DIR%\bin\mvn.cmd"

if exist %MVN_CMD% goto runMaven

echo Maven not found. Downloading Apache Maven 3.9.9...

@REM Download and extract Maven
if not exist "%USERPROFILE%\.m2\wrapper\dists" mkdir "%USERPROFILE%\.m2\wrapper\dists"

powershell -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $ProgressPreference='SilentlyContinue'; Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip' -OutFile '%USERPROFILE%\.m2\wrapper\dists\maven.zip' }"
if "%ERRORLEVEL%" NEQ "0" (
    echo ERROR: Failed to download Maven.
    goto error
)

powershell -Command "& { Expand-Archive -Path '%USERPROFILE%\.m2\wrapper\dists\maven.zip' -DestinationPath '%USERPROFILE%\.m2\wrapper\dists' -Force }"
if "%ERRORLEVEL%" NEQ "0" (
    echo ERROR: Failed to extract Maven.
    goto error
)

del "%USERPROFILE%\.m2\wrapper\dists\maven.zip"
echo Maven 3.9.9 installed successfully!

:runMaven
%MVN_CMD% %*
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%
exit /B %ERROR_CODE%

