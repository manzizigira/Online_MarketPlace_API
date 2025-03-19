package com.ecommerce.Ecommerce.Web.dto;

import java.math.BigDecimal;

public class ProductDTO {
    private Long categoryId;
    private String name;
    private String sku;
    private String description;
    private BigDecimal unitPrice;
    private Boolean active;
    private BigDecimal unitsInStock;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public BigDecimal getUnitsInStock() {
        return unitsInStock;
    }

    public void setUnitsInStock(BigDecimal unitsInStock) {
        this.unitsInStock = unitsInStock;
    }
}

