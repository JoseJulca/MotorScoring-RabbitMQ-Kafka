package com.finanscore.motorscoring.mongodb.adapter;

import com.finanscore.motorscoring.domain.entity.Solicitante;
import com.finanscore.motorscoring.domain.repository.SolicitanteRepository;
import com.finanscore.motorscoring.domain.valueobject.NumeroDocumento;
import com.finanscore.motorscoring.mongodb.mapper.SolicitanteMongoMapper;
import com.finanscore.motorscoring.mongodb.repository.SolicitanteMongoSpringDataRepository;
import com.finanscore.motorscoring.mongodb.sequence.MongoSequenceService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Profile("mongodb")
@Repository
public class SolicitanteMongoRepositoryAdapter implements SolicitanteRepository {
    private static final String SECUENCIA = "solicitantes";
    private final SolicitanteMongoSpringDataRepository repository;
    private final MongoSequenceService sequences;

    public SolicitanteMongoRepositoryAdapter(SolicitanteMongoSpringDataRepository repository, MongoSequenceService sequences) {
        this.repository = repository;
        this.sequences = sequences;
    }

    @Override
    public Optional<Solicitante> buscarPorId(Long id) {
        return repository.findById(id).map(SolicitanteMongoMapper::toDomain);
    }

    @Override
    public Optional<Solicitante> buscarPorDocumento(NumeroDocumento documento) {
        return repository.findByTipoDocumentoAndNumeroDocumento(documento.tipo(), documento.numero())
                .map(SolicitanteMongoMapper::toDomain);
    }

    @Override
    public Solicitante guardar(Solicitante solicitante) {
        Long id = solicitante.id() == null ? sequences.siguiente(SECUENCIA) : solicitante.id();
        return SolicitanteMongoMapper.toDomain(repository.save(SolicitanteMongoMapper.toDocument(solicitante, id)));
    }
}
