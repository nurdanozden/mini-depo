-- =====================================================
--  Mini Depo v2.0 — PostgreSQL Kurulum Script'i
-- =====================================================
--  Bu scripti pgAdmin veya psql ile çalıştırın.
--  Komut: psql -U postgres -f init.sql
-- =====================================================

-- Veritabanı oluştur (eğer yoksa)
SELECT 'CREATE DATABASE mini_depo'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'mini_depo')\gexec

-- Bağlan
\c mini_depo

-- Tablo oluştur
CREATE TABLE IF NOT EXISTS products (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    quantity    INTEGER NOT NULL DEFAULT 0,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Otomatik updated_at trigger'ı
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_update_timestamp ON products;

CREATE TRIGGER trg_update_timestamp
    BEFORE UPDATE ON products
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

-- =====================================================
--  30 Örnek Ürün (Bağlantı & Makine Elemanları)
-- =====================================================
INSERT INTO products (name, quantity) VALUES
    ('M6 Altıköşe Vida',         2500),
    ('M8 Altıköşe Vida',         1800),
    ('M10 Altıköşe Vida',        1200),
    ('M6 Somun',                 3000),
    ('M8 Somun',                 2200),
    ('M10 Somun',                1500),
    ('M6 Yaylı Rondela',         4000),
    ('M8 Düz Rondela',           3500),
    ('M6 Cıvata (50mm)',         900),
    ('M8 Cıvata (80mm)',         750),
    ('1.5mm Sac Metal Levha',    45),
    ('3mm Çelik Levha',          30),
    ('6mm Alüminyum Profil',     120),
    ('40x40 Kare Profil',        85),
    ('50x30 Dikdörtgen Boru',    60),
    ('Ø10 Çelik Çubuk',          200),
    ('Ø8 Paslanmaz Çubuk',       150),
    ('Rulmanlı Yatak UCP205',    35),
    ('Rulmanlı Yatak UCP208',    20),
    ('6205 ZZ Rulman',           80),
    ('6208 ZZ Rulman',           55),
    ('Kayış-Kasnak A-52',        40),
    ('Elastik Kaplin 28mm',      25),
    ('Zincir Dişlisi Z=20',      18),
    ('Krank Mili Keçesi 35x62',  60),
    ('Hidrolik Conta Seti',      7),
    ('O-Ring 50x3',              500),
    ('Paslanmaz Boru Kelepçesi', 320),
    ('Pnömatik Bağlantı 6mm',   95),
    ('Elektrovalf 24V DC',       12)
ON CONFLICT (name) DO UPDATE SET quantity = EXCLUDED.quantity;

-- Kontrol
SELECT id, name, quantity FROM products ORDER BY id;
