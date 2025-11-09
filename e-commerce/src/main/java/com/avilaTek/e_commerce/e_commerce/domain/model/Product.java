package com.avilaTek.e_commerce.e_commerce.domain.model;

import com.avilaTek.e_commerce.e_commerce.domain.exception.DomainException;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@Builder(builderClassName = "UserBuilder", toBuilder = true)
public class Product {
    private final Long id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final Long stock;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private Product(
            Long id,
            String name,
            String description,
            BigDecimal price,
            Long stock,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ){
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        validate();
    }

    private void validate(){
        if (name == null || name.isBlank() || name.length() < 2) {
            throw new DomainException("Name must be at least 2 characters long");
        }
        if(price.compareTo(BigDecimal.ZERO) < 0){
            throw new DomainException("Price must be greater than 0");
        }
        if(stock < 0){
            throw  new DomainException("Stock must be greater than 0");
        }
    }

    public Product addStock(Long quantity){
        return this.toBuilder()
                .stock(this.getStock() + quantity)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Product reduceStock(Long quantity){
        return this.toBuilder()
                .stock(this.getStock() - quantity)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Product updateProduct(String newName, String newDescription, BigDecimal newPrice) {
        return this.toBuilder()
                .name(newName)
                .description(newDescription)
                .price(newPrice)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Product updateName(String newName) {
        return this.toBuilder()
                .name(newName)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Product updateDescription(String newDescription) {
        return this.toBuilder()
                .description(newDescription)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Product updatePrice(BigDecimal newPrice) {
        return this.toBuilder()
                .price(newPrice)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
