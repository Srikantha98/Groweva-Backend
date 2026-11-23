package org.evabirds.evabirds.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "farm_images", indexes = {
        @Index(name = "idx_farm_image_farm", columnList = "farm_id"),
        @Index(name = "idx_farm_image_primary", columnList = "farm_id, is_primary")
})
public class FarmImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @Column(name = "image_url", columnDefinition = "TEXT", nullable = false)
    private String imageUrl;

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(name = "original_url", columnDefinition = "TEXT")
    private String originalUrl;

    @Column(name = "public_id", length = 255)
    private String publicId;

    @Column(length = 100)
    private String category;

    @Column(length = 500)
    private String caption;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary = false;

    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public FarmImage() {
    }

    public FarmImage(Farm farm, String imageUrl, String caption, boolean isPrimary, int displayOrder) {
        this.farm = farm;
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.isPrimary = isPrimary;
        this.displayOrder = displayOrder;
    }

    public FarmImage(Farm farm, String imageUrl, String category, String caption, boolean isPrimary, int displayOrder) {
        this.farm = farm;
        this.imageUrl = imageUrl;
        this.category = category;
        this.caption = caption;
        this.isPrimary = isPrimary;
        this.displayOrder = displayOrder;
    }

    public FarmImage(Farm farm, String imageUrl, String thumbnailUrl, String originalUrl, String publicId, String category, String caption, boolean isPrimary, int displayOrder) {
        this.farm = farm;
        this.imageUrl = imageUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.originalUrl = originalUrl;
        this.publicId = publicId;
        this.category = category;
        this.caption = caption;
        this.isPrimary = isPrimary;
        this.displayOrder = displayOrder;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Farm getFarm() {
        return farm;
    }

    public void setFarm(Farm farm) {
        this.farm = farm;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
