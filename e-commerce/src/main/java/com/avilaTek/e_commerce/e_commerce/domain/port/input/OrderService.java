package com.avilaTek.e_commerce.e_commerce.domain.port.input;

import com.avilaTek.e_commerce.e_commerce.domain.model.Order;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderService {

    Order createOrder(Long userId, List<OrderItemRequest> items, String shippingAddress);
    Optional<Order> getOrderById(Long id);
    Optional<Order> getOrderByOrderNumber(UUID orderNumber);

    OrdersPage getOrdersByUserId(Long userId, int page, int size, String sortBy, String sortDirection);
    OrdersPage getAllOrders(int page, int size, String sortBy, String sortDirection);
    OrdersPage getOrdersByStatus(String status, int page, int size, String sortBy, String sortDirection);
    OrdersPage getRecentOrders(int days, int page, int size, String sortBy, String sortDirection);

    Order cancelOrder(Long orderId);
    Order confirmOrder(Long orderId);
    Order markOrderAsProcessing(Long orderId);
    Order markOrderAsShipped(Long orderId);
    Order markOrderAsDelivered(Long orderId);
    OrdersPage getOrdersByUserIdAdmin(Long userId, int page, int size, String sortBy, String sortDirection);
    Order updateOrderShippingAddress(Long orderId, String newShippingAddress);
    Order refundOrder(Long orderId);
    OrderStatistics getOrderStatistics();
    List<OrderStatusCount> getOrdersCountByStatus();

    record OrdersPage(List<Order> orders, int page, int size, long totalElements, int totalPages) {}
    record OrderItemRequest(Long productId, Long quantity) {}
    record OrderStatistics(
            long totalOrders,
            long pendingOrders,
            long confirmedOrders,
            long processingOrders,
            long shippedOrders,
            long deliveredOrders,
            long cancelledOrders,
            BigDecimal totalRevenue,
            BigDecimal averageOrderValue,
            long ordersLast7Days
    ) {}

    record OrderStatusCount(
            String status,
            long count
    ) {}
}
