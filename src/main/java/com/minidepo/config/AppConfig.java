package com.minidepo.config;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter
public class AppConfig {

    private static final Logger log = LogManager.getLogger(AppConfig.class);
    private static AppConfig instance;
    private final Properties properties;

    private AppConfig() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                log.error("application.properties dosyası bulunamadı!");
                throw new RuntimeException("application.properties bulunamadı");
            }
            properties.load(input);
            log.info("Uygulama yapılandırması yüklendi.");
        } catch (IOException e) {
            log.error("Yapılandırma dosyası okunamadı", e);
            throw new RuntimeException("Yapılandırma yüklenemedi", e);
        }
    }

    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public double getDouble(String key, double defaultValue) {
        try {
            return Double.parseDouble(properties.getProperty(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}

