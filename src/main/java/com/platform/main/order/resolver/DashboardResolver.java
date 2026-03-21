package com.platform.main.order.resolver;

import com.platform.main.order.service.OrderService;
import com.platform.main.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DashboardResolver {

    private final OrderService orderService;
    private final ProductRepository productRepository;

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> dashboardStats() {
        return Map.of(
                "ordersToday", orderService.getOrdersCountToday(),
                "revenueToday", 0.0,        // Phase 3: calculate from delivered orders
                "activeDeliveries", 0,       // Phase 4: query Delivery Service via gRPC
                "lowStockProducts", productRepository.findByStockQuantityLessThan(10).size()
        );
    }
}