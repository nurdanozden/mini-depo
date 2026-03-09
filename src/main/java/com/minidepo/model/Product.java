package com.minidepo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private int id;                // SERIAL — DB tarafından otomatik atanır
    private String name;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Yeni ürün oluştururken kullanılır (id DB tarafından atanır).
     */
    public Product(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    /**
     * Konsol çıktısı için özelleştirilmiş format.
     */
    @Override
    public String toString() {
        return String.format("  [%d] %-20s | Stok: %-6d | Eklenme: %s",
                id, name, quantity,
                createdAt != null ? createdAt.toLocalDate().toString() : "-");
    }
}

