package com.platform.main.order.resolver;

import com.platform.main.order.dto.PlaceOrderInput;
import com.platform.main.order.entity.Order;
import com.platform.main.order.service.OrderService;
import com.platform.main.shared.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class OrderMutationResolver {

    private final OrderService orderService;

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Order placeOrder(@Argument PlaceOrderInput input) {
        return orderService.placeOrder(
                input,
                SecurityUtils.getCurrentUserId()
        );
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Order cancelOrder(@Argument String id) {
        return orderService.cancelOrder(
                id,
                SecurityUtils.getCurrentUserId(),
                SecurityUtils.isAdmin()
        );
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Order shipOrder(@Argument String id) {
        return orderService.shipOrder(id);
    }
}