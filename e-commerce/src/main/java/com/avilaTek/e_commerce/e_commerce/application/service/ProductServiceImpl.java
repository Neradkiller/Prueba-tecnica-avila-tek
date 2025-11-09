package com.avilaTek.e_commerce.e_commerce.application.service;

import com.avilaTek.e_commerce.e_commerce.domain.exception.ProductNotFounfException;
import com.avilaTek.e_commerce.e_commerce.domain.model.Product;
import com.avilaTek.e_commerce.e_commerce.domain.model.User;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.ProductService;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.UserService;
import com.avilaTek.e_commerce.e_commerce.domain.port.output.ProductRepository;
import com.avilaTek.e_commerce.e_commerce.infrastructure.persistence.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Product createProduct(String name, String description, BigDecimal price, Long stock) {
        log.info("Registering new roduct with name: {}", name);
        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .build();
        Product savedProduct = productRepository.save(product);
        log.info("Product registered successfully with ID: {}", savedProduct.getId());
        return savedProduct;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProduct(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, String name, String description, BigDecimal price) {
        log.info("Updating product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFounfException(id));
        Product updatedProduct = product.updateProduct(name,description,price);
        Product savedProduct = productRepository.save(updatedProduct);

        log.info("Product updated successfully with ID: {}", savedProduct.getId());
        return savedProduct;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductsPage getAllProducts(int page, int size, String sortBy, String sortDirection) {
        log.info("Fetching all products - page: {}, size: {}, sort: {} {}", page, size, sortBy, sortDirection);
        validatePaginationParams(page, size);
        List<Product> allProducts = productRepository.findAll();
        List<Product> sortedProducts = sortProducts(allProducts, sortBy, sortDirection);
        List<Product> pagedProducts = applyPagination(sortedProducts, page, size);
        int totalElements = pagedProducts.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        log.info("Retrieved {} products out of {} total", pagedProducts.size(), totalElements);
        return new ProductsPage(pagedProducts, page, size, totalElements, totalPages);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<Product> addStock(Long id, Long quantity) {
        log.info("Adding stock to product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFounfException(id));

        Product addedProduct = product.addStock(quantity);
        Product savedProduct = productRepository.save(addedProduct);

        log.info("Added stock to product with id: {}", id);
        return Optional.ofNullable(savedProduct);
    }

    @Override
    @Transactional
    public Optional<Product> reduceStock(Long id, Long quantity) {
        log.info("Reducing stock to product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFounfException(id));

        Product addedProduct = product.reduceStock(quantity);
        Product savedProduct = productRepository.save(addedProduct);

        log.info("Reduced stock to product with id: {}", id);
        return Optional.ofNullable(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductsPage getAvailableProducts(int page, int size, String sortBy, String sortDirection) {
        log.info("Fetching available products - page: {}, size: {}, sort: {} {}", page, size, sortBy, sortDirection);
        validatePaginationParams(page, size);
        List<Product> allProducts = productRepository.findByStockGreaterThan(0L);
        List<Product> sortedProducts = sortProducts(allProducts, sortBy, sortDirection);
        List<Product> pagedProducts = applyPagination(sortedProducts, page, size);
        int totalElements = pagedProducts.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        log.info("Retrieved {} products out of {} total", pagedProducts.size(), totalElements);
        return new ProductsPage(pagedProducts, page, size, totalElements, totalPages);
    }

    private void validatePaginationParams(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page must be greater than or equal to 0");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Size must be between 1 and 100");
        }
    }

    private List<Product> sortProducts(List<Product> allProducts, String sortBy, String sortDirection) {
        Comparator<Product> comparator = getComparator(sortBy);

        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        return allProducts.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    private Comparator<Product> getComparator(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "name" -> Comparator.comparing(Product::getName);
            case "price" -> Comparator.comparing(Product::getPrice);
            case "stock" -> Comparator.comparing(Product::getStock);
            case "createdat" -> Comparator.comparing(Product::getCreatedAt);
            case "updatedat" -> Comparator.comparing(Product::getUpdatedAt);
            default -> Comparator.comparing(Product::getId); // default sort by id
        };
    }

    private List<Product> applyPagination(List<Product> sortedProducts, int page, int size) {
        int start = page * size;
        if (start >= sortedProducts.size()) {
            return List.of();
        }
        int end = Math.min(start + size, sortedProducts.size());
        return sortedProducts.subList(start, end);
    }
}
