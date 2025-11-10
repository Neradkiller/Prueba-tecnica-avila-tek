package com.avilaTek.e_commerce.e_commerce.infrastructure.persistence.mapper;

import com.avilaTek.e_commerce.e_commerce.domain.model.order.Order;
import com.avilaTek.e_commerce.e_commerce.domain.model.order.OrderItem;
import com.avilaTek.e_commerce.e_commerce.infrastructure.persistence.entity.order.OrderEntity;
import com.avilaTek.e_commerce.e_commerce.infrastructure.persistence.entity.order.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {
    public OrderEntity toEntity(Order order) {
        if (order == null) return null;

        OrderEntity entity = OrderEntity.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();

        // Mapear items y establecer la relación bidireccional
        if (order.getItems() != null) {
            var orderItems = order.getItems().stream()
                    .map(item -> toEntity(item, entity))
                    .collect(Collectors.toList());
            entity.setItems(orderItems);
        }

        return entity;
    }

    public Order toDomain(OrderEntity entity) {
        if (entity == null) return null;

        return Order.builder()
                .id(entity.getId())
                .orderNumber(entity.getOrderNumber())
                .userId(entity.getUserId())
                .status(entity.getStatus())
                .totalAmount(entity.getTotalAmount())
                .shippingAddress(entity.getShippingAddress())
                .items(entity.getItems().stream()
                        .map(this::toDomain)
                        .collect(Collectors.toList()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private OrderItemEntity toEntity(OrderItem item, OrderEntity order) {
        return OrderItemEntity.builder()
                .order(order) // Establecer la relación
                .productId(item.getProductId())
                .productName(item.getProductName())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .build();
    }

    private OrderItem toDomain(OrderItemEntity entity) {
        return OrderItem.create(
                entity.getProductId(),
                entity.getProductName(),
                entity.getUnitPrice(),
                entity.getQuantity()
        );
    }
}
