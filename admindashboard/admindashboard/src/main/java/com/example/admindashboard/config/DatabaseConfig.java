package com.example.admindashboard.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${spring.datasource.url:${DB_URL:${DATABASE_URL:}}}")
    private String configuredUrl;

    @Value("${spring.datasource.username:${DB_USERNAME:}}")
    private String configuredUsername;

    @Value("${spring.datasource.password:${DB_PASSWORD:}}")
    private String configuredPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        String url = StringUtils.hasText(configuredUrl) ? configuredUrl.trim() : "";
        String username = StringUtils.hasText(configuredUsername) ? configuredUsername.trim() : "";
        String password = StringUtils.hasText(configuredPassword) ? configuredPassword.trim() : "";

        // Fallback to direct environment variables if properties are empty
        if (!StringUtils.hasText(url)) {
            url = System.getenv("DB_URL");
            if (!StringUtils.hasText(url)) {
                url = System.getenv("DATABASE_URL");
            }
        }
        if (!StringUtils.hasText(username)) {
            username = System.getenv("DB_USERNAME");
        }
        if (!StringUtils.hasText(password)) {
            password = System.getenv("DB_PASSWORD");
        }

        if (!StringUtils.hasText(url)) {
            throw new IllegalStateException("Database URL is not configured. Please set DB_URL or DATABASE_URL environment variable.");
        }

        url = url.trim();

        // Convert Render / Heroku postgres:// or postgresql:// to standard jdbc:postgresql://
        if (url.startsWith("postgres://") || url.startsWith("postgresql://")) {
            try {
                URI uri = URI.create(url);
                String userInfo = uri.getUserInfo();
                if (StringUtils.hasText(userInfo) && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    if (!StringUtils.hasText(username)) {
                        username = parts[0];
                    }
                    if (!StringUtils.hasText(password)) {
                        password = parts[1];
                    }
                }

                String host = uri.getHost();
                int port = uri.getPort() != -1 ? uri.getPort() : 5432;
                String path = uri.getPath(); // e.g. /dbname
                String query = uri.getQuery();

                StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://")
                        .append(host)
                        .append(":")
                        .append(port)
                        .append(path);

                if (StringUtils.hasText(query)) {
                    jdbcUrl.append("?").append(query);
                }

                url = jdbcUrl.toString();
                log.info("Converted cloud postgres URL to standard JDBC: jdbc:postgresql://{}:{}{}", host, port, path);
            } catch (Exception e) {
                log.warn("URI parsing exception ({}), applying fallback conversion.", e.getMessage());
                if (url.startsWith("postgres://")) {
                    url = "jdbc:postgresql://" + url.substring("postgres://".length());
                } else if (url.startsWith("postgresql://")) {
                    url = "jdbc:postgresql://" + url.substring("postgresql://".length());
                }
            }
        } else if (!url.startsWith("jdbc:")) {
            url = "jdbc:postgresql://" + url;
        }

        log.info("Configuring DataSource with JDBC URL: {}", maskUrl(url));

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        if (StringUtils.hasText(username)) {
            hikariConfig.setUsername(username);
        }
        if (StringUtils.hasText(password)) {
            hikariConfig.setPassword(password);
        }
        hikariConfig.setDriverClassName("org.postgresql.Driver");

        // Useful connection pool settings for cloud environments like Render
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setIdleTimeout(600000);
        hikariConfig.setMaxLifetime(1800000);

        return new HikariDataSource(hikariConfig);
    }

    private String maskUrl(String url) {
        if (url == null) return "";
        return url.replaceAll(":[^/@]+@", ":****@");
    }
}
