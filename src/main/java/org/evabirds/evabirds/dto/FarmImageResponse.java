package org.evabirds.evabirds.dto;

import org.evabirds.evabirds.entity.FarmImage;

import java.time.LocalDateTime;

public class FarmImageResponse {

    private Long id;
    private Long farmId;
    private String imageUrl;
    private String category;
    private String caption;
    private boolean isPrimary;
    private int displayOrder;
    private LocalDateTime createdAt;

    public FarmImageResponse() {
    }

    public static FarmImageResponse fromEntity(FarmImage image) {
        if (image == null) {
            return null;
        }

        FarmImageResponse response = new FarmImageResponse();
        response.setId(image.getId());
        if (image.getFarm() != null) {
            response.setFarmId(image.getFarm().getId());
        }
        response.setImageUrl(image.getImageUrl());
        response.setCategory(image.getCategory());
        response.setCaption(image.getCaption());
        response.setPrimary(image.isPrimary());
        response.setDisplayOrder(image.getDisplayOrder());
        response.setCreatedAt(image.getCreatedAt());

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFarmId() {
        return farmId;
    }

    public void setFarmId(Long farmId) {
        this.farmId = farmId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
