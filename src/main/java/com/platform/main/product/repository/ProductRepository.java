package com.platform.main.product.repository;

import com.platform.main.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Page<Product> findByCategory_Name(String categoryName, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String search, Pageable pageable);
    List<Product> findByStockQuantityLessThan(int threshold);
}