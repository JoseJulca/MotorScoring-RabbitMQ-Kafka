package com.finanscore.motorscoring.mongodb.adapter;

import com.finanscore.motorscoring.domain.entity.ModeloScoring;
import com.finanscore.motorscoring.domain.repository.ModeloScoringRepository;
import com.finanscore.motorscoring.mongodb.mapper.ModeloScoringMongoMapper;
import com.finanscore.motorscoring.mongodb.repository.ModeloScoringMongoSpringDataRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Profile("mongodb")
@Repository
public class ModeloScoringMongoRepositoryAdapter implements ModeloScoringRepository {
    private final ModeloScoringMongoSpringDataRepository repository;

    public ModeloScoringMongoRepositoryAdapter(ModeloScoringMongoSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<ModeloScoring> buscarCompletoPorId(Long idModelo) {
        return repository.findById(idModelo).map(ModeloScoringMongoMapper::toDomain);
    }
}
