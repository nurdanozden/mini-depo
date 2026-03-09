package com.minidepo;

import com.minidepo.model.Product;
import com.minidepo.service.WarehouseService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Veritabanına 30 örnek ürün ekler.
 * Çalıştırma: mvn exec:java -Dexec.mainClass="com.minidepo.DataSeeder"
 */
public class DataSeeder {

    private static final Logger log = LogManager.getLogger(DataSeeder.class);

    // 30 ürün: isim → stok
    private static final Object[][] SEED_DATA = {
        {"M6 Altıköşe Vida",          2500},
        {"M8 Altıköşe Vida",          1800},
        {"M10 Altıköşe Vida",         1200},
        {"M6 Somun",                  3000},
        {"M8 Somun",                  2200},
        {"M10 Somun",                 1500},
        {"M6 Yaylı Rondela",          4000},
        {"M8 Düz Rondela",            3500},
        {"M6 Cıvata (50mm)",          900},
        {"M8 Cıvata (80mm)",          750},
        {"1.5mm Sac Metal Levha",     45},
        {"3mm Çelik Levha",           30},
        {"6mm Alüminyum Profil",      120},
        {"40x40 Kare Profil",         85},
        {"50x30 Dikdörtgen Boru",     60},
        {"Ø10 Çelik Çubuk",           200},
        {"Ø8 Paslanmaz Çubuk",        150},
        {"Rulmanlı Yatak UCP205",     35},
        {"Rulmanlı Yatak UCP208",     20},
        {"6205 ZZ Rulman",            80},
        {"6208 ZZ Rulman",            55},
        {"Kayış-Kasnak A-52",         40},
        {"Elastik Kaplin 28mm",       25},
        {"Zincir Dişlisi Z=20",       18},
        {"Krank Mili Keçesi 35x62",   60},
        {"Hidrolik Conta Seti",       7},
        {"O-Ring 50x3",               500},
        {"Paslanmaz Boru Kelepçesi",  320},
        {"Pnömatik Bağlantı 6mm",    95},
        {"Elektrovalf 24V DC",        12}
    };

    public static void main(String[] args) {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║     Mini Depo — Veri Yükleme          ║");
        System.out.println("╚═══════════════════════════════════════╝\n");

        WarehouseService service = WarehouseService.getInstance();

        int added = 0, skipped = 0;

        for (Object[] row : SEED_DATA) {
            String name = (String) row[0];
            int quantity = (int) row[1];

            try {
                Product p = service.addProduct(name, quantity);
                System.out.printf("  ✅ [%2d] %-35s → Stok: %d%n", p.getId(), p.getName(), p.getQuantity());
                added++;
            } catch (Exception e) {
                System.out.printf("  ⚠️  Atlandı (zaten var): %-35s%n", name);
                skipped++;
            }
        }

        System.out.println("\n────────────────────────────────────────");
        System.out.printf("  ✅ Eklenen  : %d ürün%n", added);
        System.out.printf("  ⚠️  Atlanan  : %d ürün (zaten vardı)%n", skipped);
        System.out.println("────────────────────────────────────────");

        System.out.println("\n📋 Mevcut Depo Durumu:\n");
        List<Product> all = service.listProducts();
        all.forEach(System.out::println);
        System.out.println("\n  Toplam: " + all.size() + " ürün\n");

        com.minidepo.config.DatabaseConfig.getInstance().shutdown();
        System.out.println("✅ Veri yükleme tamamlandı!\n");
    }
}

