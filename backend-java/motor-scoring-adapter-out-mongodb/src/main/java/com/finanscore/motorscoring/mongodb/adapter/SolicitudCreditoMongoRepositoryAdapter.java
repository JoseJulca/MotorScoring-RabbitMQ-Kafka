package com.finanscore.motorscoring.mongodb.adapter;

import com.finanscore.motorscoring.domain.entity.SolicitudCredito;
import com.finanscore.motorscoring.domain.repository.SolicitudCreditoRepository;
import com.finanscore.motorscoring.domain.valueobject.IdentificadorExterno;
import com.finanscore.motorscoring.mongodb.mapper.SolicitudCreditoMongoMapper;
import com.finanscore.motorscoring.mongodb.repository.SolicitudCreditoMongoSpringDataRepository;
import com.finanscore.motorscoring.mongodb.sequence.MongoSequenceService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Profile("mongodb")
@Repository
public class SolicitudCreditoMongoRepositoryAdapter implements SolicitudCreditoRepository {
    private static final String SECUENCIA = "solicitudes_credito";
    private final SolicitudCreditoMongoSpringDataRepository repository;
    private final MongoSequenceService sequences;

    public SolicitudCreditoMongoRepositoryAdapter(SolicitudCreditoMongoSpringDataRepository repository, MongoSequenceService sequences) {
        this.repository = repository;
        this.sequences = sequences;
    }

    @Override
    public boolean existePorIdentificadorExterno(IdentificadorExterno identificador) {
        return repository.existsByIdentificadorExterno(identificador.valor());
    }

    @Override
    public Optional<SolicitudCredito> buscarPorId(Long id) {
        return repository.findById(id).map(SolicitudCreditoMongoMapper::toDomain);
    }

    @Override
    public SolicitudCredito guardar(SolicitudCredito solicitud) {
        Long id = solicitud.id() == null ? sequences.siguiente(SECUENCIA) : solicitud.id();
        return SolicitudCreditoMongoMapper.toDomain(repository.save(SolicitudCreditoMongoMapper.toDocument(solicitud, id)));
    }
}
