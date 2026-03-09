package com.minidepo.repository;

import com.minidepo.config.DatabaseConfig;
import com.minidepo.exception.DatabaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Tüm repository sınıfları için temel DB operasyonları.
 */
public abstract class BaseRepository {

    protected final Logger log = LogManager.getLogger(getClass());
    protected final DatabaseConfig db = DatabaseConfig.getInstance();

    protected Connection getConnection() throws SQLException {
        return db.getConnection();
    }

    /**
     * Güvenli kaynak kapatma yardımcısı.
     */
    protected void closeQuietly(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    log.warn("Kaynak kapatılırken hata: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * Tek satır sayısı döndüren sorgular için yardımcı.
     */
    protected int countQuery(String sql) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Count sorgusu başarısız: " + sql, e);
        }
    }
}

