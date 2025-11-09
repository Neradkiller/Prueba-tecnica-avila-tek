package com.avilaTek.e_commerce.e_commerce.infrastructure.controller;

import com.avilaTek.e_commerce.e_commerce.application.exception.AuthorizationException;
import com.avilaTek.e_commerce.e_commerce.application.service.AuthorizationService;
import com.avilaTek.e_commerce.e_commerce.domain.model.Product;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.AuthService;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.ProductService;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final AuthorizationService authorizationService;
    private final AuthService authService;

    @GetMapping("/")
    public ResponseEntity<ProductsPageResponse> getAvailableProducts(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ){
        try {
            log.info("Fetching available products - page: {}, size: {}, sort: {} {}", page, size, sortBy, sortDirection);
            System.out.println(!authService.validateToken(authHeader));
            if (!authService.validateToken(authHeader.substring(7))) throw new AuthorizationException("Invalid or expired token", "INVALID_TOKEN");
            var productsPage = productService.getAvailableProducts(page, size, sortBy, sortDirection);
            var response = toProductsPageResponse(productsPage);

            log.info("Successfully retrieved {} products", response.products.size());
            return ResponseEntity.ok(response);
        } catch (AuthorizationException ex) {
            log.warn("Authorization failed: {}", ex.getMessage());
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@RequestHeader("Authorization") String authHeader,@PathVariable Long id) {
        try{
            if (!authService.validateToken(authHeader.substring(7))) throw new AuthorizationException("Invalid or expired token", "INVALID_TOKEN");
            return productService.getProduct(id)
                    .map(product -> ResponseEntity.ok(toResponse(product)))
                    .orElse(ResponseEntity.notFound().build());
        }
        catch (AuthorizationException ex) {
            log.warn("Authorization failed: {}", ex.getMessage());
            throw ex;
        }
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    private UserInfo extractAndValidateUserInfo(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthorizationException("Missing or invalid authorization header", "INVALID_AUTH_HEADER");
        }

        String token = authHeader.substring(7);
        if (!authService.validateToken(token)) {
            throw new AuthorizationException("Invalid or expired token", "INVALID_TOKEN");
        }

        return authService.extractUserInfo(token);
    }

    private ProductsPageResponse toProductsPageResponse(ProductService.ProductsPage productsPage) {
        var productResponses = productsPage.products().stream()
                .map(this::toProductResponse)
                .toList();

        return new ProductsPageResponse(
                productResponses,
                productsPage.page(),
                productsPage.size(),
                productsPage.totalElements(),
                productsPage.totalPages(),
                productsPage.page() > 0,
                productsPage.page() < productsPage.totalPages() - 1
        );
    }

    private ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    public record ProductsPageResponse(
            List<ProductResponse> products,
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean hasPrevious,
            boolean hasNext
    ) {}

    public record ProductResponse(Long id, String name, String description, java.math.BigDecimal price, Long stock, LocalDateTime createdAt, LocalDateTime updatedAt){}
}
