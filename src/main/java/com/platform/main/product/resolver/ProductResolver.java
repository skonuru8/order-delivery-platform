package com.platform.main.product.resolver;

import com.platform.main.product.entity.Category;
import com.platform.main.product.dto.ProductPage;
import com.platform.main.product.entity.Product;
import com.platform.main.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ProductResolver {

    private final ProductService productService;

    // ── Queries ──

    @QueryMapping
    public ProductPage products(@Argument String category,
                                @Argument String search,
                                @Argument Integer page,
                                @Argument Integer size) {
        // Default page=0, size=20 if not provided
        return productService.getProducts(
                category,
                search,
                page != null ? page : 0,
                size != null ? size : 20
        );
    }

    @QueryMapping
    public Product product(@Argument String id) {
        return productService.getProductById(id);
    }

    // @BatchMapping replaces @DgsDataLoader — solves N+1 natively
    // Spring collects ALL products first, then calls this ONCE
    // with the full list — fires one SELECT WHERE id IN (...) query
    @BatchMapping
    public Map<Product, Category> category(List<Product> products) {
        return productService.getCategoriesForProducts(products);
    }

    // ── Mutations ──

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Product updateStock(@Argument String productId,
                               @Argument Integer adjustment) {
        return productService.adjustStock(productId, adjustment);
    }
}