package com.avilaTek.e_commerce.e_commerce.domain.port.input.product;

import com.avilaTek.e_commerce.e_commerce.domain.model.product.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product createProduct(String name, String description, BigDecimal price, Long stock );
    Optional<Product> getProduct(Long id);
    Product updateProduct(Long id, String name, String description, BigDecimal price);
    ProductsPage getAllProducts(int page, int size, String sortBy, String sortDirection);
    void deleteProduct(Long id);
    Optional<Product> addStock(Long id, Long quantity);
    Optional<Product> reduceStock(Long id, Long quantity);
    ProductsPage getAvailableProducts(int page, int size, String sortBy, String sortDirection);
    record ProductsPage(List<Product> products, int page, int size, long totalElements, int totalPages){}
}
