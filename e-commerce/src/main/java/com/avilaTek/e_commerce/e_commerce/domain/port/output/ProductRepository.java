package com.avilaTek.e_commerce.e_commerce.domain.port.output;

import com.avilaTek.e_commerce.e_commerce.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    void deleteById(Long id);
    List<Product> findByStockGreaterThan(Long stock);
}
