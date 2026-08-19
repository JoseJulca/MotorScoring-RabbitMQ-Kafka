package com.finanscore.motorscoring.mongodb.repository;

import com.finanscore.motorscoring.mongodb.document.ProductoCrediticioMongoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ProductoCrediticioMongoSpringDataRepository extends MongoRepository<ProductoCrediticioMongoDocument, Long> {
    Optional<ProductoCrediticioMongoDocument> findByCodigo(String codigo);
}
