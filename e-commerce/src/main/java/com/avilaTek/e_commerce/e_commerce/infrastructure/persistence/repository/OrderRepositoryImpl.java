package com.avilaTek.e_commerce.e_commerce.infrastructure.persistence.repository;

import com.avilaTek.e_commerce.e_commerce.domain.model.Order;
import com.avilaTek.e_commerce.e_commerce.domain.model.OrderStatus;
import com.avilaTek.e_commerce.e_commerce.domain.port.output.OrderRepository;
import com.avilaTek.e_commerce.e_commerce.infrastructure.persistence.entity.OrderEntity;
import com.avilaTek.e_commerce.e_commerce.infrastructure.persistence.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.data.jpa.repository.query.KeysetScrollSpecification.createSort;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final JpaOrderRepository jpaOrderRepository;
    private final OrderMapper orderMapper;

    @Override
    public Order save(Order order) {
        OrderEntity entity = orderMapper.toEntity(order);
        OrderEntity savedEntity = jpaOrderRepository.save(entity);
        return jpaOrderRepository.findById(savedEntity.getId())
                .map(orderMapper::toDomain)
                .orElseThrow(() -> new RuntimeException("Failed to load saved order with items"));
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaOrderRepository.findById(id)
                .map(orderMapper::toDomain);
    }

    @Override
    public Optional<Order> findByOrderNumber(UUID orderNumber) {
        return jpaOrderRepository.findByOrderNumber(orderNumber)
                .map(orderMapper::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaOrderRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaOrderRepository.deleteById(id);
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return jpaOrderRepository.findByUserId(userId)
                .stream()
                .map(orderMapper::toDomain)
                .toList();
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return jpaOrderRepository.findByStatus(status)
                .stream()
                .map(orderMapper::toDomain)
                .toList();
    }

    @Override
    public List<Order> findByCreatedAtAfter(LocalDateTime date) {
        return jpaOrderRepository.findByCreatedAtAfter(date)
                .stream()
                .map(orderMapper::toDomain)
                .toList();
    }

    @Override
    public List<Order> findByUserIdAndStatus(Long userId, OrderStatus status) {
        return jpaOrderRepository.findByUserIdAndStatus(userId, status)
                .stream()
                .map(orderMapper::toDomain)
                .toList();
    }

    @Override
    public List<Order> findByUserId(Long userId, int page, int size, String sortBy, String sortDirection) {
        Sort sort = createSort(sortBy, sortDirection);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OrderEntity> result = jpaOrderRepository.findByUserId(userId, pageable);
        return result.getContent()
                .stream()
                .map(orderMapper::toDomain)
                .toList();
    }

    @Override
    public List<Order> findAll(int page, int size, String sortBy, String sortDirection) {
        Sort sort = createSort(sortBy, sortDirection);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderEntity> result = jpaOrderRepository.findAll(pageable);
        return result.getContent()
                .stream()
                .map(orderMapper::toDomain)
                .toList();
    }

    @Override
    public List<Order> findByStatus(OrderStatus status, int page, int size, String sortBy, String sortDirection) {
        Sort sort = createSort(sortBy, sortDirection);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OrderEntity> result = jpaOrderRepository.findByStatus(status, pageable);
        return result.getContent()
                .stream()
                .map(orderMapper::toDomain)
                .toList();
    }

    @Override
    public List<Order> findByCreatedAtAfter(LocalDateTime date, int page, int size, String sortBy, String sortDirection) {
        Sort sort = createSort(sortBy, sortDirection);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OrderEntity> result = jpaOrderRepository.findByCreatedAtAfter(date, pageable);
        return result.getContent()
                .stream()
                .map(orderMapper::toDomain)
                .toList();
    }

    @Override
    public long countByUserId(Long userId) {
        return jpaOrderRepository.countByUserId(userId);
    }

    @Override
    public long countAll() {
        return jpaOrderRepository.count();
    }

    @Override
    public long countByStatus(OrderStatus status) {
        return jpaOrderRepository.countByStatus(status);
    }

    @Override
    public long countByCreatedAtAfter(LocalDateTime date) {
        return jpaOrderRepository.countByCreatedAtAfter(date);
    }

    private Sort createSort(String sortBy, String sortDirection) {
        String actualSortBy = getSortField(sortBy);
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        return Sort.by(direction, actualSortBy);
    }

    private String getSortField(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "createdat" -> "createdAt";
            case "updatedat" -> "updatedAt";
            case "totalamount" -> "totalAmount";
            case "ordernumber" -> "orderNumber";
            case "useremail" -> "userEmail";
            default -> sortBy;
        };
    }
}
