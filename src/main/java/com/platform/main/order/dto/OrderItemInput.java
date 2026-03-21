package com.platform.main.order.dto;

import lombok.Data;

@Data
public class OrderItemInput {
    private String productId;
    private int quantity;
}