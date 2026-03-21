package com.platform.main.order.dto;

import com.platform.main.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderPage {
    private List<Order> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
}