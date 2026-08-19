package com.finanscore.motorscoring.mongodb.mapper;

import com.finanscore.motorscoring.domain.entity.ProductoCrediticio;
import com.finanscore.motorscoring.domain.valueobject.Dinero;
import com.finanscore.motorscoring.mongodb.document.ProductoCrediticioMongoDocument;

public final class ProductoCrediticioMongoMapper {
    private ProductoCrediticioMongoMapper() {
    }

    public static ProductoCrediticio toDomain(ProductoCrediticioMongoDocument document) {
        return new ProductoCrediticio(
                document.id(),
                document.codigo(),
                document.nombre(),
                new Dinero(document.montoMinimo(), document.moneda()),
                new Dinero(document.montoMaximo(), document.moneda()),
                document.plazoMinimo(),
                document.plazoMaximo(),
                document.estado(),
                document.idModeloScoring());
    }
}
