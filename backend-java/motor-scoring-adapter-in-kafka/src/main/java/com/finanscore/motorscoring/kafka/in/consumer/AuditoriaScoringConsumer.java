package com.finanscore.motorscoring.kafka.in.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finanscore.motorscoring.application.event.AuditoriaScoringEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Component
public class AuditoriaScoringConsumer {
    private static final Logger log = LoggerFactory.getLogger(AuditoriaScoringConsumer.class);

    private final ObjectMapper objectMapper;
    private final Path auditFile;

    public AuditoriaScoringConsumer(
            ObjectMapper objectMapper,
            @Value("${motor-scoring.audit.file:/app/audit/auditoria-scoring.jsonl}") String auditFile) {
        this.objectMapper = objectMapper;
        this.auditFile = Path.of(auditFile);
    }

    @KafkaListener(topics = "${motor-scoring.kafka.audit-topic:scoring.evaluaciones.auditoria}", groupId = "motor-scoring-auditoria")
    public synchronized void consumir(String payload) throws Exception {
        AuditoriaScoringEvent evento = objectMapper.readValue(payload, AuditoriaScoringEvent.class);
        Path parent = auditFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(
                auditFile,
                objectMapper.writeValueAsString(evento) + System.lineSeparator(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
        log.info("[KAFKA][AUDITORIA] Evento {} registrado para solicitud {}", evento.eventId(), evento.idSolicitud());
    }
}
