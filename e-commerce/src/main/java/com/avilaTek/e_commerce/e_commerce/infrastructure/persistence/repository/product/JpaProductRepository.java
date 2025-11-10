package com.avilaTek.e_commerce.e_commerce.infrastructure.persistence.repository.product;

import com.avilaTek.e_commerce.e_commerce.infrastructure.persistence.entity.product.ProducEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaProductRepository extends JpaRepository<ProducEntity, Long> {
    List<ProducEntity> findByStockGreaterThan(Long stock);
    
}
