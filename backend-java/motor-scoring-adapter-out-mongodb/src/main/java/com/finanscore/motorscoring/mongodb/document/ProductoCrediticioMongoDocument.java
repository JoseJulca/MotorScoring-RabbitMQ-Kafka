package com.finanscore.motorscoring.mongodb.document;

import com.finanscore.motorscoring.domain.enums.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;

@Document(collection = "productos_crediticios")
public record ProductoCrediticioMongoDocument(
        @Id Long id,
        @Indexed(name = "uk_producto_codigo", unique = true) String codigo,
        String nombre,
        BigDecimal montoMinimo,
        BigDecimal montoMaximo,
        int plazoMinimo,
        int plazoMaximo,
        Moneda moneda,
        EstadoProducto estado,
        Long idModeloScoring) {
}
