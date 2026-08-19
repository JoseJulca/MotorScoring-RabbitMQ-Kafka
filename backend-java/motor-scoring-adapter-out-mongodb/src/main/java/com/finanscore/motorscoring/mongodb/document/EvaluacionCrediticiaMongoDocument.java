package com.finanscore.motorscoring.mongodb.document;

import com.finanscore.motorscoring.domain.enums.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "evaluaciones_crediticias")
@CompoundIndex(name = "uk_evaluacion_solicitud_version", def = "{'idSolicitud': 1, 'idVersionModelo': 1}", unique = true)
public record EvaluacionCrediticiaMongoDocument(
        @Id Long id,
        Long idSolicitud,
        Long idVersionModelo,
        LocalDateTime fechaEvaluacion,
        int puntajeTotal,
        ResultadoScoring resultado,
        EstadoEvaluacion estado,
        List<ResultadoFactorMongoDocument> resultadosFactor) {

    public record ResultadoFactorMongoDocument(
            Long idFactor,
            String codigoFactor,
            BigDecimal valorEvaluado,
            BigDecimal pesoAplicado,
            int puntajeBase,
            int puntajeObtenido,
            String reglaAplicada,
            String observacion,
            boolean reglaExcluyente,
            ResultadoScoring resultadoExcluyente) {
    }
}
