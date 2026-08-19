package com.finanscore.motorscoring.mongodb.initializer;

import com.finanscore.motorscoring.mongodb.document.ModeloScoringMongoDocument;
import com.finanscore.motorscoring.mongodb.repository.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.ApplicationArguments;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MongoReferenceDataInitializerTest {
    @Test
    void debeInsertarDatosEquivalentesAV2YV3CuandoNoExisten() throws Exception {
        var modelos = mock(ModeloScoringMongoSpringDataRepository.class);
        var productos = mock(ProductoCrediticioMongoSpringDataRepository.class);
        when(modelos.existsById(1L)).thenReturn(false);
        when(productos.findByCodigo("PRESTAMO_PERSONAL")).thenReturn(Optional.empty());

        new MongoReferenceDataInitializer(modelos, productos).run(mock(ApplicationArguments.class));

        var captor = ArgumentCaptor.forClass(ModeloScoringMongoDocument.class);
        verify(modelos).save(captor.capture());
        verify(productos).save(any());
        var modelo = captor.getValue();
        assertEquals("MODELO_PERSONAL", modelo.codigo());
        assertEquals(2, modelo.versiones().size());
        var versionActiva = modelo.versiones().stream().filter(v -> "1.1.0".equals(v.numeroVersion())).findFirst().orElseThrow();
        assertEquals(9, versionActiva.factores().size());
        assertEquals(0, versionActiva.factores().stream().map(f -> f.peso()).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
                .compareTo(new java.math.BigDecimal("100.00")));
    }
}
