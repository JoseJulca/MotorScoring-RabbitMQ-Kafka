package com.finanscore.motorscoring.mongodb.adapter;

import com.finanscore.motorscoring.domain.entity.ProductoCrediticio;
import com.finanscore.motorscoring.domain.repository.ProductoCrediticioRepository;
import com.finanscore.motorscoring.mongodb.mapper.ProductoCrediticioMongoMapper;
import com.finanscore.motorscoring.mongodb.repository.ProductoCrediticioMongoSpringDataRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Profile("mongodb")
@Repository
public class ProductoCrediticioMongoRepositoryAdapter implements ProductoCrediticioRepository {
    private final ProductoCrediticioMongoSpringDataRepository repository;

    public ProductoCrediticioMongoRepositoryAdapter(ProductoCrediticioMongoSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<ProductoCrediticio> buscarPorId(Long id) {
        return repository.findById(id).map(ProductoCrediticioMongoMapper::toDomain);
    }

    @Override
    public Optional<ProductoCrediticio> buscarPorCodigo(String codigo) {
        return repository.findByCodigo(codigo).map(ProductoCrediticioMongoMapper::toDomain);
    }
}
