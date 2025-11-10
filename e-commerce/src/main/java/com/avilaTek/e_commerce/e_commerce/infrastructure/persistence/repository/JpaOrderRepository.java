package com.avilaTek.e_commerce.e_commerce.infrastructure.persistence.repository;

import com.avilaTek.e_commerce.e_commerce.domain.model.OrderStatus;
import com.avilaTek.e_commerce.e_commerce.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long> {
    @EntityGraph(value = "OrderEntity.withItems", type = EntityGraph.EntityGraphType.LOAD)
    Optional<OrderEntity> findById(Long id);

    @EntityGraph(value = "OrderEntity.withItems", type = EntityGraph.EntityGraphType.LOAD)
    Optional<OrderEntity> findByOrderNumber(UUID orderNumber);

    @EntityGraph(value = "OrderEntity.withItems", type = EntityGraph.EntityGraphType.LOAD)
    List<OrderEntity> findByUserId(Long userId);

    @EntityGraph(value = "OrderEntity.withItems", type = EntityGraph.EntityGraphType.LOAD)
    List<OrderEntity> findByStatus(OrderStatus status);

    @EntityGraph(value = "OrderEntity.withItems", type = EntityGraph.EntityGraphType.LOAD)
    List<OrderEntity> findByCreatedAtAfter(LocalDateTime date);

    @EntityGraph(value = "OrderEntity.withItems", type = EntityGraph.EntityGraphType.LOAD)
    List<OrderEntity> findByUserIdAndStatus(Long userId, OrderStatus status);

    @EntityGraph(value = "OrderEntity.withItems", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT o FROM OrderEntity o WHERE o.userId = :userId")
    Page<OrderEntity> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @EntityGraph(value = "OrderEntity.withItems", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT o FROM OrderEntity o WHERE o.status = :status")
    Page<OrderEntity> findByStatus(@Param("status") OrderStatus status, Pageable pageable);

    @EntityGraph(value = "OrderEntity.withItems", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT o FROM OrderEntity o WHERE o.createdAt >= :date")
    Page<OrderEntity> findByCreatedAtAfter(@Param("date") LocalDateTime date, Pageable pageable);

    @EntityGraph(value = "OrderEntity.withItems", type = EntityGraph.EntityGraphType.LOAD)
    Page<OrderEntity> findAll(Pageable pageable);

    long countByUserId(Long userId);
    long countByStatus(OrderStatus status);
    long countByCreatedAtAfter(LocalDateTime date);

}
