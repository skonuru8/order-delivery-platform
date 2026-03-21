package com.platform.main.order.service;

import com.platform.main.auth.entity.User;
import com.platform.main.auth.repository.UserRepository;
import com.platform.main.order.dto.OrderItemInput;
import com.platform.main.order.dto.OrderPage;
import com.platform.main.order.dto.PlaceOrderInput;
import com.platform.main.order.entity.Order;
import com.platform.main.order.entity.OrderLineItem;
import com.platform.main.order.entity.OrderStatus;
import com.platform.main.order.repository.OrderRepository;
import com.platform.main.product.entity.Product;
import com.platform.main.product.repository.ProductRepository;
import com.platform.main.shared.exception.InsufficientStockException;
import com.platform.main.shared.exception.InvalidOrderStateException;
import com.platform.main.shared.exception.OrderNotFoundException;
import com.platform.main.shared.exception.ProductNotFoundException;
import com.platform.main.shared.exception.UnauthorizedOrderAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // ── Place Order ──

    @Transactional
    public Order placeOrder(PlaceOrderInput input, UUID userId) {

        // 1. Load the authenticated user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Collect all product IDs from the order upfront
        List<UUID> productIds = input.getItems().stream()
                .map(item -> UUID.fromString(item.getProductId()))
                .collect(Collectors.toList());

        // 3. ONE query for all products: SELECT * FROM products WHERE id IN (...)
        Map<UUID, Product> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        // 4. Validate all products exist and have sufficient stock
        //    Done in memory — no more DB calls in this loop
        for (OrderItemInput itemInput : input.getItems()) {
            UUID productId = UUID.fromString(itemInput.getProductId());

            Product product = productMap.get(productId);

            // Check product exists
            if (product == null) {
                throw new ProductNotFoundException(itemInput.getProductId());
            }

            // Check stock
            if (product.getStockQuantity() < itemInput.getQuantity()) {
                throw new InsufficientStockException(
                        product.getName(),
                        itemInput.getQuantity(),
                        product.getStockQuantity()
                );
            }
        }

        // 5. Create the order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setShippingAddress(input.getShippingAddress());

        // 6. Build line items and calculate total
        //    All products already in memory — no DB calls here either
        BigDecimal total = BigDecimal.ZERO;
        List<OrderLineItem> lineItems = new ArrayList<>();

        for (OrderItemInput itemInput : input.getItems()) {
            UUID productId = UUID.fromString(itemInput.getProductId());
            Product product = productMap.get(productId);

            BigDecimal lineTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemInput.getQuantity()));
            total = total.add(lineTotal);

            OrderLineItem lineItem = new OrderLineItem();
            lineItem.setOrder(order);
            lineItem.setProduct(product);
            lineItem.setQuantity(itemInput.getQuantity());
            lineItem.setUnitPrice(product.getPrice());

            lineItems.add(lineItem);

            // 7. Decrement stock — in memory, saved via saveAll below
            product.setStockQuantity(product.getStockQuantity() - itemInput.getQuantity());
        }

        // 8. Save all stock changes in ONE batch call
        productRepository.saveAll(productMap.values());

        order.setTotalAmount(total);
        order.setItems(lineItems);

        // 9. Save order — cascades to line items
        return orderRepository.save(order);
    }

    // ── Cancel Order ──

    @Transactional
    public Order cancelOrder(String orderId, UUID userId, boolean isAdmin) {

        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Customers can only cancel their own orders
        if (!isAdmin && !order.getUser().getId().equals(userId)) {
            throw new UnauthorizedOrderAccessException();
        }

        // Can only cancel PENDING or CONFIRMED orders
        if (order.getStatus() == OrderStatus.SHIPPED ||
                order.getStatus() == OrderStatus.DELIVERED ||
                order.getStatus() == OrderStatus.CANCELED) {
            throw new InvalidOrderStateException(
                    "Cannot cancel order with status: " + order.getStatus()
            );
        }

        // Restore stock for each line item
        for (OrderLineItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELED);
        return orderRepository.save(order);
    }

    // ── Ship Order (Admin Only) ──

    @Transactional
    public Order shipOrder(String orderId) {

        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Can only ship CONFIRMED orders
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new InvalidOrderStateException(
                    "Cannot ship order with status: " + order.getStatus()
            );
        }

        order.setStatus(OrderStatus.SHIPPED);
        // Phase 3: publish order.shipped event to RabbitMQ here
        return orderRepository.save(order);
    }

    // ── Queries ──

    public OrderPage getOrders(UUID userId, boolean isAdmin,
                               OrderStatus status, int page, int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());

        Page<Order> result;

        if (isAdmin) {
            // Admin sees all orders
            result = (status != null)
                    ? orderRepository.findByStatus(status, pageable)
                    : orderRepository.findAll(pageable);
        } else {
            // Customer sees only their own orders
            result = (status != null)
                    ? orderRepository.findByUser_IdAndStatus(userId, status, pageable)
                    : orderRepository.findByUser_Id(userId, pageable);
        }

        return new OrderPage(
                result.getContent(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber()
        );
    }

    public Order getOrderById(String orderId, UUID userId, boolean isAdmin) {

        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Customers can only view their own orders
        if (!isAdmin && !order.getUser().getId().equals(userId)) {
            throw new UnauthorizedOrderAccessException();
        }

        return order;
    }

    public int getOrdersCountToday() {
        return orderRepository.countByCreatedAtAfter(
                java.time.LocalDateTime.now().toLocalDate().atStartOfDay()
        );
    }
}