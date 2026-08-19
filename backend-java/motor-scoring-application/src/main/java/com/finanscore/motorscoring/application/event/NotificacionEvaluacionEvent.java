package com.finanscore.motorscoring.application.event;

import java.time.LocalDateTime;

public record NotificacionEvaluacionEvent(
        String eventId,
        Long idEvaluacion,
        Long idSolicitud,
        int puntajeTotal,
        String resultado,
        String versionModelo,
        LocalDateTime fechaEvaluacion) {
}
