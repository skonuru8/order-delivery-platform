package com.platform.main.order.dto;

import lombok.Data;
import java.util.List;

@Data
public class PlaceOrderInput {
    private List<OrderItemInput> items;
    private String shippingAddress;
}