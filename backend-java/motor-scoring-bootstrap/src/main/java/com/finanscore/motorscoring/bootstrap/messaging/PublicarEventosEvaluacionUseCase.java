package com.finanscore.motorscoring.bootstrap.messaging;

import com.finanscore.motorscoring.application.command.EjecutarEvaluacionScoringCommand;
import com.finanscore.motorscoring.application.dto.EvaluacionScoringDto;
import com.finanscore.motorscoring.application.event.AuditoriaScoringEvent;
import com.finanscore.motorscoring.application.event.NotificacionEvaluacionEvent;
import com.finanscore.motorscoring.application.port.out.AuditoriaScoringPublisher;
import com.finanscore.motorscoring.application.port.out.NotificacionEvaluacionPublisher;
import com.finanscore.motorscoring.application.usecase.EjecutarEvaluacionScoringUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Decorador posterior al caso de uso actual. La evaluación sigue siendo síncrona
 * y conserva la misma respuesta HTTP. RabbitMQ y Kafka son efectos adicionales:
 * una falla de mensajería no invalida una evaluación ya persistida.
 */
public final class PublicarEventosEvaluacionUseCase implements EjecutarEvaluacionScoringUseCase {
    private static final Logger log = LoggerFactory.getLogger(PublicarEventosEvaluacionUseCase.class);

    private final EjecutarEvaluacionScoringUseCase delegate;
    private final NotificacionEvaluacionPublisher notificaciones;
    private final AuditoriaScoringPublisher auditoria;
    private final Clock clock;

    public PublicarEventosEvaluacionUseCase(
            EjecutarEvaluacionScoringUseCase delegate,
            NotificacionEvaluacionPublisher notificaciones,
            AuditoriaScoringPublisher auditoria,
            Clock clock) {
        this.delegate = delegate;
        this.notificaciones = notificaciones;
        this.auditoria = auditoria;
        this.clock = clock;
    }

    @Override
    public EvaluacionScoringDto ejecutar(EjecutarEvaluacionScoringCommand command) {
        EvaluacionScoringDto resultado = delegate.ejecutar(command);
        publicarNotificacion(resultado);
        publicarAuditoria(resultado);
        return resultado;
    }

    private void publicarNotificacion(EvaluacionScoringDto d) {
        try {
            notificaciones.publicar(new NotificacionEvaluacionEvent(
                    UUID.randomUUID().toString(),
                    d.idEvaluacion(), d.idSolicitud(), d.puntajeTotal(), d.resultado(),
                    d.versionModelo(), d.fechaEvaluacion()));
        } catch (RuntimeException ex) {
            log.error("La evaluación {} fue completada, pero no se pudo publicar la notificación RabbitMQ.", d.idEvaluacion(), ex);
        }
    }

    private void publicarAuditoria(EvaluacionScoringDto d) {
        try {
            auditoria.publicar(new AuditoriaScoringEvent(
                    UUID.randomUUID().toString(),
                    "EVALUACION_SCORING_COMPLETADA",
                    LocalDateTime.now(clock),
                    d.idEvaluacion(), d.idSolicitud(), d.puntajeTotal(), d.resultado(),
                    d.estado(), d.versionModelo()));
        } catch (RuntimeException ex) {
            log.error("La evaluación {} fue completada, pero no se pudo publicar la auditoría Kafka.", d.idEvaluacion(), ex);
        }
    }
}
