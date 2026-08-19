package com.finanscore.motorscoring.mongodb.document;

import com.finanscore.motorscoring.domain.enums.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "solicitudes_credito")
public record SolicitudCreditoMongoDocument(
        @Id Long id,
        Long idSolicitante,
        Long idProducto,
        BigDecimal montoSolicitado,
        int plazoSolicitado,
        Moneda moneda,
        String finalidadCredito,
        String canalOrigen,
        LocalDateTime fechaRegistro,
        @Indexed(name = "uk_solicitud_identificador_externo", unique = true) String identificadorExterno,
        EstadoSolicitud estado) {
}
