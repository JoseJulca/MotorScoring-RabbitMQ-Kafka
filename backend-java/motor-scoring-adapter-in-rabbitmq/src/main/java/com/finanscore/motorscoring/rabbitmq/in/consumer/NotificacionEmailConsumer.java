package com.finanscore.motorscoring.rabbitmq.in.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finanscore.motorscoring.application.event.NotificacionEvaluacionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class NotificacionEmailConsumer {
    private static final Logger log = LoggerFactory.getLogger(NotificacionEmailConsumer.class);

    private final ObjectMapper objectMapper;
    private final JavaMailSender mailSender;
    private final boolean mailEnabled;
    private final String mailTo;
    private final String mailFrom;

    public NotificacionEmailConsumer(
            ObjectMapper objectMapper,
            JavaMailSender mailSender,
            @Value("${motor-scoring.notification.email.enabled:false}") boolean mailEnabled,
            @Value("${motor-scoring.notification.email.to:demo@local.test}") String mailTo,
            @Value("${motor-scoring.notification.email.from:motor-scoring@local.test}") String mailFrom) {
        this.objectMapper = objectMapper;
        this.mailSender = mailSender;
        this.mailEnabled = mailEnabled;
        this.mailTo = mailTo;
        this.mailFrom = mailFrom;
    }

    @RabbitListener(queues = "${motor-scoring.rabbitmq.email-queue:scoring.notificaciones.email}")
    public void consumir(String payload) throws Exception {
        NotificacionEvaluacionEvent evento = objectMapper.readValue(payload, NotificacionEvaluacionEvent.class);
        String asunto = "Resultado de evaluación de scoring - Solicitud " + evento.idSolicitud();
        String cuerpo = "La evaluación " + evento.idEvaluacion()
                + " finalizó con resultado " + evento.resultado()
                + " y puntaje " + evento.puntajeTotal()
                + ". Versión del modelo: " + evento.versionModelo() + ".";

        if (!mailEnabled) {
            log.info("[RABBITMQ][CORREO SIMULADO] Para: {} | Asunto: {} | {}", mailTo, asunto, cuerpo);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(mailTo);
        message.setSubject(asunto);
        message.setText(cuerpo);
        mailSender.send(message);
        log.info("[RABBITMQ] Correo enviado para la solicitud {} a {}", evento.idSolicitud(), mailTo);
    }
}
