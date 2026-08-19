package com.finanscore.motorscoring.bootstrap.integration;

import com.finanscore.motorscoring.application.command.*;
import com.finanscore.motorscoring.application.usecase.*;
import com.finanscore.motorscoring.bootstrap.MotorScoringApplication;
import com.finanscore.motorscoring.domain.enums.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.*;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.utility.DockerImageName;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(classes = MotorScoringApplication.class)
@ActiveProfiles("mongodb")
@Import(FixedTestClockConfiguration.class)
class FlujoScoringMongoIT {
    @Container
    static final MongoDBContainer MONGO = new MongoDBContainer(DockerImageName.parse("mongo:8.0"));

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO::getReplicaSetUrl);
    }

    @Autowired
    private CrearSolicitudCreditoUseCase crearSolicitud;
    @Autowired
    private EjecutarEvaluacionScoringUseCase evaluarSolicitud;

    @Test
    void debeRegistrarCalcularYPersistirElMismoResultadoEnMongoDB() {
        String externalId = "MONGO-IT-" + System.nanoTime();
        String documento = String.valueOf(10_000_000L + Math.floorMod(System.nanoTime(), 89_999_999L));

        var registrada = crearSolicitud.ejecutar(new CrearSolicitudCreditoCommand(
                externalId, TipoDocumento.DNI, documento, "Integración MongoDB",
                new BigDecimal("5500"), new BigDecimal("1800"), new BigDecimal("700"),
                36, 2, 85, 0, "PRESTAMO_PERSONAL",
                new BigDecimal("15000"), 24, Moneda.PEN, "Consumo", "INTEGRATION_TEST"));

        var evaluada = evaluarSolicitud.ejecutar(new EjecutarEvaluacionScoringCommand(registrada.idSolicitud()));

        assertNotNull(registrada.idSolicitud());
        assertNotNull(evaluada.idEvaluacion());
        assertEquals(989, evaluada.puntajeTotal());
        assertEquals("PREAPROBADA", evaluada.resultado());
        assertEquals("1.1.0", evaluada.versionModelo());
        assertEquals(9, evaluada.factores().size());
    }
}
