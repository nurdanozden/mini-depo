package com.minidepo.repository;

import com.minidepo.exception.DatabaseException;
import com.minidepo.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepository extends BaseRepository {

    // ─── CREATE ──────────────────────────────────────────
    public Product save(Product product) {
        String sql = "INSERT INTO products (name, quantity) VALUES (?, ?) RETURNING id, created_at, updated_at";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setInt(2, product.getQuantity());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    product.setId(rs.getInt("id"));
                    product.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    product.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                }
            }
            log.info("Ürün eklendi → {} (ID: {})", product.getName(), product.getId());
            return product;

        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().equals("23505")) {
                throw new DatabaseException("Bu isimde bir ürün zaten var: " + product.getName(), e);
            }
            throw new DatabaseException("Ürün eklenirken hata oluştu", e);
        }
    }

    // ─── READ ALL ────────────────────────────────────────
    public List<Product> findAll() {
        String sql = "SELECT id, name, quantity, created_at, updated_at FROM products ORDER BY id";
        List<Product> products = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                products.add(mapRow(rs));
            }
            return products;

        } catch (SQLException e) {
            throw new DatabaseException("Ürünler listelenirken hata", e);
        }
    }

    // ─── READ BY ID ──────────────────────────────────────
    public Optional<Product> findById(int id) {
        String sql = "SELECT id, name, quantity, created_at, updated_at FROM products WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("Ürün aranırken hata (ID: " + id + ")", e);
        }
    }

    // ─── READ BY NAME (LIKE) ─────────────────────────────
    public List<Product> findByName(String name) {
        String sql = "SELECT id, name, quantity, created_at, updated_at FROM products WHERE LOWER(name) LIKE LOWER(?) ORDER BY id";
        List<Product> products = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }
            return products;

        } catch (SQLException e) {
            throw new DatabaseException("Ürün aranırken hata (isim: " + name + ")", e);
        }
    }

    // ─── FIND CRITICAL STOCK ─────────────────────────────
    public List<Product> findCriticalStock(int threshold) {
        String sql = "SELECT id, name, quantity, created_at, updated_at FROM products WHERE quantity <= ? ORDER BY quantity ASC";
        List<Product> products = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, threshold);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }
            return products;

        } catch (SQLException e) {
            throw new DatabaseException("Kritik stok sorgulanırken hata", e);
        }
    }

    // ─── UPDATE ──────────────────────────────────────────
    public boolean updateQuantity(int id, int newQuantity) {
        String sql = "UPDATE products SET quantity = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newQuantity);
            ps.setInt(2, id);
            int affected = ps.executeUpdate();

            if (affected > 0) {
                log.info("Stok güncellendi → ID: {}, Yeni Stok: {}", id, newQuantity);
                return true;
            }
            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Stok güncellenirken hata (ID: " + id + ")", e);
        }
    }

    // ─── DELETE ──────────────────────────────────────────
    public boolean deleteById(int id) {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int affected = ps.executeUpdate();

            if (affected > 0) {
                log.info("Ürün silindi → ID: {}", id);
                return true;
            }
            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Ürün silinirken hata (ID: " + id + ")", e);
        }
    }

    // ─── TOPLAM ÜRÜN SAYISI ──────────────────────────────
    public int count() {
        return countQuery("SELECT COUNT(*) FROM products");
    }

    // ─── TOPLAM STOK MİKTARI ─────────────────────────────
    public int totalStock() {
        return countQuery("SELECT COALESCE(SUM(quantity), 0) FROM products");
    }

    // ─── ROW MAPPER ──────────────────────────────────────
    private Product mapRow(ResultSet rs) throws SQLException {
        return Product.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .quantity(rs.getInt("quantity"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }
}

