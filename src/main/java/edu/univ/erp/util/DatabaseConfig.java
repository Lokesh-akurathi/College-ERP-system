package edu.univ.erp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    private static String getEnvOrDefault(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        return value != null ? value : defaultValue;
    }

    public static Connection getAuthConnection() throws SQLException {
        String url = getEnvOrDefault("AUTH_DATABASE_URL", "jdbc:postgresql://localhost:5432/auth_db");
        String username = getEnvOrDefault("PGUSER", "postgres");
        String password = getEnvOrDefault("PGPASSWORD", "postgres");
        return DriverManager.getConnection(url, username, password);
    }

    public static Connection getErpConnection() throws SQLException {
        String url = getEnvOrDefault("DATABASE_URL", "jdbc:postgresql://localhost:5432/erp_db");
        String username = getEnvOrDefault("PGUSER", "postgres");
        String password = getEnvOrDefault("PGPASSWORD", "postgres");
        return DriverManager.getConnection(url, username, password);
    }
}
