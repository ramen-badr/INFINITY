package com.shop.server.dto;

import java.util.List;

public class OrderRequest {
    private List<Long> itemIds;

    public List<Long> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<Long> itemIds) {
        this.itemIds = itemIds;
    }
}