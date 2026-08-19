package com.finanscore.motorscoring.mongodb.repository;

import com.finanscore.motorscoring.mongodb.document.ModeloScoringMongoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ModeloScoringMongoSpringDataRepository extends MongoRepository<ModeloScoringMongoDocument, Long> {
}
