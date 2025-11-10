package com.avilaTek.e_commerce.e_commerce.domain.model.order;

import com.avilaTek.e_commerce.e_commerce.domain.exception.DomainException;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
@Builder
public class OrderItem {
    private final Long productId;
    private final String productName;
    private final BigDecimal unitPrice;
    private final Long quantity;
    private final BigDecimal subtotal;

    private OrderItem(Long productId, String productName, BigDecimal unitPrice,
                      Long quantity, BigDecimal subtotal) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
        validate();
    }

    private void validate() {
        if (productId == null) {
            throw new DomainException("Product ID is required");
        }
        if (productName == null || productName.isBlank()) {
            throw new DomainException("Product name is required");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("Unit price must be positive");
        }
        if (quantity == null || quantity <= 0) {
            throw new DomainException("Quantity must be positive");
        }
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("Subtotal must be positive");
        }
    }

    public static OrderItem create(Long productId, String productName,
                                   BigDecimal unitPrice, Long quantity) {
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return OrderItem.builder()
                .productId(productId)
                .productName(productName)
                .unitPrice(unitPrice)
                .quantity(quantity)
                .subtotal(subtotal)
                .build();
    }

}
