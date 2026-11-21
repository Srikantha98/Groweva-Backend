package org.evabirds.evabirds.dto;

import jakarta.validation.constraints.NotBlank;

public class AddFarmImageRequest {

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private String category;

    private String caption;

    private Boolean isPrimary = false;

    private Integer displayOrder = 0;

    public AddFarmImageRequest() {
    }

    public AddFarmImageRequest(String imageUrl, String caption, Boolean isPrimary, Integer displayOrder) {
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.isPrimary = isPrimary != null ? isPrimary : false;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
    }

    public AddFarmImageRequest(String imageUrl, String category, String caption, Boolean isPrimary, Integer displayOrder) {
        this.imageUrl = imageUrl;
        this.category = category;
        this.caption = caption;
        this.isPrimary = isPrimary != null ? isPrimary : false;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
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

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean primary) {
        isPrimary = primary != null ? primary : false;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder != null ? displayOrder : 0;
    }
}
