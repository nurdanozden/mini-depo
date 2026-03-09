package com.minidepo.ai;

import com.minidepo.model.Product;
import com.minidepo.service.WarehouseService;
import dev.langchain4j.agent.tool.Tool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * LangChain4j Tool sınıfı — AI Agent bu metodları doğrudan tetikleyebilir.
 */
public class InventoryTools {

    private static final Logger log = LogManager.getLogger(InventoryTools.class);
    private final WarehouseService warehouseService = WarehouseService.getInstance();

    @Tool("Depodaki tüm ürünleri listeler. Her ürünün id, isim, stok miktarı ve eklenme tarihini döndürür.")
    public String tumUrunleriListele() {
        log.debug("AI Tool çağrıldı: tumUrunleriListele");
        List<Product> products = warehouseService.listProducts();

        if (products.isEmpty()) {
            return "Depoda hiç ürün bulunmuyor.";
        }

        StringBuilder sb = new StringBuilder("Depodaki ürünler:\n");
        for (Product p : products) {
            sb.append(String.format("- ID:%d | %s | Stok: %d\n", p.getId(), p.getName(), p.getQuantity()));
        }
        sb.append(String.format("\nToplam: %d çeşit ürün", products.size()));
        return sb.toString();
    }

    @Tool("İsme göre stok sorgular. Verilen ürün adını depoda arar ve stok bilgisini döndürür. Parametre: ürün adı")
    public String stokSorgula(String name) {
        log.debug("AI Tool çağrıldı: stokSorgula({})", name);
        List<Product> results = warehouseService.searchByName(name);

        if (results.isEmpty()) {
            return "'" + name + "' ile eşleşen ürün bulunamadı.";
        }

        return results.stream()
                .map(p -> String.format("- %s (ID:%d): %d adet stokta", p.getName(), p.getId(), p.getQuantity()))
                .collect(Collectors.joining("\n"));
    }

    @Tool("Kritik stok seviyesinin altındaki ürünlerin raporunu döndürür. Stoku düşük olan, tükenmek üzere olan veya biten ürünleri gösterir.")
    public String kritikStokRaporu() {
        log.debug("AI Tool çağrıldı: kritikStokRaporu");
        List<Product> critical = warehouseService.getCriticalStock();
        int threshold = warehouseService.getCriticalThreshold();

        if (critical.isEmpty()) {
            return String.format("Tüm ürünlerin stoğu güvenli seviyede (eşik: %d). Kritik stokta ürün yok!", threshold);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("⚠️ KRİTİK STOK RAPORU (eşik: ≤%d):\n", threshold));
        for (Product p : critical) {
            String status = p.getQuantity() == 0 ? "❌ TÜKENDİ" : "⚠️ KRİTİK";
            sb.append(String.format("  %s | %s (ID:%d): %d adet kaldı\n", status, p.getName(), p.getId(), p.getQuantity()));
        }
        sb.append(String.format("\nToplam %d ürün kritik seviyede!", critical.size()));
        return sb.toString();
    }

    @Tool("Depo hakkında genel istatistik ve özet bilgi verir: toplam ürün sayısı, toplam stok, kritik stok durumu.")
    public String depoOzeti() {
        log.debug("AI Tool çağrıldı: depoOzeti");
        int totalProducts = warehouseService.listProducts().size();
        List<Product> critical = warehouseService.getCriticalStock();
        int threshold = warehouseService.getCriticalThreshold();

        int totalStock = warehouseService.listProducts().stream()
                .mapToInt(Product::getQuantity)
                .sum();

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Depo Özeti:\n"));
        sb.append(String.format("- Toplam ürün çeşidi: %d\n", totalProducts));
        sb.append(String.format("- Toplam stok miktarı: %d adet\n", totalStock));
        sb.append(String.format("- Kritik stok (≤%d): %d ürün\n", threshold, critical.size()));

        if (!critical.isEmpty()) {
            sb.append("- Kritik ürünler: ");
            sb.append(critical.stream()
                    .map(p -> p.getName() + "(" + p.getQuantity() + ")")
                    .collect(Collectors.joining(", ")));
        }
        return sb.toString();
    }

    @Tool("Depoya yeni ürün ekler. Parametreler: ürün adı ve stok miktarı.")
    public String urunEkle(String name, int quantity) {
        log.debug("AI Tool çağrıldı: urunEkle({}, {})", name, quantity);
        try {
            Product p = warehouseService.addProduct(name, quantity);
            return String.format("✅ Ürün başarıyla eklendi → %s | Stok: %d | ID: %d", p.getName(), p.getQuantity(), p.getId());
        } catch (Exception e) {
            return "❌ Ürün eklenemedi: " + e.getMessage();
        }
    }

    @Tool("ID ile depodaki bir ürünün stok miktarını günceller. Parametreler: ürün ID ve yeni stok miktarı.")
    public String stokGuncelle(int id, int newQuantity) {
        log.debug("AI Tool çağrıldı: stokGuncelle({}, {})", id, newQuantity);
        try {
            warehouseService.updateQuantity(id, newQuantity);
            return String.format("✅ Stok güncellendi → ID:%d yeni stok: %d", id, newQuantity);
        } catch (Exception e) {
            return "❌ Güncelleme başarısız: " + e.getMessage();
        }
    }

    @Tool("ID ile depodaki bir ürünü siler. Parametre: silinecek ürünün ID numarası.")
    public String urunSil(int id) {
        log.debug("AI Tool çağrıldı: urunSil({})", id);
        try {
            warehouseService.removeProduct(id);
            return String.format("✅ Ürün silindi → ID:%d", id);
        } catch (Exception e) {
            return "❌ Silme başarısız: " + e.getMessage();
        }
    }
}

