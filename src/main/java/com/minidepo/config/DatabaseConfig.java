package com.minidepo.config;

import com.minidepo.exception.DatabaseException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {

    private static final Logger log = LogManager.getLogger(DatabaseConfig.class);
    private static DatabaseConfig instance;
    private final HikariDataSource dataSource;

    private DatabaseConfig() {
        AppConfig config = AppConfig.getInstance();

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.get("db.url"));
        hikariConfig.setUsername(config.get("db.username"));
        hikariConfig.setPassword(config.get("db.password"));
        hikariConfig.setMaximumPoolSize(config.getInt("db.pool.size", 5));
        hikariConfig.setConnectionTimeout(config.getInt("db.pool.timeout", 30000));
        hikariConfig.setDriverClassName("org.postgresql.Driver");

        // Performans ayarları
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        try {
            this.dataSource = new HikariDataSource(hikariConfig);
            log.info("PostgreSQL bağlantı havuzu oluşturuldu → {}", config.get("db.url"));
            initializeSchema();
        } catch (Exception e) {
            log.error("Veritabanı bağlantısı kurulamadı!", e);
            throw new DatabaseException("Veritabanı bağlantısı başarısız", e);
        }
    }

    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private void initializeSchema() {
        String createTable = """
                CREATE TABLE IF NOT EXISTS products (
                    id          SERIAL PRIMARY KEY,
                    name        VARCHAR(255) NOT NULL UNIQUE,
                    quantity    INTEGER NOT NULL DEFAULT 0,
                    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
                
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
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTable);
            log.info("Veritabanı şeması hazır (products tablosu + trigger).");
        } catch (SQLException e) {
            log.error("Şema oluşturulamadı", e);
            throw new DatabaseException("Veritabanı şeması oluşturulamadı", e);
        }
    }

    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            log.info("Veritabanı bağlantı havuzu kapatıldı.");
        }
    }
}

