package com.finanscore.motorscoring.mongodb.sequence;

import com.finanscore.motorscoring.mongodb.document.SequenceMongoDocument;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MongoSequenceServiceTest {
    @Test
    void debeRetornarElValorIncrementadoAtomicamente() {
        MongoOperations operations = mock(MongoOperations.class);
        SequenceMongoDocument document = mock(SequenceMongoDocument.class);
        when(document.getValor()).thenReturn(42L);
        when(operations.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class),
                eq(SequenceMongoDocument.class))).thenReturn(document);

        long resultado = new MongoSequenceService(operations).siguiente("solicitudes_credito");

        assertEquals(42L, resultado);
        verify(operations).findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class),
                eq(SequenceMongoDocument.class));
    }
}
