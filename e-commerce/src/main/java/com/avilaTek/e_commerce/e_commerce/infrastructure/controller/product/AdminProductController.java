package com.avilaTek.e_commerce.e_commerce.infrastructure.controller.product;

import com.avilaTek.e_commerce.e_commerce.application.exception.AuthorizationException;
import com.avilaTek.e_commerce.e_commerce.application.service.authorization.AuthorizationService;
import com.avilaTek.e_commerce.e_commerce.domain.model.product.Product;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.auth.AuthService;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.product.ProductService;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.user.UserInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@Slf4j
@RequiredArgsConstructor
public class AdminProductController {
    private final ProductService productService;
    private final AuthorizationService authorizationService;
    private final AuthService authService;

    @GetMapping()
    public ResponseEntity<ProductsPageResponse> getProducts(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ){
        try {
            log.info("Fetching all products - page: {}, size: {}, sort: {} {}", page, size, sortBy, sortDirection);
            authorizationService.validateAdminAccess(authHeader);
            var productsPage = productService.getAllProducts(page, size, sortBy, sortDirection);
            var response = toProductsPageResponse(productsPage);

            log.info("Successfully retrieved {} products", response.products.size());
            return ResponseEntity.ok(response);
        }catch (AuthorizationException ex) {
            log.warn("Admin authorization failed for user list: {}", ex.getMessage());
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        try{
            authorizationService.validateAdminAccess(authHeader);
            return productService.getProduct(id)
                    .map(product -> ResponseEntity.ok(toResponse(product)))
                    .orElse(ResponseEntity.notFound().build());
        }
        catch (AuthorizationException ex) {
            log.warn("Admin authorization failed for user list: {}", ex.getMessage());
            throw ex;
        }
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestHeader("Authorization") String authHeader,@Valid @RequestBody CreateProductRequest request) {
        try {
            authorizationService.validateAdminAccess(authHeader);
            var product = productService.createProduct(
                    request.name(),
                    request.description(),
                    request.price(),
                    request.stock()
            );

            var response = toResponse(product);
            return ResponseEntity.created(URI.create("/api/products/" + product.getId()))
                    .body(response);
        } catch (AuthorizationException ex) {
            log.warn("Admin authorization failed for user access: {}", ex.getMessage());
            throw ex;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {

        try{
            authorizationService.validateAdminAccess(authHeader);
            var product = productService.updateProduct(
                    id,
                    request.name(),
                    request.description(),
                    request.price()
            );

            return ResponseEntity.ok(toResponse(product));
        }
        catch (AuthorizationException ex) {
            log.warn("Admin authorization failed for user access: {}", ex.getMessage());
            throw ex;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        try {
            authorizationService.validateAdminAccess(authHeader);
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        }catch (AuthorizationException ex) {
            log.warn("Admin authorization failed for user access: {}", ex.getMessage());
            throw ex;
        }
    }

    @PostMapping("/{id}/reduce-stock")
    public ResponseEntity<ProductResponse> reduceStock(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @Valid @RequestBody ReduceStockRequest request) {

        try{
            authorizationService.validateAdminAccess(authHeader);
            return productService.reduceStock(id, request.quantity())
                    .map(product -> ResponseEntity.ok(toResponse(product)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (AuthorizationException ex) {
            log.warn("Admin authorization failed for user access: {}", ex.getMessage());
            throw ex;
        }

    }

    @PostMapping("/{id}/increase-stock")
    public ResponseEntity<ProductResponse> increaseStock(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @Valid @RequestBody IncreaseStockRequest request) {

        try{
            authorizationService.validateAdminAccess(authHeader);
            return productService.addStock(id, request.quantity())
                    .map(product -> ResponseEntity.ok(toResponse(product)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (AuthorizationException ex) {
            log.warn("Admin authorization failed for user access: {}", ex.getMessage());
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

    public record ProductResponse(Long id, String name, String description, BigDecimal price, Long stock, LocalDateTime createdAt, LocalDateTime updatedAt){}
    public record CreateProductRequest(
            @NotBlank String name,
            String description,
            @NotNull @DecimalMin("0.01") BigDecimal price,
            @NotNull @Min(1) Long stock
    ) {}

    public record UpdateProductRequest(
            String name,
            String description,
            @DecimalMin("0.01") BigDecimal price
    ) {}

    public record ReduceStockRequest(@NotNull @Min(1) Long quantity) {}
    public record IncreaseStockRequest(@NotNull @Min(1) Long quantity) {}


}
