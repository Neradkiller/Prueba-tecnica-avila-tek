package com.avilaTek.e_commerce.e_commerce.application.service.order;

import com.avilaTek.e_commerce.e_commerce.domain.exception.DomainException;
import com.avilaTek.e_commerce.e_commerce.domain.exception.OrderNotFoundException;
import com.avilaTek.e_commerce.e_commerce.domain.exception.ProductNotFounfException;
import com.avilaTek.e_commerce.e_commerce.domain.model.order.Order;
import com.avilaTek.e_commerce.e_commerce.domain.model.order.OrderItem;
import com.avilaTek.e_commerce.e_commerce.domain.model.order.OrderStatus;
import com.avilaTek.e_commerce.e_commerce.domain.model.product.Product;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.order.OrderService;
import com.avilaTek.e_commerce.e_commerce.domain.port.output.order.OrderRepository;
import com.avilaTek.e_commerce.e_commerce.domain.port.output.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;


    @Override
    @Transactional
    public Order createOrder(Long userId, List<OrderItemRequest> items, String shippingAddress) {
        log.info("Creating order for user: {}", userId);

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest itemRequest : items) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ProductNotFounfException(itemRequest.productId()));

            if ( product.getStock() < itemRequest.quantity()) {
                throw new DomainException("Insufficient stock for product: " + product.getName());
            }

            OrderItem orderItem = OrderItem.create(product.getId(), product.getName(), product.getPrice(), itemRequest.quantity());
            orderItems.add(orderItem);

            Product updatedProduct = product.reduceStock(itemRequest.quantity());
            productRepository.save(updatedProduct);
        }

        Order order = Order.builder()
                .userId(userId)
                .items(orderItems)
                .shippingAddress(shippingAddress)
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getId());
        return savedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        log.info("Fetching orders by ID: {}", id);

        return orderRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderByOrderNumber(UUID orderNumber) {
        log.info("Fetching orders by UUID: {}", orderNumber);

        return orderRepository.findByOrderNumber(orderNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public OrdersPage getOrdersByUserId(Long userId, int page, int size, String sortBy, String sortDirection) {
        log.info("Fetching orders for user ID: {}, page: {}, size: {}", userId, page, size);
        validatePaginationParams(page, size);

        var orders = orderRepository.findByUserId(userId, page, size, sortBy, sortDirection);
        long totalElements = orderRepository.countByUserId(userId);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new OrdersPage(orders, page, size, totalElements, totalPages);
    }

    @Override
    @Transactional(readOnly = true)
    public OrdersPage getAllOrders(int page, int size, String sortBy, String sortDirection) {
        log.info("Fetching all orders - page: {}, size: {}", page, size);
        validatePaginationParams(page, size);
        var orders = orderRepository.findAll(page, size, sortBy, sortDirection);
        long totalElements = orderRepository.countAll();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        log.info("Found {} orders total", orders.size());
        return new OrdersPage(orders, page, size, totalElements, totalPages);
    }

    @Override
    @Transactional(readOnly = true)
    public OrdersPage getOrdersByStatus(String status, int page, int size, String sortBy, String sortDirection) {
        validatePaginationParams(page, size);
        OrderStatus orderStatus = parseOrderStatus(status);
        var orders = orderRepository.findByStatus(orderStatus, page, size, sortBy, sortDirection);
        long totalElements = orderRepository.countByStatus(orderStatus);
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new OrdersPage(orders, page, size, totalElements, totalPages);
    }

    @Override
    @Transactional(readOnly = true)
    public OrdersPage getRecentOrders(int days, int page, int size, String sortBy, String sortDirection) {
        validatePaginationParams(page, size);
        if (days <= 0) throw new IllegalArgumentException("Days must be positive");

        var since = LocalDateTime.now().minusDays(days);
        var orders = orderRepository.findByCreatedAtAfter(since, page, size, sortBy, sortDirection);
        long totalElements = orderRepository.countByCreatedAtAfter(since);
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new OrdersPage(orders, page, size, totalElements, totalPages);
    }

    @Override
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        Order cancelledOrder = order.cancel();
        Order savedOrder = orderRepository.save(cancelledOrder);
        restoreStock(order);
        return savedOrder;
    }

    @Override
    @Transactional
    public Order confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        Order confirmedOrder = order.confirm();
        return orderRepository.save(confirmedOrder);
    }

    @Override
    @Transactional
    public Order markOrderAsProcessing(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        Order confirmedOrder = order.markAsProcessing();
        return orderRepository.save(confirmedOrder);
    }

    @Override
    public Order markOrderAsShipped(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        Order confirmedOrder = order.markAsShipped();
        return orderRepository.save(confirmedOrder);
    }

    @Override
    public Order markOrderAsDelivered(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        Order confirmedOrder = order.markAsDelivered();
        return orderRepository.save(confirmedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrdersPage getOrdersByUserIdAdmin(Long userId, int page, int size, String sortBy, String sortDirection) {
        log.info("Admin fetching orders for user ID: {}, page: {}, size: {}", userId, page, size);
        validatePaginationParams(page, size);

        var orders = orderRepository.findByUserId(userId, page, size, sortBy, sortDirection);
        long totalElements = orderRepository.countByUserId(userId);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        log.info("Admin found {} orders for user ID: {}", orders.size(), userId);
        return new OrdersPage(orders, page, size, totalElements, totalPages);
    }

    @Override
    @Transactional
    public Order updateOrderShippingAddress(Long orderId, String newShippingAddress) {
        log.info("Admin updating shipping address for order ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new DomainException("Cannot update shipping address for order in status: " + order.getStatus());
        }

        Order updatedOrder = order.toBuilder()
                .shippingAddress(newShippingAddress)
                .updatedAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(updatedOrder);
        log.info("Shipping address updated successfully for order: {}", orderId);
        return savedOrder;
    }

    @Override
    @Transactional
    public Order refundOrder(Long orderId) {
        log.info("Admin processing refund for order ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        if (order.getStatus() != OrderStatus.DELIVERED && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new DomainException("Cannot refund order in status: " + order.getStatus());
        }

        Order refundedOrder = order.toBuilder()
                .status(OrderStatus.REFUNDED)
                .updatedAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(refundedOrder);
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            restoreStock(order);
        }

        log.info("Order refunded successfully: {}", orderId);
        return savedOrder;
    }

    @Transactional(readOnly = true)
    @Override
    public OrderStatistics getOrderStatistics() {
        log.info("Generating order statistics for admin");

        long totalOrders = orderRepository.countAll();
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        long confirmedOrders = orderRepository.countByStatus(OrderStatus.CONFIRMED);
        long processingOrders = orderRepository.countByStatus(OrderStatus.PROCESSING);
        long shippedOrders = orderRepository.countByStatus(OrderStatus.SHIPPED);
        long deliveredOrders = orderRepository.countByStatus(OrderStatus.DELIVERED);
        long cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);
        BigDecimal totalRevenue = calculateTotalRevenue();

        BigDecimal averageOrderValue = totalOrders > 0 ?
                totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;

        var since = LocalDateTime.now().minusDays(7);
        long ordersLast7Days = orderRepository.countByCreatedAtAfter(since);

        return new OrderStatistics(
                totalOrders,
                pendingOrders,
                confirmedOrders,
                processingOrders,
                shippedOrders,
                deliveredOrders,
                cancelledOrders,
                totalRevenue,
                averageOrderValue,
                ordersLast7Days
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderStatusCount> getOrdersCountByStatus() {
        log.info("Fetching order counts by status for admin");

        return List.of(
                new OrderStatusCount("PENDING", orderRepository.countByStatus(OrderStatus.PENDING)),
                new OrderStatusCount("CONFIRMED", orderRepository.countByStatus(OrderStatus.CONFIRMED)),
                new OrderStatusCount("PROCESSING", orderRepository.countByStatus(OrderStatus.PROCESSING)),
                new OrderStatusCount("SHIPPED", orderRepository.countByStatus(OrderStatus.SHIPPED)),
                new OrderStatusCount("DELIVERED", orderRepository.countByStatus(OrderStatus.DELIVERED)),
                new OrderStatusCount("CANCELLED", orderRepository.countByStatus(OrderStatus.CANCELLED)),
                new OrderStatusCount("REFUNDED", orderRepository.countByStatus(OrderStatus.REFUNDED))
        );
    }

    private BigDecimal calculateTotalRevenue() {
        var deliveredOrders = orderRepository.findByStatus(OrderStatus.DELIVERED);
        return deliveredOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    private void validatePaginationParams(int page, int size) {
        if (page < 0) throw new IllegalArgumentException("Page must be greater than or equal to 0");
        if (size <= 0 || size > 100) throw new IllegalArgumentException("Size must be between 1 and 100");
    }

    private OrderStatus parseOrderStatus(String status) {
        try { return OrderStatus.valueOf(status.toUpperCase()); }
        catch (IllegalArgumentException e) { throw new DomainException("Invalid order status: " + status); }
    }

    private void restoreStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ProductNotFounfException(item.getProductId()));
            Product updatedProduct = product.addStock(item.getQuantity());
            productRepository.save(updatedProduct);
        }
    }

}
