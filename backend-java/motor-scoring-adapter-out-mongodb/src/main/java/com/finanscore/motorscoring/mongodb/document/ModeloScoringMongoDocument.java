package com.finanscore.motorscoring.mongodb.document;

import com.finanscore.motorscoring.domain.enums.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Document(collection = "modelos_scoring")
public record ModeloScoringMongoDocument(
        @Id Long id,
        @Indexed(name = "uk_modelo_codigo", unique = true) String codigo,
        String nombre,
        String descripcion,
        EstadoModelo estado,
        List<VersionMongoDocument> versiones) {

    public record VersionMongoDocument(
            Long id,
            String numeroVersion,
            LocalDate fechaInicioVigencia,
            LocalDate fechaFinVigencia,
            EstadoVersionModelo estado,
            List<FactorMongoDocument> factores) {
    }

    public record FactorMongoDocument(
            Long id,
            String codigo,
            String nombre,
            String descripcion,
            BigDecimal peso,
            EstadoFactor estado,
            List<ReglaMongoDocument> reglas) {
    }

    public record ReglaMongoDocument(
            Long id,
            String codigo,
            String descripcion,
            BigDecimal valorMinimo,
            BigDecimal valorMaximo,
            int puntaje,
            boolean excluyente,
            ResultadoScoring resultadoExcluyente,
            EstadoRegla estado) {
    }
}
