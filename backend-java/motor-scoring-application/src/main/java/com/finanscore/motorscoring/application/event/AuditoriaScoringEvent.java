package com.finanscore.motorscoring.application.event;

import java.time.LocalDateTime;

public record AuditoriaScoringEvent(
        String eventId,
        String tipoEvento,
        LocalDateTime fechaEvento,
        Long idEvaluacion,
        Long idSolicitud,
        int puntajeTotal,
        String resultado,
        String estado,
        String versionModelo) {
}
