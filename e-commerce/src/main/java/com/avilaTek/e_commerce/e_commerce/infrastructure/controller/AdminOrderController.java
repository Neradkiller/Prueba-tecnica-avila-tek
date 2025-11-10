package com.avilaTek.e_commerce.e_commerce.infrastructure.controller;

import com.avilaTek.e_commerce.e_commerce.application.exception.AuthorizationException;
import com.avilaTek.e_commerce.e_commerce.application.service.AuthorizationService;
import com.avilaTek.e_commerce.e_commerce.domain.model.Order;
import com.avilaTek.e_commerce.e_commerce.domain.model.OrderItem;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.AuthService;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.OrderService;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.UserInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {
    private final OrderService orderService;
    private final AuthorizationService authorizationService;
    private final AuthService authService;

    @GetMapping()
    public ResponseEntity<OrdersPageResponse> getAllOrders(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        authorizationService.validateAdminAccess(authHeader);
        var ordersPage = orderService.getAllOrders(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(toOrdersPageResponse(ordersPage));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<OrdersPageResponse> getOrdersByStatus(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        authorizationService.validateAdminAccess(authHeader);
        var ordersPage = orderService.getOrdersByStatus(status, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(toOrdersPageResponse(ordersPage));
    }

    @GetMapping("/recent")
    public ResponseEntity<OrdersPageResponse> getRecentOrders(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        authorizationService.validateAdminAccess(authHeader);
        var ordersPage = orderService.getRecentOrders(days, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(toOrdersPageResponse(ordersPage));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        authorizationService.validateAdminAccess(authHeader);
        var confirmedOrder = orderService.confirmOrder(id);
        return ResponseEntity.ok(toResponse(confirmedOrder));
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<OrderResponse> processOrder(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        authorizationService.validateAdminAccess(authHeader);
        var processingOrder = orderService.markOrderAsProcessing(id);
        return ResponseEntity.ok(toResponse(processingOrder));
    }

    @PostMapping("/{id}/ship")
    public ResponseEntity<OrderResponse> shipOrder(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        authorizationService.validateAdminAccess(authHeader);
        var shippedOrder = orderService.markOrderAsShipped(id);
        return ResponseEntity.ok(toResponse(shippedOrder));
    }

    @PostMapping("/{id}/deliver")
    public ResponseEntity<OrderResponse> deliverOrder(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        authorizationService.validateAdminAccess(authHeader);
        var deliveredOrder = orderService.markOrderAsDelivered(id);
        return ResponseEntity.ok(toResponse(deliveredOrder));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<OrdersPageResponse> getOrdersByUserAdmin(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        authorizationService.validateAdminAccess(authHeader);

        var ordersPage = orderService.getOrdersByUserIdAdmin(userId, page, size, sortBy, sortDirection);
        var response = toOrdersPageResponse(ordersPage);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/shipping-address")
    public ResponseEntity<OrderResponse> updateShippingAddress(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @Valid @RequestBody UpdateShippingAddressRequest request) {

        authorizationService.validateAdminAccess(authHeader);

        var updatedOrder = orderService.updateOrderShippingAddress(id, request.newShippingAddress());
        return ResponseEntity.ok(toResponse(updatedOrder));
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<OrderResponse> refundOrder(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        authorizationService.validateAdminAccess(authHeader);

        var refundedOrder = orderService.refundOrder(id);
        return ResponseEntity.ok(toResponse(refundedOrder));
    }

    @GetMapping("/statistics")
    public ResponseEntity<OrderStatisticsResponse> getOrderStatistics(
            @RequestHeader("Authorization") String authHeader) {

        authorizationService.validateAdminAccess(authHeader);

        var statistics = orderService.getOrderStatistics();
        var response = toOrderStatisticsResponse(statistics);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/status-counts")
    public ResponseEntity<List<OrderStatusCountResponse>> getOrderStatusCounts(
            @RequestHeader("Authorization") String authHeader) {

        authorizationService.validateAdminAccess(authHeader);

        var statusCounts = orderService.getOrdersCountByStatus();
        var response = statusCounts.stream()
                .map(this::toOrderStatusCountResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    private OrderStatisticsResponse toOrderStatisticsResponse(OrderService.OrderStatistics statistics) {
        return new OrderStatisticsResponse(
                statistics.totalOrders(),
                statistics.pendingOrders(),
                statistics.confirmedOrders(),
                statistics.processingOrders(),
                statistics.shippedOrders(),
                statistics.deliveredOrders(),
                statistics.cancelledOrders(),
                statistics.totalRevenue(),
                statistics.averageOrderValue(),
                statistics.ordersLast7Days()
        );
    }

    private OrderStatusCountResponse toOrderStatusCountResponse(OrderService.OrderStatusCount statusCount) {
        return new OrderStatusCountResponse(
                statusCount.status(),
                statusCount.count()
        );
    }

    public record UpdateShippingAddressRequest(
            @NotBlank String newShippingAddress
    ) {}

    public record OrderStatisticsResponse(
            long totalOrders,
            long pendingOrders,
            long confirmedOrders,
            long processingOrders,
            long shippedOrders,
            long deliveredOrders,
            long cancelledOrders,
            java.math.BigDecimal totalRevenue,
            java.math.BigDecimal averageOrderValue,
            long ordersLast7Days
    ) {}

    public record OrderStatusCountResponse(
            String status,
            long count
    ) {}


    private UserInfo extractAndValidateUserInfo(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthorizationException("Missing or invalid authorization header", "INVALID_AUTH_HEADER");
        }

        String token = authHeader.substring(7);
        if (!authService.validateToken(token)) {
            throw new AuthorizationException("Invalid or expired token", "INVALID_TOKEN");
        }

        return authService.extractUserInfo(token);
    }

    private OrdersPageResponse toOrdersPageResponse(OrderService.OrdersPage ordersPage) {
        var orderResponses = ordersPage.orders().stream()
                .map(this::toResponse)
                .toList();

        return new OrdersPageResponse(
                orderResponses,
                ordersPage.page(),
                ordersPage.size(),
                ordersPage.totalElements(),
                ordersPage.totalPages(),
                ordersPage.page() > 0, // hasPrevious
                ordersPage.page() < ordersPage.totalPages() - 1 // hasNext
        );
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getUserId(),
                order.getStatus().name(),
                order.getItems().stream()
                        .map(this::toItemResponse)
                        .toList(),
                order.getTotalAmount(),
                order.getShippingAddress(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.canBeCancelled()
        );
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getProductId(),
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getSubtotal()
        );
    }

    public record CreateOrderRequest(
            @Valid List<OrderController.CreateOrderItemRequest> items,
            @NotBlank String shippingAddress
    ) {}

    public record CreateOrderItemRequest(
            @NotNull Long productId,
            @Positive Long quantity
    ) {}

    public record OrdersPageResponse(
            List<OrderResponse> orders,
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean hasPrevious,
            boolean hasNext
    ) {}

    public record OrderResponse(
            Long id,
            UUID orderNumber,
            Long userId,
            String status,
            List<OrderItemResponse> items,
            BigDecimal totalAmount,
            String shippingAddress,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            boolean canBeCancelled
    ) {}

    public record OrderItemResponse(
            Long productId,
            String productName,
            BigDecimal unitPrice,
            Long quantity,
            java.math.BigDecimal subtotal
    ) {}
}
