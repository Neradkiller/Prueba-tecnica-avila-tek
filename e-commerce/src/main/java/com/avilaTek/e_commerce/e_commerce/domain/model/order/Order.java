package com.avilaTek.e_commerce.e_commerce.domain.model.order;

import com.avilaTek.e_commerce.e_commerce.domain.exception.DomainException;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
@Builder(builderClassName = "OrderBuilder", toBuilder = true)
public class Order {
    private final Long id;
    private final UUID orderNumber;
    private final Long userId;
    private final OrderStatus status;
    private final List<OrderItem> items;
    private final BigDecimal totalAmount;
    private final String shippingAddress;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private Order(Long id, UUID orderNumber, Long userId,
                  OrderStatus status, List<OrderItem> items, BigDecimal totalAmount,
                  String shippingAddress, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.status = status;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        validate();
    }

    private void validate() {
        if (userId == null) {
            throw new DomainException("User ID is required");
        }
        if (items == null || items.isEmpty()) {
            throw new DomainException("Order must have at least one item");
        }
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Total amount must be positive");
        }
    }

    public boolean canBeCancelled() {
        return this.status.canBeCancelled();
    }

    public Order cancel() {
        if (!canBeCancelled()) {
            throw new DomainException("Order cannot be cancelled in current status: " + this.status);
        }
        return this.toBuilder()
                .status(OrderStatus.CANCELLED)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Order confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new DomainException("Order can only be confirmed from PENDING status");
        }
        return this.toBuilder()
                .status(OrderStatus.CONFIRMED)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Order markAsProcessing() {
        if (this.status != OrderStatus.CONFIRMED) {
            throw new DomainException("Order can only be processed from CONFIRMED status");
        }
        return this.toBuilder()
                .status(OrderStatus.PROCESSING)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Order markAsShipped() {
        if (this.status != OrderStatus.PROCESSING) {
            throw new DomainException("Order can only be shipped from PROCESSING status");
        }
        return this.toBuilder()
                .status(OrderStatus.SHIPPED)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Order markAsDelivered() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new DomainException("Order can only be delivered from SHIPPED status");
        }
        return this.toBuilder()
                .status(OrderStatus.DELIVERED)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static BigDecimal calculateTotal(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static OrderBuilder builder() { return new CustomOrderBuilder(); }

    private static class CustomOrderBuilder extends OrderBuilder {
        @Override
        public Order build() {
            if (super.orderNumber == null) super.orderNumber = UUID.randomUUID();
            if (super.status == null) super.status = OrderStatus.PENDING;
            if (super.createdAt == null) super.createdAt = LocalDateTime.now();
            if (super.updatedAt == null) super.updatedAt = LocalDateTime.now();
            if (super.totalAmount == null && super.items != null) super.totalAmount = calculateTotal(super.items);
            return super.build();
        }
    }
}
