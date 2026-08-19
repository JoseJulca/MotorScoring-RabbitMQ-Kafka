package com.finanscore.motorscoring.mongodb.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sequences")
public class SequenceMongoDocument {
    @Id
    private String id;
    private long valor;

    public String getId() {
        return id;
    }

    public long getValor() {
        return valor;
    }
}
