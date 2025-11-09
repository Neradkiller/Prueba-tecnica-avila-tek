package com.avilaTek.e_commerce.e_commerce.infrastructure.persistence.repository;

import com.avilaTek.e_commerce.e_commerce.domain.model.Product;
import com.avilaTek.e_commerce.e_commerce.infrastructure.persistence.entity.ProducEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaProductRepository extends JpaRepository<ProducEntity, Long> {
    List<ProducEntity> findByStockGreaterThan(Long stock);
    
}
