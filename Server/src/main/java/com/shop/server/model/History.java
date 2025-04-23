package com.shop.server.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "history")
public class History {
    @Id
    private String phoneNumber;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<Long> orderIds = new ArrayList<>();

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Long> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<Long> orderIds) {
        this.orderIds = orderIds;
    }
}