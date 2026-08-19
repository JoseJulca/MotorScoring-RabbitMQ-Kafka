package com.finanscore.motorscoring.bootstrap.integration;

import com.finanscore.motorscoring.bootstrap.MotorScoringApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MotorScoringApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"h2", "test"})
@Import(FixedTestClockConfiguration.class)
class SolicitudCreditoE2EIT {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void debeEjecutarRf04Rf05Rf06PorHttpConH2() {
        Map<String, Object> body = request("H2-E2E-" + System.nanoTime(), documentoUnico());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var registro = restTemplate.exchange(url("/api/solicitudes-credito"), HttpMethod.POST,
                new HttpEntity<>(body, headers), Map.class);

        assertEquals(HttpStatus.CREATED, registro.getStatusCode());
        assertNotNull(registro.getBody());
        Number idSolicitud = (Number) registro.getBody().get("idSolicitud");

        var evaluacion = restTemplate.postForEntity(
                url("/api/solicitudes-credito/" + idSolicitud.longValue() + "/evaluar"), null, Map.class);

        assertEquals(HttpStatus.OK, evaluacion.getStatusCode());
        assertNotNull(evaluacion.getBody());
        assertEquals(989, ((Number) evaluacion.getBody().get("puntajeTotal")).intValue());
        assertEquals("PREAPROBADA", evaluacion.getBody().get("resultado"));
        assertEquals("1.1.0", evaluacion.getBody().get("versionModelo"));
        assertEquals(9, ((List<?>) evaluacion.getBody().get("factores")).size());
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    static Map<String, Object> request(String externalId, String documento) {
        Map<String, Object> solicitante = new LinkedHashMap<>();
        solicitante.put("tipoDocumento", "DNI");
        solicitante.put("numeroDocumento", documento);
        solicitante.put("nombresRazonSocial", "Persona de prueba");
        solicitante.put("ingresosMensuales", 5500);
        solicitante.put("gastosMensuales", 1800);
        solicitante.put("obligacionesFinancieras", 700);
        solicitante.put("antiguedadLaboralNegocio", 36);
        solicitante.put("numeroObligacionesActivas", 2);
        solicitante.put("puntajeHistorialPagos", 85);
        solicitante.put("alertasMora", 0);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("identificadorExterno", externalId);
        body.put("solicitante", solicitante);
        body.put("codigoProducto", "PRESTAMO_PERSONAL");
        body.put("montoSolicitado", 15000);
        body.put("plazoSolicitado", 24);
        body.put("moneda", "PEN");
        body.put("finalidadCredito", "Consumo");
        body.put("canalOrigen", "E2E_TEST");
        return body;
    }

    static String documentoUnico() {
        return String.valueOf(10_000_000L + Math.floorMod(System.nanoTime(), 89_999_999L));
    }
}
