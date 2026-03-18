package com.platform.main.product.service;

import com.platform.main.product.dto.ProductPage;
import com.platform.main.product.entity.Category;
import com.platform.main.product.entity.Product;
import com.platform.main.product.repository.CategoryRepository;
import com.platform.main.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    public ProductPage getProducts(String category, String search, int page, int size) {

        // Always sort by name for consistent pagination
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<Product> result;

        if (category != null && !category.isBlank()) {
            // Filter by category name
            result = productRepository.findByCategory_Name(category, pageable);
        } else if (search != null && !search.isBlank()) {
            // Search by product name (case insensitive)
            result = productRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            // No filter — return all products paginated
            result = productRepository.findAll(pageable);
        }

        return new ProductPage(
                result.getContent(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber()       // current page number
        );
    }

    public Product getProductById(String id) {
        return productRepository.findById(java.util.UUID.fromString(id))
                .orElse(null);  // GraphQL returns null for missing optional fields
    }

    public Product adjustStock(String productId, int adjustment) {
        Product product = productRepository.findById(java.util.UUID.fromString(productId))
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        int newStock = product.getStockQuantity() + adjustment;

        if (newStock < 0) {
            throw new RuntimeException("Insufficient stock. Current: "
                    + product.getStockQuantity() + ", adjustment: " + adjustment);
        }

        product.setStockQuantity(newStock);
        return productRepository.save(product);
    }

    public Map<Product, Category> getCategoriesForProducts(List<Product> products) {
        // Collect all unique category IDs from all products
        List<UUID> categoryIds = products.stream()
                .filter(p -> p.getCategory() != null)
                .map(p -> p.getCategory().getId())
                .distinct()
                .collect(Collectors.toList());

        // ONE query: SELECT * FROM categories WHERE id IN (...)
        Map<UUID, Category> categoryMap = categoryRepository.findAllById(categoryIds)
                .stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));

        // Map each product to its category
        return products.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        p -> p.getCategory() != null
                                ? categoryMap.get(p.getCategory().getId())
                                : null
                ));
    }
}