package com.finanscore.motorscoring.mongodb.mapper;

import com.finanscore.motorscoring.domain.entity.*;
import com.finanscore.motorscoring.domain.valueobject.Porcentaje;
import com.finanscore.motorscoring.mongodb.document.ModeloScoringMongoDocument;

public final class ModeloScoringMongoMapper {
    private ModeloScoringMongoMapper() {
    }

    public static ModeloScoring toDomain(ModeloScoringMongoDocument document) {
        var versiones = document.versiones().stream()
                .map(version -> new VersionModelo(
                        version.id(),
                        version.numeroVersion(),
                        version.fechaInicioVigencia(),
                        version.fechaFinVigencia(),
                        version.estado(),
                        version.factores().stream()
                                .map(factor -> new FactorScoring(
                                        factor.id(),
                                        factor.codigo(),
                                        factor.nombre(),
                                        factor.descripcion(),
                                        new Porcentaje(factor.peso()),
                                        factor.estado(),
                                        factor.reglas().stream()
                                                .map(regla -> new ReglaEvaluacion(
                                                        regla.id(),
                                                        regla.codigo(),
                                                        regla.descripcion(),
                                                        regla.valorMinimo(),
                                                        regla.valorMaximo(),
                                                        regla.puntaje(),
                                                        regla.excluyente(),
                                                        regla.resultadoExcluyente(),
                                                        regla.estado()))
                                                .toList()))
                                .toList()))
                .toList();

        return new ModeloScoring(document.id(), document.codigo(), document.nombre(), document.estado(), versiones);
    }
}
