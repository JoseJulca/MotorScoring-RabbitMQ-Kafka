package com.finanscore.motorscoring.mongodb.repository;

import com.finanscore.motorscoring.mongodb.document.EvaluacionCrediticiaMongoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EvaluacionCrediticiaMongoSpringDataRepository extends MongoRepository<EvaluacionCrediticiaMongoDocument, Long> {
    boolean existsByIdSolicitudAndIdVersionModelo(Long idSolicitud, Long idVersionModelo);
}
