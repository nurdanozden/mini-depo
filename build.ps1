# =====================================================
#  Mini Depo v2.0 — PowerShell Build & Run Script
#  Java ve Maven'i otomatik bulur/indirir, projeyi derler.
# =====================================================

param(
    [switch]$Run,
    [switch]$Clean
)

$ErrorActionPreference = "Continue"
$ProjectDir = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host ""
Write-Host "========================================"  -ForegroundColor Cyan
Write-Host "  Mini Depo v2.0 - Build Script"         -ForegroundColor Cyan
Write-Host "========================================"  -ForegroundColor Cyan
Write-Host ""

# --- JAVA_HOME Tespiti ---
function Find-Java {
    # 1. Zaten JAVA_HOME set mi?
    if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
        Write-Host "[OK] JAVA_HOME: $env:JAVA_HOME" -ForegroundColor Green
        return $env:JAVA_HOME
    }

    # 2. PATH'te java var mi?
    $javaCmd = Get-Command java -ErrorAction SilentlyContinue
    if ($javaCmd) {
        $jHome = (Split-Path (Split-Path $javaCmd.Source))
        Write-Host "[OK] Java PATH'te bulundu: $jHome" -ForegroundColor Green
        return $jHome
    }

    # 3. IntelliJ IDEA JBR
    $ideaPaths = @(
        "C:\Program Files\JetBrains\IntelliJ IDEA 2025.2\jbr",
        "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.2\jbr",
        "C:\Program Files\JetBrains\IntelliJ IDEA 2024.3\jbr",
        "C:\Program Files\JetBrains\IntelliJ IDEA 2024.2\jbr"
    )
    foreach ($p in $ideaPaths) {
        if (Test-Path "$p\bin\java.exe") {
            Write-Host "[OK] IntelliJ JBR bulundu: $p" -ForegroundColor Green
            return $p
        }
    }

    # 4. Standart konumlar
    $searchPaths = @(
        "C:\Program Files\Java",
        "C:\Program Files\Eclipse Adoptium",
        "C:\Program Files\Oracle\Java",
        "$env:USERPROFILE\.jdks"
    )
    foreach ($base in $searchPaths) {
        if (Test-Path $base) {
            $jdks = Get-ChildItem $base -Directory | Where-Object { $_.Name -match 'jdk|openjdk' } | Sort-Object Name -Descending
            foreach ($jdk in $jdks) {
                if (Test-Path "$($jdk.FullName)\bin\java.exe") {
                    Write-Host "[OK] JDK bulundu: $($jdk.FullName)" -ForegroundColor Green
                    return $jdk.FullName
                }
            }
        }
    }

    Write-Host "[HATA] Java bulunamadi!" -ForegroundColor Red
    Write-Host "  Cozum: JDK 17+ yukleyin veya JAVA_HOME ortam degiskenini ayarlayin." -ForegroundColor Yellow
    Write-Host "  Indirme: https://adoptium.net/temurin/releases/" -ForegroundColor Yellow
    exit 1
}

# --- Maven Tespiti/Indirme ---
function Find-Maven {
    # 1. PATH'te mvn var mi?
    $mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue
    if ($mvnCmd) {
        Write-Host "[OK] Maven PATH'te bulundu." -ForegroundColor Green
        return $mvnCmd.Source
    }

    # 2. Lokal Maven var mı?
    $localMaven = "$env:USERPROFILE\.m2\wrapper\dists\apache-maven-3.9.9\bin\mvn.cmd"
    if (Test-Path $localMaven) {
        Write-Host "[OK] Lokal Maven bulundu." -ForegroundColor Green
        return $localMaven
    }

    # 3. Maven indir
    Write-Host "[INFO] Maven bulunamadi, indiriliyor..." -ForegroundColor Yellow
    $mavenUrl = "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip"
    $mavenZip = "$env:USERPROFILE\.m2\wrapper\dists\maven.zip"
    $mavenDir = "$env:USERPROFILE\.m2\wrapper\dists"

    New-Item -ItemType Directory -Path $mavenDir -Force | Out-Null

    try {
        [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
        $ProgressPreference = 'SilentlyContinue'
        Write-Host "  Indiriliyor: Apache Maven 3.9.9 ..." -ForegroundColor Yellow
        Invoke-WebRequest -Uri $mavenUrl -OutFile $mavenZip -UseBasicParsing
        Write-Host "  Cikartiliyor..." -ForegroundColor Yellow
        Expand-Archive -Path $mavenZip -DestinationPath $mavenDir -Force
        Remove-Item $mavenZip -Force -ErrorAction SilentlyContinue
        $mvnPath = "$mavenDir\apache-maven-3.9.9\bin\mvn.cmd"
        if (Test-Path $mvnPath) {
            Write-Host "[OK] Maven 3.9.9 indirildi!" -ForegroundColor Green
            return $mvnPath
        }
    } catch {
        Write-Host "[HATA] Maven indirilemedi: $_" -ForegroundColor Red
    }

    Write-Host "[HATA] Maven bulunamadi ve indirilemedi!" -ForegroundColor Red
    Write-Host "  Cozum: https://maven.apache.org/download.cgi adresinden indirin." -ForegroundColor Yellow
    exit 1
}

# === ANA AKIS ===

# Java bul
$javaHome = Find-Java
$env:JAVA_HOME = $javaHome
$env:PATH = "$javaHome\bin;$env:PATH"

# Java versiyon kontrol
Write-Host ""
$javaVersion = (& "$javaHome\bin\java.exe" -version 2>&1) | ForEach-Object { $_.ToString() }
Write-Host "  $($javaVersion[0])" -ForegroundColor DarkGray
Write-Host ""

# Maven bul
$mvn = Find-Maven
Write-Host ""

# Build
$buildArgs = @("clean", "compile")
if ($Clean) {
    $buildArgs = @("clean")
}

Write-Host "[BUILD] Proje derleniyor..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

& $mvn @buildArgs -f "$ProjectDir\pom.xml"

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "[HATA] Build basarisiz!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "[OK] Build basarili!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

# Run
if ($Run) {
    Write-Host ""
    Write-Host "[RUN] Uygulama baslatiliyor..." -ForegroundColor Cyan
    Write-Host ""
    & $mvn exec:java "-Dexec.mainClass=com.minidepo.Main" -f "$ProjectDir\pom.xml"
}

Write-Host ""


