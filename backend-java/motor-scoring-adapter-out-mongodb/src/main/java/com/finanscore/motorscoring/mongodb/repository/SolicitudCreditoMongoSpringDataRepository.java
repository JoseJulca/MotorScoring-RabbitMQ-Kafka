package com.finanscore.motorscoring.mongodb.repository;

import com.finanscore.motorscoring.mongodb.document.SolicitudCreditoMongoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SolicitudCreditoMongoSpringDataRepository extends MongoRepository<SolicitudCreditoMongoDocument, Long> {
    boolean existsByIdentificadorExterno(String identificadorExterno);
}
