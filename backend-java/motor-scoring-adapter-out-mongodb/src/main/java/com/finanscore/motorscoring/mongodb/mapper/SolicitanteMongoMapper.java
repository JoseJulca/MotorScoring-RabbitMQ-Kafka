package com.finanscore.motorscoring.mongodb.mapper;

import com.finanscore.motorscoring.domain.entity.Solicitante;
import com.finanscore.motorscoring.domain.valueobject.*;
import com.finanscore.motorscoring.mongodb.document.SolicitanteMongoDocument;

public final class SolicitanteMongoMapper {
    private SolicitanteMongoMapper() {
    }

    public static SolicitanteMongoDocument toDocument(Solicitante solicitante, Long id) {
        return new SolicitanteMongoDocument(
                id,
                solicitante.documento().tipo(),
                solicitante.documento().numero(),
                solicitante.nombresRazonSocial(),
                solicitante.ingresosMensuales().monto(),
                solicitante.gastosMensuales().monto(),
                solicitante.obligacionesFinancieras().monto(),
                solicitante.antiguedadLaboralNegocio(),
                solicitante.numeroObligacionesActivas(),
                solicitante.puntajeHistorialPagos(),
                solicitante.alertasMora(),
                solicitante.moneda(),
                solicitante.estado(),
                solicitante.fechaRegistro());
    }

    public static Solicitante toDomain(SolicitanteMongoDocument document) {
        return Solicitante.reconstituir(
                document.id(),
                new NumeroDocumento(document.tipoDocumento(), document.numeroDocumento()),
                document.nombresRazonSocial(),
                new Dinero(document.ingresosMensuales(), document.moneda()),
                new Dinero(document.gastosMensuales(), document.moneda()),
                new Dinero(document.obligacionesFinancieras(), document.moneda()),
                document.antiguedadLaboralNegocio(),
                document.numeroObligacionesActivas(),
                document.puntajeHistorialPagos(),
                document.alertasMora(),
                document.estado(),
                document.fechaRegistro());
    }
}
