package com.finanscore.motorscoring.mongodb.sequence;

import com.finanscore.motorscoring.mongodb.document.SequenceMongoDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Profile("mongodb")
@Component
public class MongoSequenceService {
    private final MongoOperations mongoOperations;

    public MongoSequenceService(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public long siguiente(String nombreSecuencia) {
        Query query = Query.query(Criteria.where("_id").is(nombreSecuencia));
        Update update = new Update().inc("valor", 1L);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true).upsert(true);
        SequenceMongoDocument sequence = mongoOperations.findAndModify(query, update, options, SequenceMongoDocument.class);
        return Objects.requireNonNull(sequence, "No se pudo generar la secuencia " + nombreSecuencia).getValor();
    }
}
