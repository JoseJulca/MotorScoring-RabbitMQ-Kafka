package com.finanscore.motorscoring.mongodb.repository;

import com.finanscore.motorscoring.domain.enums.TipoDocumento;
import com.finanscore.motorscoring.mongodb.document.SolicitanteMongoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface SolicitanteMongoSpringDataRepository extends MongoRepository<SolicitanteMongoDocument, Long> {
    Optional<SolicitanteMongoDocument> findByTipoDocumentoAndNumeroDocumento(TipoDocumento tipoDocumento, String numeroDocumento);
}
