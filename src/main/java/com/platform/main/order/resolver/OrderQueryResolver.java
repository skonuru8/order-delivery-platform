package com.platform.main.order.resolver;

import com.platform.main.order.dto.OrderPage;
import com.platform.main.order.entity.Order;
import com.platform.main.order.entity.OrderLineItem;
import com.platform.main.order.entity.OrderStatus;
import com.platform.main.order.service.OrderService;
import com.platform.main.product.entity.Product;
import com.platform.main.product.repository.ProductRepository;
import com.platform.main.auth.entity.User;
import com.platform.main.auth.repository.UserRepository;
import com.platform.main.shared.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class OrderQueryResolver {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public OrderPage orders(@Argument OrderStatus status,
                            @Argument Integer page,
                            @Argument Integer size) {
        return orderService.getOrders(
                SecurityUtils.getCurrentUserId(),
                SecurityUtils.isAdmin(),
                status,
                page != null ? page : 0,
                size != null ? size : 20
        );
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public Order order(@Argument String id) {
        return orderService.getOrderById(
                id,
                SecurityUtils.getCurrentUserId(),
                SecurityUtils.isAdmin()
        );
    }

    // ── Nested Field Resolution ──

    // Resolve Order.customer — who placed the order
    @BatchMapping(typeName = "Order", field = "customer")
    public Map<Order, User> customer(List<Order> orders) {
        List<UUID> userIds = orders.stream()
                .map(o -> o.getUser().getId())
                .distinct()
                .collect(Collectors.toList());

        Map<UUID, User> userMap = userRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return orders.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        o -> userMap.get(o.getUser().getId())
                ));
    }

    // Resolve OrderLineItem.product — what was ordered
    @BatchMapping(typeName = "OrderLineItem", field = "product")
    public Map<OrderLineItem, Product> product(List<OrderLineItem> items) {
        List<UUID> productIds = items.stream()
                .map(i -> i.getProduct().getId())
                .distinct()
                .collect(Collectors.toList());

        Map<UUID, Product> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        return items.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        i -> productMap.get(i.getProduct().getId())
                ));
    }
}