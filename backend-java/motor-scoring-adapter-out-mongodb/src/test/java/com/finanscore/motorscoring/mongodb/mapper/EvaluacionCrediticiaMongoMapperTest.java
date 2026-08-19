package com.finanscore.motorscoring.mongodb.mapper;

import com.finanscore.motorscoring.domain.entity.*;
import com.finanscore.motorscoring.domain.enums.*;
import com.finanscore.motorscoring.domain.valueobject.PuntajeCrediticio;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EvaluacionCrediticiaMongoMapperTest {
    @Test
    void debeConservarElResultadoYLosFactoresEmbebidos() {
        var original = new EvaluacionCrediticia(
                7L, 3L, 2L, LocalDateTime.of(2026, 7, 27, 10, 30),
                new PuntajeCrediticio(989), ResultadoScoring.PREAPROBADA, EstadoEvaluacion.COMPLETADA,
                List.of(new ResultadoFactor(9L, "HISTORIAL_PAGOS", new BigDecimal("85"),
                        new BigDecimal("22.50"), 100, 225, "HP_EXCELENTE", "Historial excelente", false, null)));

        var reconstruida = EvaluacionCrediticiaMongoMapper.toDomain(EvaluacionCrediticiaMongoMapper.toDocument(original, original.id()));

        assertEquals(989, reconstruida.puntajeTotal().valor());
        assertEquals(ResultadoScoring.PREAPROBADA, reconstruida.resultado());
        assertEquals(1, reconstruida.resultadosFactor().size());
        assertEquals(225, reconstruida.resultadosFactor().getFirst().puntajeObtenido());
    }
}
