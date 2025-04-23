package com.shop.server.dto;

import java.time.LocalDate;
import java.util.List;

public class OrderResponse {
    private LocalDate purchaseDate;
    private List<Long> itemIds;

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public List<Long> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<Long> itemIds) {
        this.itemIds = itemIds;
    }
}