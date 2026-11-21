package org.evabirds.evabirds.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "google_ads_config")
public class GoogleAdsConfig {

    @Id
    private Long id = 1L;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(name = "publisher_id")
    private String publisherId = "ca-pub-9824104928174921";

    @Column(name = "header_slot_id")
    private String headerSlotId = "8492019482";

    @Column(name = "sidebar_slot_id")
    private String sidebarSlotId = "6291048192";

    @Column(name = "auto_ads_enabled")
    private Boolean autoAdsEnabled = true;

    @Column(name = "test_mode")
    private Boolean testMode = true;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public GoogleAdsConfig() {
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getHeaderSlotId() {
        return headerSlotId;
    }

    public void setHeaderSlotId(String headerSlotId) {
        this.headerSlotId = headerSlotId;
    }

    public String getSidebarSlotId() {
        return sidebarSlotId;
    }

    public void setSidebarSlotId(String sidebarSlotId) {
        this.sidebarSlotId = sidebarSlotId;
    }

    public Boolean getAutoAdsEnabled() {
        return autoAdsEnabled;
    }

    public void setAutoAdsEnabled(Boolean autoAdsEnabled) {
        this.autoAdsEnabled = autoAdsEnabled;
    }

    public Boolean getTestMode() {
        return testMode;
    }

    public void setTestMode(Boolean testMode) {
        this.testMode = testMode;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
