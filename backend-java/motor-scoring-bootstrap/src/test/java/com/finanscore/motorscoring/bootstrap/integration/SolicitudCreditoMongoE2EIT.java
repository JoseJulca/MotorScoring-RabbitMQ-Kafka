package com.finanscore.motorscoring.bootstrap.integration;

import com.finanscore.motorscoring.bootstrap.MotorScoringApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.*;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.utility.DockerImageName;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(classes = MotorScoringApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("mongodb")
@Import(FixedTestClockConfiguration.class)
class SolicitudCreditoMongoE2EIT {
    @Container
    static final MongoDBContainer MONGO = new MongoDBContainer(DockerImageName.parse("mongo:8.0"));

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO::getReplicaSetUrl);
    }

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void debeMantenerElContratoHttpUsandoMongoDB() {
        var body = SolicitudCreditoE2EIT.request("MONGO-E2E-" + System.nanoTime(), SolicitudCreditoE2EIT.documentoUnico());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var registro = restTemplate.exchange(url("/api/solicitudes-credito"), HttpMethod.POST,
                new HttpEntity<>(body, headers), Map.class);
        assertEquals(HttpStatus.CREATED, registro.getStatusCode());
        Number idSolicitud = (Number) Objects.requireNonNull(registro.getBody()).get("idSolicitud");

        var evaluacion = restTemplate.postForEntity(
                url("/api/solicitudes-credito/" + idSolicitud.longValue() + "/evaluar"), null, Map.class);

        assertEquals(HttpStatus.OK, evaluacion.getStatusCode());
        Map<?, ?> response = Objects.requireNonNull(evaluacion.getBody());
        assertEquals(989, ((Number) response.get("puntajeTotal")).intValue());
        assertEquals("PREAPROBADA", response.get("resultado"));
        assertEquals("1.1.0", response.get("versionModelo"));
        assertEquals(9, ((List<?>) response.get("factores")).size());
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
