package com.finanscore.motorscoring.mongodb.document;

import com.finanscore.motorscoring.domain.enums.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "solicitantes")
@CompoundIndex(name = "uk_solicitante_documento", def = "{'tipoDocumento': 1, 'numeroDocumento': 1}", unique = true)
public record SolicitanteMongoDocument(
        @Id Long id,
        TipoDocumento tipoDocumento,
        String numeroDocumento,
        String nombresRazonSocial,
        BigDecimal ingresosMensuales,
        BigDecimal gastosMensuales,
        BigDecimal obligacionesFinancieras,
        int antiguedadLaboralNegocio,
        int numeroObligacionesActivas,
        int puntajeHistorialPagos,
        int alertasMora,
        Moneda moneda,
        EstadoRegistro estado,
        LocalDateTime fechaRegistro) {
}
