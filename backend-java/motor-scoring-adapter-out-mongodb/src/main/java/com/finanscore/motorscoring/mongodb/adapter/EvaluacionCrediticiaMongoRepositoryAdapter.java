package com.finanscore.motorscoring.mongodb.adapter;

import com.finanscore.motorscoring.domain.entity.EvaluacionCrediticia;
import com.finanscore.motorscoring.domain.repository.EvaluacionCrediticiaRepository;
import com.finanscore.motorscoring.mongodb.mapper.EvaluacionCrediticiaMongoMapper;
import com.finanscore.motorscoring.mongodb.repository.EvaluacionCrediticiaMongoSpringDataRepository;
import com.finanscore.motorscoring.mongodb.sequence.MongoSequenceService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Profile("mongodb")
@Repository
public class EvaluacionCrediticiaMongoRepositoryAdapter implements EvaluacionCrediticiaRepository {
    private static final String SECUENCIA = "evaluaciones_crediticias";
    private final EvaluacionCrediticiaMongoSpringDataRepository repository;
    private final MongoSequenceService sequences;

    public EvaluacionCrediticiaMongoRepositoryAdapter(EvaluacionCrediticiaMongoSpringDataRepository repository, MongoSequenceService sequences) {
        this.repository = repository;
        this.sequences = sequences;
    }

    @Override
    public boolean existePorSolicitudYVersion(Long idSolicitud, Long idVersionModelo) {
        return repository.existsByIdSolicitudAndIdVersionModelo(idSolicitud, idVersionModelo);
    }

    @Override
    public EvaluacionCrediticia guardar(EvaluacionCrediticia evaluacion) {
        Long id = evaluacion.id() == null ? sequences.siguiente(SECUENCIA) : evaluacion.id();
        return EvaluacionCrediticiaMongoMapper.toDomain(repository.save(EvaluacionCrediticiaMongoMapper.toDocument(evaluacion, id)));
    }
}
