package com.finanscore.motorscoring.mongodb.mapper;

import com.finanscore.motorscoring.domain.entity.SolicitudCredito;
import com.finanscore.motorscoring.domain.enums.*;
import com.finanscore.motorscoring.domain.valueobject.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SolicitudCreditoMongoMapperTest {
    @Test
    void debeConvertirEnAmbasDireccionesSinPerderDatosDelDominio() {
        var original = SolicitudCredito.reconstituir(
                10L, 2L, 1L, new Dinero(new BigDecimal("15000"), Moneda.PEN),
                24, "Consumo", "TEST", LocalDateTime.of(2026, 7, 27, 10, 0),
                new IdentificadorExterno("MAP-001"), EstadoSolicitud.REGISTRADA);

        var reconstruida = SolicitudCreditoMongoMapper.toDomain(SolicitudCreditoMongoMapper.toDocument(original, original.id()));

        assertEquals(original.id(), reconstruida.id());
        assertEquals(original.identificadorExterno(), reconstruida.identificadorExterno());
        assertEquals(original.montoSolicitado(), reconstruida.montoSolicitado());
        assertEquals(original.estado(), reconstruida.estado());
    }
}
