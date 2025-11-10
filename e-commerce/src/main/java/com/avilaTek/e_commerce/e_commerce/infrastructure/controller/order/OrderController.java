package com.avilaTek.e_commerce.e_commerce.infrastructure.controller.order;

import com.avilaTek.e_commerce.e_commerce.application.exception.AuthorizationException;
import com.avilaTek.e_commerce.e_commerce.application.service.authorization.AuthorizationService;
import com.avilaTek.e_commerce.e_commerce.domain.exception.OrderNotFoundException;
import com.avilaTek.e_commerce.e_commerce.domain.model.order.Order;
import com.avilaTek.e_commerce.e_commerce.domain.model.order.OrderItem;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.auth.AuthService;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.order.OrderService;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.user.UserInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final AuthorizationService authorizationService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateOrderRequest request) {

        try {
            var userInfo = extractAndValidateUserInfo(authHeader);

            var itemRequests = request.items().stream()
                    .map(item -> new OrderService.OrderItemRequest(item.productId(), item.quantity()))
                    .toList();
            System.out.println(itemRequests.toString());
            var order = orderService.createOrder(
                    userInfo.userId(),
                    itemRequests,
                    request.shippingAddress()
            );

            return ResponseEntity.created(URI.create("/api/orders/" + order.getId()))
                    .body(toResponse(order));

        } catch (AuthorizationException ex) {
            log.warn("Authorization failed for order creation: {}", ex.getMessage());
            throw ex;
        }
    }

    @GetMapping
    public ResponseEntity<OrdersPageResponse> getUserOrders(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        try {
            var userInfo = extractAndValidateUserInfo(authHeader);
            var ordersPage = orderService.getOrdersByUserId(userInfo.userId(), page, size, sortBy, sortDirection);
            return ResponseEntity.ok(toOrdersPageResponse(ordersPage));

        } catch (AuthorizationException ex) {
            log.warn("Authorization failed for orders list: {}", ex.getMessage());
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        try {
            var order = orderService.getOrderById(id)
                    .orElseThrow(() -> new OrderNotFoundException(id));

            authorizationService.validateUserAccess(authHeader, order.getUserId());
            return ResponseEntity.ok(toResponse(order));

        } catch (AuthorizationException ex) {
            log.warn("Authorization failed for order access: {}", ex.getMessage());
            throw ex;
        }
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderByNumber(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID orderNumber) {

        try {
            var order = orderService.getOrderByOrderNumber(orderNumber)
                    .orElseThrow(() -> new OrderNotFoundException(orderNumber.toString()));

            authorizationService.validateUserAccess(authHeader, order.getUserId());
            return ResponseEntity.ok(toResponse(order));

        } catch (AuthorizationException ex) {
            log.warn("Authorization failed for order access: {}", ex.getMessage());
            throw ex;
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        try {
            var order = orderService.getOrderById(id)
                    .orElseThrow(() -> new OrderNotFoundException(id));

            authorizationService.validateUserAccess(authHeader, order.getUserId());
            var cancelledOrder = orderService.cancelOrder(id);
            return ResponseEntity.ok(toResponse(cancelledOrder));

        } catch (AuthorizationException ex) {
            log.warn("Authorization failed for order cancellation: {}", ex.getMessage());
            throw ex;
        }
    }

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
            @Valid List<CreateOrderItemRequest> items,
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
            java.math.BigDecimal totalAmount,
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
