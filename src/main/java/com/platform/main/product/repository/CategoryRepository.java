package com.platform.main.product.repository;

import com.platform.main.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    // findAllById is inherited from JpaRepository
    // it generates: SELECT * FROM categories WHERE id IN (...)
    List<Category> findAllById(Iterable<UUID> ids);
}