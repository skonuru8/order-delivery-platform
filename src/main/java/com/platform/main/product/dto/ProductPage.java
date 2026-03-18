package com.platform.main.product.dto;

import com.platform.main.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductPage {
    private List<Product> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
}