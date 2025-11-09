package com.avilaTek.e_commerce.e_commerce.infrastructure.persistence.repository;

import com.avilaTek.e_commerce.e_commerce.domain.model.Product;
import com.avilaTek.e_commerce.e_commerce.domain.port.output.ProductRepository;
import com.avilaTek.e_commerce.e_commerce.infrastructure.persistence.entity.ProducEntity;
import com.avilaTek.e_commerce.e_commerce.infrastructure.persistence.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;
    private final ProductMapper productMapper;

    @Override
    public Product save(Product product) {
        ProducEntity entity = productMapper.toEntity(product);
        ProducEntity savedEntity = jpaProductRepository.save(entity);
        return productMapper.toDOmain(savedEntity);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaProductRepository.findById(id)
                .map(productMapper::toDOmain);
    }

    @Override
    public List<Product> findAll() {
        return jpaProductRepository.findAll()
                .stream()
                .map(productMapper::toDOmain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaProductRepository.deleteById(id);
    }

    @Override
    public List<Product> findByStockGreaterThan(Long stock) {
        return jpaProductRepository.findByStockGreaterThan(stock)
                .stream()
                .map(productMapper::toDOmain)
                .toList();
    }
}
