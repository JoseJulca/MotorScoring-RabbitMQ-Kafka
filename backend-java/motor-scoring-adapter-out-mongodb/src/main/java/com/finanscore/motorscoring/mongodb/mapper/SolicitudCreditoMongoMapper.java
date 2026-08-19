package com.finanscore.motorscoring.mongodb.mapper;

import com.finanscore.motorscoring.domain.entity.SolicitudCredito;
import com.finanscore.motorscoring.domain.valueobject.*;
import com.finanscore.motorscoring.mongodb.document.SolicitudCreditoMongoDocument;

public final class SolicitudCreditoMongoMapper {
    private SolicitudCreditoMongoMapper() {
    }

    public static SolicitudCreditoMongoDocument toDocument(SolicitudCredito solicitud, Long id) {
        return new SolicitudCreditoMongoDocument(
                id,
                solicitud.idSolicitante(),
                solicitud.idProducto(),
                solicitud.montoSolicitado().monto(),
                solicitud.plazoSolicitado(),
                solicitud.montoSolicitado().moneda(),
                solicitud.finalidadCredito(),
                solicitud.canalOrigen(),
                solicitud.fechaRegistro(),
                solicitud.identificadorExterno().valor(),
                solicitud.estado());
    }

    public static SolicitudCredito toDomain(SolicitudCreditoMongoDocument document) {
        return SolicitudCredito.reconstituir(
                document.id(),
                document.idSolicitante(),
                document.idProducto(),
                new Dinero(document.montoSolicitado(), document.moneda()),
                document.plazoSolicitado(),
                document.finalidadCredito(),
                document.canalOrigen(),
                document.fechaRegistro(),
                new IdentificadorExterno(document.identificadorExterno()),
                document.estado());
    }
}
