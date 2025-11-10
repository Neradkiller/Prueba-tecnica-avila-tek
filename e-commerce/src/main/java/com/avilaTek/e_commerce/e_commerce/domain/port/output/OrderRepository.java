package com.avilaTek.e_commerce.e_commerce.domain.port.output;

import com.avilaTek.e_commerce.e_commerce.domain.model.Order;
import com.avilaTek.e_commerce.e_commerce.domain.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    Optional<Order> findByOrderNumber(UUID orderNumber);

    boolean existsById(Long id);
    void deleteById(Long id);

    List<Order> findByUserId(Long userId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByCreatedAtAfter(LocalDateTime date);
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    List<Order> findByUserId(Long userId, int page, int size, String sortBy, String sortDirection);
    List<Order> findAll(int page, int size, String sortBy, String sortDirection); // ✅ Cambiado de findAllPaginated a findAll
    List<Order> findByStatus(OrderStatus status, int page, int size, String sortBy, String sortDirection);
    List<Order> findByCreatedAtAfter(LocalDateTime date, int page, int size, String sortBy, String sortDirection);


    long countByUserId(Long userId);
    long countAll();
    long countByStatus(OrderStatus status);
    long countByCreatedAtAfter(LocalDateTime date);
}
