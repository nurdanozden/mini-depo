<div align="center">

# 📦 Mini Depo v2.0

### AI Destekli Profesyonel Depo Yönetim Sistemi

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)](https://adoptium.net/)
[![Maven](https://img.shields.io/badge/Maven-3.9.9-red?style=flat-square&logo=apachemaven)](https://maven.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![LangChain4j](https://img.shields.io/badge/LangChain4j-0.36.2-green?style=flat-square)](https://github.com/langchain4j/langchain4j)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)

*Doğal dil komutlarıyla deponuzu yönetin. "Kanka biten bir şey var mı?" sorusunu sormalısınız yeter.*

</div>

---

## 🎯 Proje Hakkında

**Mini Depo**, sıfırdan profesyonel mimariye dönüştürülmüş bir **konsol tabanlı depo yönetim sistemidir**. Klasik Java'dan başlayıp **PostgreSQL + HikariCP + LangChain4j** entegrasyonuyla modern bir uygulama haline getirilmiştir.

Projenin en öne çıkan özelliği, **AI Agent entegrasyonu**dur: Kullanıcı Türkçe doğal dil komutları yazabilir, sistem bu komutları anlayıp otomatik olarak doğru veritabanı fonksiyonunu tetikler.

---

## ✨ Özellikler

### 🖥️ Klasik Mod
| # | Özellik | Açıklama |
|---|---------|----------|
| 1 | Ürün Ekle | İsim ve stok miktarıyla yeni ürün |
| 2 | Ürün Sil | ID ile ürün silme |
| 3 | Stok Güncelle | ID ile mevcut stoğu değiştir |
| 4 | Ürün Listele | Tüm ürünleri tabloyla görüntüle |
| 5 | İsme Göre Ara | LIKE tabanlı arama (kısmi eşleşme) |
| 6 | Kritik Stok Raporu | Eşik altındaki ürünleri listele |
| 7 | Depo İstatistikleri | Toplam ürün, stok ve kritik özeti |

### 🤖 AI Modu — LangChain4j Agent
- **Doğal dil ile depo yönetimi** — Türkçe yazmanız yeterli
- **Tool Calling** — AI, 7 farklı veritabanı fonksiyonunu otomatik tetikler
- **Akıllı stok analizi** — "Bitmek üzere olan ürünler" gibi sezgisel sorgular
- **GPT-4o-mini** entegrasyonu, düşük maliyet + yüksek doğruluk

**Örnek Konuşmalar:**
```
Sen: Kanka depo ne durumda?
🤖: Depoda 30 çeşit ürün var, toplam 28.847 adet stok...

Sen: Biten veya azalan ürün var mı?
🤖: ⚠️ 4 ürün kritik seviyede: Hidrolik Conta Seti(7), Elektrovalf 24V DC(12)...

Sen: Elastik Kaplin stokuna 100 adet daha ekle
🤖: ✅ Stok güncellendi → Elastik Kaplin 28mm: 125 adet

Sen: Rulmanlı Yatak UCP208 kaç tane kalmış?
🤖: Rulmanlı Yatak UCP208 (ID:19): 20 adet stokta.
```

---

## 🏗️ Mimari

```
┌─────────────────────────────────────────────────────────┐
│                     Main.java                           │
│              (Konsol UI + Menü Yönetimi)                │
└───────────────┬─────────────────┬───────────────────────┘
                │                 │
    ┌───────────▼──────┐  ┌───────▼──────────────────┐
    │ WarehouseService │  │      AIManager           │
    │   (Singleton)    │  │  (LangChain4j Agent)     │
    └───────────┬──────┘  └───────┬──────────────────┘
                │                 │
    ┌───────────▼──────┐  ┌───────▼──────────────────┐
    │ProductRepository │  │    InventoryTools        │
    │  (PostgreSQL)    │  │  (@Tool metodları x7)    │
    └───────────┬──────┘  └──────────────────────────┘
                │
    ┌───────────▼──────────────────────────────────────┐
    │         DatabaseConfig (HikariCP Pool)            │
    │         PostgreSQL — products tablosu             │
    └──────────────────────────────────────────────────┘
```

### Katmanlar
```
com.minidepo/
├── Main.java                    # Entry point, 8 seçenekli menü
├── DataSeeder.java              # 30 örnek ürün yükleme scripti
├── ai/
│   ├── InventoryAgent.java      # LangChain4j @SystemMessage arayüzü
│   ├── AgentConfiguration.java  # OpenAI model builder
│   ├── InventoryTools.java      # @Tool metodları (AI tetikler)
│   └── AIManager.java           # Chat döngüsü & yönetimi
├── config/
│   ├── AppConfig.java           # Singleton properties yükleyici
│   └── DatabaseConfig.java      # HikariCP + schema auto-init
├── exception/
│   ├── DatabaseException.java
│   ├── ProductNotFoundException.java
│   └── DuplicateProductException.java
├── model/
│   └── Product.java             # @Data @Builder (Lombok)
├── repository/
│   ├── BaseRepository.java      # Ortak DB yardımcıları
│   └── ProductRepository.java   # Full CRUD + kritik stok sorgusu
└── service/
    └── WarehouseService.java    # İş mantığı (Singleton)
```

---

## 🛠️ Teknoloji Stack

| Teknoloji | Versiyon | Kullanım |
|-----------|----------|----------|
| **Java** | 21 | Ana geliştirme dili |
| **Maven** | 3.9.9 | Build & bağımlılık yönetimi |
| **PostgreSQL** | 17 | İlişkisel veritabanı |
| **HikariCP** | 6.2.1 | Connection pooling |
| **LangChain4j** | 0.36.2 | AI Agent framework |
| **OpenAI API** | GPT-4o-mini | Dil modeli |
| **Lombok** | 1.18.36 | Boilerplate azaltma |
| **Log4j2** | 2.24.3 | Yapılandırılabilir loglama |

---

## ⚙️ Kurulum & Çalıştırma

### Gereksinimler
- Java 17+
- PostgreSQL 15+
- OpenAI API Key (AI modu için)
- Maven (otomatik indirilir)

### 1️⃣ Depoyu Klonla
```bash
git clone https://github.com/KULLANICIADI/mini-depo.git
cd mini-depo
```

### 2️⃣ PostgreSQL Veritabanı Oluştur
```sql
-- pgAdmin veya psql ile:
CREATE DATABASE mini_depo;
```

Veya hazır SQL scriptiyle:
```bash
psql -U postgres -f init.sql
```

### 3️⃣ Yapılandırma
`src/main/resources/application.properties` dosyasını düzenle:

```properties
db.url=jdbc:postgresql://localhost:5432/mini_depo
db.username=postgres
db.password=SENIN_SIFREN

# AI modu için OpenAI API Key:
openai.api.key=sk-XXXXXXXXXXXXXXXX
openai.model=gpt-4o-mini
```

### 4️⃣ Örnek Verileri Yükle (Opsiyonel)
30 adet örnek ürünü otomatik ekler:
```bash
.\build.ps1          # önce derle
# ardından DataSeeder çalıştır (IntelliJ'den veya Maven ile)
mvn exec:java -Dexec.mainClass="com.minidepo.DataSeeder"
```

### 5️⃣ Uygulamayı Başlat

**Windows (PowerShell):**
```powershell
.\build.ps1 -Run
```

**Manuel Maven:**
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.minidepo.Main"
```

---

## 📋 Örnek Depo Ürünleri (Seed Data)

Proje, aşağıdaki 30 makine & bağlantı elemanı örneğiyle gelir:

| # | Ürün | Stok |
|---|------|------|
| 1 | M6 Altıköşe Vida | 2.500 |
| 2 | M8 Altıköşe Vida | 1.800 |
| 3 | M10 Altıköşe Vida | 1.200 |
| 4 | M6 Somun | 3.000 |
| 5 | M8 Somun | 2.200 |
| 6 | M6 Yaylı Rondela | 4.000 |
| 7 | M8 Düz Rondela | 3.500 |
| 8 | M6 Cıvata (50mm) | 900 |
| 9 | 1.5mm Sac Metal Levha | 45 |
| 10 | 3mm Çelik Levha | 30 |
| 11 | 6mm Alüminyum Profil | 120 |
| 12 | 40x40 Kare Profil | 85 |
| 13 | Rulmanlı Yatak UCP205 | 35 |
| 14 | Rulmanlı Yatak UCP208 | 20 ⚠️ |
| 15 | 6205 ZZ Rulman | 80 |
| 16 | Kayış-Kasnak A-52 | 40 |
| 17 | Elastik Kaplin 28mm | 25 ⚠️ |
| 18 | Zincir Dişlisi Z=20 | 18 ⚠️ |
| 19 | Hidrolik Conta Seti | 7 🔴 |
| 20 | O-Ring 50x3 | 500 |
| 21 | Paslanmaz Boru Kelepçesi | 320 |
| 22 | Pnömatik Bağlantı 6mm | 95 |
| 23 | Elektrovalf 24V DC | 12 ⚠️ |
| ... | *+7 daha* | |

> 🔴 Kritik stok (≤10) &nbsp; ⚠️ Düşük stok (≤25)

---

## 🤖 AI Tool Calling — Nasıl Çalışır?

```
Kullanıcı yazar: "M8 vida kaç tane kaldı?"
        │
        ▼
  InventoryAgent (@SystemMessage ile yapılandırılmış)
        │
        ▼
  LangChain4j → OpenAI GPT-4o-mini
        │  "stokSorgula('M8 Altıköşe Vida') çağırmalıyım"
        ▼
  InventoryTools.stokSorgula("M8")  ← @Tool metodu tetiklendi
        │
        ▼
  PostgreSQL: SELECT ... WHERE name LIKE '%M8%'
        │
        ▼
  "M8 Altıköşe Vida (ID:2): 1800 adet stokta."
```

AI'nın kullanabileceği **7 Tool**:
1. `tumUrunleriListele()` — tüm ürünleri döndür
2. `stokSorgula(name)` — isme göre stok sorgula
3. `kritikStokRaporu()` — eşik altı ürünler
4. `depoOzeti()` — genel istatistik
5. `urunEkle(name, qty)` — yeni ürün ekle
6. `stokGuncelle(id, qty)` — stoğu güncelle
7. `urunSil(id)` — ürün sil

---

## 📁 Proje Yapısı (Tam)

```
mini-depo/
├── 📄 pom.xml                          # Maven yapılandırması
├── 📄 init.sql                         # PostgreSQL kurulum + 30 ürün
├── 📄 build.ps1                        # Windows build & run scripti
├── 📄 build.cmd                        # CMD build scripti
├── 📄 run.cmd                          # Hızlı çalıştırma
├── 📂 src/main/
│   ├── 📂 java/com/minidepo/
│   │   ├── Main.java
│   │   ├── DataSeeder.java
│   │   ├── ai/                         # LangChain4j entegrasyonu
│   │   ├── config/                     # DB + App yapılandırması
│   │   ├── exception/                  # Custom exceptions
│   │   ├── model/                      # Product (Lombok)
│   │   ├── repository/                 # PostgreSQL CRUD
│   │   └── service/                    # İş mantığı
│   └── 📂 resources/
│       ├── application.properties
│       └── log4j2.xml
└── 📂 logs/                            # Uygulama logları
```

---

## 🔒 Güvenlik Notu

> ⚠️ `application.properties` dosyasına gerçek şifre ve API key eklemeyin.  
> Bunları ortam değişkenlerine taşımak için:

```properties
db.password=${DB_PASSWORD}
openai.api.key=${OPENAI_API_KEY}
```

---

## 📝 Lisans

MIT License — Dilediğiniz gibi kullanabilirsiniz.

---

<div align="center">

**Mini Depo v1.0** → basit txt dosyası  
**Mini Depo v2.0** → PostgreSQL + AI Agent 🚀

*Made with ☕ Java*

</div>
