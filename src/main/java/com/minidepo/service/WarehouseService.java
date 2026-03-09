package com.minidepo.service;

import com.minidepo.config.AppConfig;
import com.minidepo.exception.ProductNotFoundException;
import com.minidepo.model.Product;
import com.minidepo.repository.ProductRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Depo iş mantığı — Singleton Service.
 */
public class WarehouseService {

    private static final Logger log = LogManager.getLogger(WarehouseService.class);
    private static WarehouseService instance;

    private final ProductRepository repository;
    private final int criticalThreshold;

    private WarehouseService() {
        this.repository = new ProductRepository();
        this.criticalThreshold = AppConfig.getInstance().getInt("stock.critical.threshold", 10);
        log.info("WarehouseService başlatıldı (kritik stok eşiği: {})", criticalThreshold);
    }

    public static synchronized WarehouseService getInstance() {
        if (instance == null) {
            instance = new WarehouseService();
        }
        return instance;
    }

    // ─── ÜRÜN EKLE ──────────────────────────────────────
    public Product addProduct(String name, int quantity) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Ürün adı boş olamaz!");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Stok miktarı negatif olamaz!");
        }

        Product product = new Product(name.trim(), quantity);
        return repository.save(product);
    }

    // ─── ÜRÜN SİL ───────────────────────────────────────
    public void removeProduct(int id) {
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new ProductNotFoundException(id);
        }
    }

    // ─── STOK GÜNCELLE ──────────────────────────────────
    public void updateQuantity(int id, int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Stok miktarı negatif olamaz!");
        }
        boolean updated = repository.updateQuantity(id, newQuantity);
        if (!updated) {
            throw new ProductNotFoundException(id);
        }
    }

    // ─── TÜM ÜRÜNLERİ LİSTELE ─────────────────────────
    public List<Product> listProducts() {
        return repository.findAll();
    }

    // ─── İSME GÖRE ARA ─────────────────────────────────
    public List<Product> searchByName(String name) {
        return repository.findByName(name);
    }

    // ─── ID'YE GÖRE BUL ────────────────────────────────
    public Product findById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    // ─── KRİTİK STOK RAPORU ────────────────────────────
    public List<Product> getCriticalStock() {
        return repository.findCriticalStock(criticalThreshold);
    }

    // ─── DEPO İSTATİSTİKLERİ ────────────────────────────
    public String getStats() {
        int totalProducts = repository.count();
        int totalStock = repository.totalStock();
        int criticalCount = getCriticalStock().size();

        return String.format("""
                ┌─────────────── DEPO İSTATİSTİKLERİ ───────────────┐
                │  Toplam Ürün Çeşidi  : %-5d                      │
                │  Toplam Stok Miktarı : %-5d                      │
                │  Kritik Stok (<=%d)   : %-5d                      │
                └───────────────────────────────────────────────────┘""",
                totalProducts, totalStock, criticalThreshold, criticalCount);
    }

    public int getCriticalThreshold() {
        return criticalThreshold;
    }
}

