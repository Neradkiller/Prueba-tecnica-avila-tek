package com.avilaTek.e_commerce.e_commerce.infrastructure.persistence.mapper;

import com.avilaTek.e_commerce.e_commerce.domain.model.product.Product;
import com.avilaTek.e_commerce.e_commerce.infrastructure.persistence.entity.product.ProducEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProducEntity toEntity(Product product){
        if (product == null) return null;
        return ProducEntity.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .stock(product.getStock())
                .price(product.getPrice())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public Product toDOmain(ProducEntity entity){
        if (entity == null) return null;

        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .stock(entity.getStock())
                .price(entity.getPrice())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
