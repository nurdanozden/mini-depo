package com.minidepo;

import com.minidepo.ai.AIManager;
import com.minidepo.config.DatabaseConfig;
import com.minidepo.exception.DatabaseException;
import com.minidepo.exception.ProductNotFoundException;
import com.minidepo.model.Product;
import com.minidepo.service.WarehouseService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);
    private static final Scanner scanner = new Scanner(System.in);
    private static WarehouseService warehouseService;
    private static AIManager aiManager;

    public static void main(String[] args) {
        printBanner();

        try {
            // Veritabanı & Servis başlat
            warehouseService = WarehouseService.getInstance();
            aiManager = new AIManager();

            log.info("Mini Depo v2.0 başlatıldı.");
            System.out.println("✅ Sistem hazır!\n");

            mainLoop();

        } catch (DatabaseException e) {
            System.out.println("\n❌ Veritabanı bağlantı hatası!");
            System.out.println("   → PostgreSQL çalışıyor mu? 'mini_depo' veritabanı oluşturuldu mu?");
            System.out.println("   → Bağlantı ayarları: src/main/resources/application.properties");
            System.out.println("\n   Hata detayı: " + e.getMessage());
            log.error("Veritabanı başlatma hatası", e);
        } catch (Exception e) {
            System.out.println("\n❌ Beklenmeyen hata: " + e.getMessage());
            log.error("Kritik hata", e);
        } finally {
            shutdown();
        }
    }

    private static void mainLoop() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleAddProduct();
                case "2" -> handleRemoveProduct();
                case "3" -> handleUpdateQuantity();
                case "4" -> handleListProducts();
                case "5" -> handleSearchProduct();
                case "6" -> handleCriticalStock();
                case "7" -> handleStats();
                case "8" -> handleAIMode();
                case "0" -> {
                    System.out.println("\n👋 Güle güle! Program kapatılıyor...");
                    return;
                }
                default -> System.out.println("\n⚠️  Geçersiz seçim! Tekrar dene.\n");
            }
        }
    }

    // ═══════════════════════════════════════════════════════
    //  MENÜ & BANNER
    // ═══════════════════════════════════════════════════════

    private static void printBanner() {
        System.out.println("""
                
                ╔═══════════════════════════════════════════════════════╗
                ║                                                       ║
                ║          📦  M I N I   D E P O   v 2 . 0  📦        ║
                ║          ─────────────────────────────────            ║
                ║       AI Destekli Depo Yönetim Sistemi                ║
                ║       PostgreSQL + LangChain4j                        ║
                ║                                                       ║
                ╚═══════════════════════════════════════════════════════╝
                """);
    }

    private static void printMenu() {
        System.out.println("""
                ┌───────────────── ANA MENÜ ─────────────────┐
                │  1  │  Ürün Ekle                            │
                │  2  │  Ürün Sil                             │
                │  3  │  Stok Güncelle                        │
                │  4  │  Ürünleri Listele                     │
                │  5  │  Ürün Ara (isme göre)                 │
                │  6  │  Kritik Stok Raporu                   │
                │  7  │  Depo İstatistikleri                  │
                │  8  │  🤖 AI Asistan Modu                  │
                │  0  │  Çıkış                                │
                └─────────────────────────────────────────────┘""");
        System.out.print("  Seçim: ");
    }

    // ═══════════════════════════════════════════════════════
    //  HANDLER'LAR
    // ═══════════════════════════════════════════════════════

    private static void handleAddProduct() {
        System.out.println("\n── Yeni Ürün Ekle ──");
        System.out.print("  Ürün adı: ");
        String name = scanner.nextLine().trim();

        System.out.print("  Stok miktarı: ");
        int quantity = readInt();

        try {
            Product product = warehouseService.addProduct(name, quantity);
            System.out.println("\n  ✅ Ürün eklendi → " + product);
        } catch (DatabaseException e) {
            System.out.println("\n  ❌ " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("\n  ⚠️  " + e.getMessage());
        }
        System.out.println();
    }

    private static void handleRemoveProduct() {
        System.out.println("\n── Ürün Sil ──");
        System.out.print("  Silinecek ürün ID: ");
        int id = readInt();

        try {
            warehouseService.removeProduct(id);
            System.out.println("\n  ✅ Ürün silindi (ID: " + id + ")");
        } catch (ProductNotFoundException e) {
            System.out.println("\n  ❌ " + e.getMessage());
        }
        System.out.println();
    }

    private static void handleUpdateQuantity() {
        System.out.println("\n── Stok Güncelle ──");
        System.out.print("  Ürün ID: ");
        int id = readInt();

        System.out.print("  Yeni stok miktarı: ");
        int qty = readInt();

        try {
            warehouseService.updateQuantity(id, qty);
            System.out.println("\n  ✅ Stok güncellendi (ID: " + id + " → " + qty + ")");
        } catch (ProductNotFoundException e) {
            System.out.println("\n  ❌ " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("\n  ⚠️  " + e.getMessage());
        }
        System.out.println();
    }

    private static void handleListProducts() {
        System.out.println("\n── Tüm Ürünler ──");
        List<Product> products = warehouseService.listProducts();

        if (products.isEmpty()) {
            System.out.println("  Depo boş. Henüz ürün eklenmemiş.");
        } else {
            products.forEach(p -> System.out.println(p));
            System.out.printf("\n  Toplam: %d ürün\n", products.size());
        }
        System.out.println();
    }

    private static void handleSearchProduct() {
        System.out.println("\n── Ürün Ara ──");
        System.out.print("  Aranacak isim: ");
        String name = scanner.nextLine().trim();

        List<Product> results = warehouseService.searchByName(name);
        if (results.isEmpty()) {
            System.out.println("  Sonuç bulunamadı.");
        } else {
            results.forEach(p -> System.out.println(p));
            System.out.printf("\n  %d sonuç bulundu.\n", results.size());
        }
        System.out.println();
    }

    private static void handleCriticalStock() {
        System.out.println("\n── Kritik Stok Raporu ──");
        List<Product> critical = warehouseService.getCriticalStock();

        if (critical.isEmpty()) {
            System.out.println("  ✅ Tüm ürünler güvenli stok seviyesinde!");
        } else {
            System.out.printf("  ⚠️  %d ürün kritik stok seviyesinde (≤%d):\n\n",
                    critical.size(), warehouseService.getCriticalThreshold());
            for (Product p : critical) {
                String icon = p.getQuantity() == 0 ? "❌" : "⚠️";
                System.out.printf("  %s %s\n", icon, p);
            }
        }
        System.out.println();
    }

    private static void handleStats() {
        System.out.println();
        System.out.println(warehouseService.getStats());
        System.out.println();
    }

    private static void handleAIMode() {
        aiManager.startChatLoop(scanner);
    }

    // ═══════════════════════════════════════════════════════
    //  YARDIMCI METOTLAR
    // ═══════════════════════════════════════════════════════

    private static int readInt() {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("  ⚠️  Geçerli bir sayı girin: ");
            }
        }
    }

    private static void shutdown() {
        try {
            DatabaseConfig.getInstance().shutdown();
            log.info("Mini Depo kapatıldı.");
        } catch (Exception e) {
            // Zaten kapanıyor, sessiz geç
        }
        scanner.close();
    }
}

