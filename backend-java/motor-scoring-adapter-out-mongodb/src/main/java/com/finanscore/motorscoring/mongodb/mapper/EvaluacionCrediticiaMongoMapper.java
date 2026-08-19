package com.finanscore.motorscoring.mongodb.mapper;

import com.finanscore.motorscoring.domain.entity.*;
import com.finanscore.motorscoring.domain.valueobject.PuntajeCrediticio;
import com.finanscore.motorscoring.mongodb.document.EvaluacionCrediticiaMongoDocument;

public final class EvaluacionCrediticiaMongoMapper {
    private EvaluacionCrediticiaMongoMapper() {
    }

    public static EvaluacionCrediticiaMongoDocument toDocument(EvaluacionCrediticia evaluacion, Long id) {
        return new EvaluacionCrediticiaMongoDocument(
                id,
                evaluacion.idSolicitud(),
                evaluacion.idVersionModelo(),
                evaluacion.fechaEvaluacion(),
                evaluacion.puntajeTotal().valor(),
                evaluacion.resultado(),
                evaluacion.estado(),
                evaluacion.resultadosFactor().stream()
                        .map(resultado -> new EvaluacionCrediticiaMongoDocument.ResultadoFactorMongoDocument(
                                resultado.idFactor(),
                                resultado.codigoFactor(),
                                resultado.valorEvaluado(),
                                resultado.pesoAplicado(),
                                resultado.puntajeBase(),
                                resultado.puntajeObtenido(),
                                resultado.reglaAplicada(),
                                resultado.observacion(),
                                resultado.reglaExcluyente(),
                                resultado.resultadoExcluyente()))
                        .toList());
    }

    public static EvaluacionCrediticia toDomain(EvaluacionCrediticiaMongoDocument document) {
        return new EvaluacionCrediticia(
                document.id(),
                document.idSolicitud(),
                document.idVersionModelo(),
                document.fechaEvaluacion(),
                new PuntajeCrediticio(document.puntajeTotal()),
                document.resultado(),
                document.estado(),
                document.resultadosFactor().stream()
                        .map(resultado -> new ResultadoFactor(
                                resultado.idFactor(),
                                resultado.codigoFactor(),
                                resultado.valorEvaluado(),
                                resultado.pesoAplicado(),
                                resultado.puntajeBase(),
                                resultado.puntajeObtenido(),
                                resultado.reglaAplicada(),
                                resultado.observacion(),
                                resultado.reglaExcluyente(),
                                resultado.resultadoExcluyente()))
                        .toList());
    }
}
