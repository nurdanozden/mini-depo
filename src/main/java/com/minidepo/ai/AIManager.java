package com.minidepo.ai;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

/**
 * AI Chat modunu yöneten sınıf.
 * Kullanıcının doğal dil komutlarını Agent'a iletir, cevapları ekrana basar.
 */
public class AIManager {

    private static final Logger log = LogManager.getLogger(AIManager.class);
    private InventoryAgent agent;
    private boolean initialized = false;

    public void initialize() {
        if (initialized) return;

        System.out.println("\n🤖 AI Asistan başlatılıyor...");
        try {
            agent = AgentConfiguration.createAgent();
            initialized = true;
            System.out.println("✅ AI Asistan hazır!\n");
        } catch (Exception e) {
            System.out.println("❌ AI başlatılamadı: " + e.getMessage());
            log.error("AI Agent başlatma hatası", e);
        }
    }

    public boolean isInitialized() {
        return initialized;
    }

    /**
     * AI Chat döngüsünü başlatır. 'çıkış' yazılana kadar devam eder.
     */
    public void startChatLoop(Scanner scanner) {
        if (!initialized) {
            initialize();
            if (!initialized) {
                System.out.println("⚠️  AI modu kullanılamıyor. Ana menüye dönülüyor...\n");
                return;
            }
        }

        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║           🤖 AI DEPO ASİSTANI — CHAT MODU           ║");
        System.out.println("╠═══════════════════════════════════════════════════════╣");
        System.out.println("║  Doğal dil ile deponu yönet!                         ║");
        System.out.println("║  Örnek komutlar:                                     ║");
        System.out.println("║    • \"Depoda ne var?\"                                ║");
        System.out.println("║    • \"Kanka biten ürün var mı?\"                      ║");
        System.out.println("║    • \"Vida stokunu 500 yap\"                          ║");
        System.out.println("║    • \"50 adet cıvata ekle\"                           ║");
        System.out.println("║                                                       ║");
        System.out.println("║  Çıkmak için 'çıkış' veya 'exit' yaz                ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝\n");

        while (true) {
            System.out.print("🧑 Sen: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;

            if (input.equalsIgnoreCase("çıkış") || input.equalsIgnoreCase("cikis")
                    || input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("q")) {
                System.out.println("\n🤖 AI modu kapatıldı. Ana menüye dönülüyor...\n");
                break;
            }

            try {
                System.out.println("\n🤖 Asistan düşünüyor...\n");
                String response = agent.chat(input);
                System.out.println("🤖 Asistan: " + response);
                System.out.println();
            } catch (Exception e) {
                System.out.println("❌ Hata: " + e.getMessage());
                log.error("AI chat hatası", e);
                System.out.println();
            }
        }
    }

    /**
     * Tek seferlik soru sorma (menüden çıkmadan).
     */
    public String ask(String question) {
        if (!initialized) {
            return "AI Asistan henüz başlatılmadı!";
        }
        try {
            return agent.chat(question);
        } catch (Exception e) {
            log.error("AI soru hatası", e);
            return "Hata: " + e.getMessage();
        }
    }
}

