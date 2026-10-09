package com.workaudit.workaudit.report.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "validation_config")
public class ValidationConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "config_key", nullable = false, unique = true, length = 100)
    private String configKey;

    @Column(name = "config_value", nullable = false, length = 255)
    private String configValue;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected ValidationConfig() {
        // JPA
    }

    public ValidationConfig(String configKey, String configValue, String description) {
        this.configKey = configKey;
        this.configValue = configValue;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public String getConfigKey() { return configKey; }

    public String getConfigValue() { return configValue; }
    public void setConfigValue(String configValue) {
        this.configValue = configValue;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() { return description; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
}