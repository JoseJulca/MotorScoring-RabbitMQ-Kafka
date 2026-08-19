# RabbitMQ y Kafka en la versión Hexagonal

Esta versión conserva el flujo síncrono existente de evaluación. El endpoint `POST /api/solicitudes-credito/{id}/evaluar` continúa calculando el scoring, persistiendo la evaluación y devolviendo el mismo resultado al cliente.

Después de que la transacción actual termina correctamente se ejecutan dos efectos adicionales:

```text
Evaluación completada
        |
        +--> RabbitMQ --> NotificacionEmailConsumer
        |
        +--> Kafka -----> AuditoriaScoringConsumer
```

Una falla posterior de RabbitMQ o Kafka no invalida una evaluación que ya fue persistida.

## Módulos agregados

```text
motor-scoring-adapter-out-rabbitmq
motor-scoring-adapter-in-rabbitmq
motor-scoring-adapter-out-kafka
motor-scoring-adapter-in-kafka
```

Los adapters RabbitMQ y Kafka no se referencian entre sí. Los adapters `in` tampoco dependen de sus adapters `out`: comparten únicamente los contratos y eventos definidos en `motor-scoring-application`.

## Contratos agregados en Application

```text
application/event/
  NotificacionEvaluacionEvent
  AuditoriaScoringEvent

application/port/out/
  NotificacionEvaluacionPublisher
  AuditoriaScoringPublisher
```

`PublicarEventosEvaluacionUseCase`, ubicado en Bootstrap como decorador, envuelve al caso de uso transaccional existente. No modifica `EjecutarEvaluacionScoringService` ni `CalculadorScoring`.

## RabbitMQ

### Publicación

`RabbitMqNotificacionEvaluacionPublisher` implementa `NotificacionEvaluacionPublisher` y publica en:

```text
Exchange: scoring.events
Routing key: scoring.email
Queue: scoring.notificaciones.email
```

Los nombres pueden cambiarse mediante variables de configuración sin modificar el código.

### Consumo

`NotificacionEmailConsumer` escucha la cola automáticamente al iniciar Spring Boot.

Por defecto el envío SMTP está desactivado y el consumer escribe en logs un correo simulado. Esto permite probar RabbitMQ sin necesitar credenciales de correo.

Para habilitar correo real:

```text
SCORING_EMAIL_ENABLED=true
SCORING_EMAIL_TO=destino@dominio.com
SCORING_EMAIL_FROM=motor-scoring@dominio.com
SMTP_HOST=smtp.dominio.com
SMTP_PORT=587
SMTP_USERNAME=usuario
SMTP_PASSWORD=secreto
SMTP_AUTH=true
SMTP_STARTTLS=true
```

## Kafka

### Publicación

`KafkaAuditoriaScoringPublisher` implementa `AuditoriaScoringPublisher` y publica el evento:

```text
EVALUACION_SCORING_COMPLETADA
```

en el topic:

```text
scoring.evaluaciones.auditoria
```

### Consumo y auditoría

`AuditoriaScoringConsumer` consume el topic y registra cada evento como JSON Lines.

En Docker:

```text
/app/audit/auditoria-scoring.jsonl
```

El archivo se conserva en el volumen:

```text
motor_scoring_audit_data
```

Cada evento contiene:

```text
eventId
tipoEvento
fechaEvento
idEvaluacion
idSolicitud
puntajeTotal
resultado
estado
versionModelo
```

La auditoría se mantiene separada de las tablas y colecciones funcionales existentes para no alterar el modelo actual de scoring.

## Docker

Con MongoDB:

```bash
docker compose up --build
```

Con H2:

```bash
docker compose -f docker-compose.h2.yml up --build
```

Servicios adicionales:

```text
motor-scoring-rabbitmq
motor-scoring-kafka
```

RabbitMQ Management:

```text
http://localhost:15672
usuario: scoring
password: scoring123
```

Kafka se expone para herramientas del host en:

```text
localhost:29092
```

La API usa internamente:

```text
rabbitmq:5672
kafka:9092
```

## Flujo de evaluación

```text
Cliente
   |
   | POST /evaluar
   v
SolicitudCreditoController
   |
   v
TransactionalEjecutarEvaluacionScoringUseCase
   |
   v
EjecutarEvaluacionScoringService
   |
   +--> CalculadorScoring
   +--> Repository --> H2 o MongoDB
   |
   v
COMMIT
   |
   v
PublicarEventosEvaluacionUseCase
   |
   +--> NotificacionEvaluacionPublisher
   |       --> RabbitMQ
   |       --> NotificacionEmailConsumer
   |
   +--> AuditoriaScoringPublisher
           --> Kafka
           --> AuditoriaScoringConsumer
           --> auditoria-scoring.jsonl
   |
   v
Respuesta original de evaluación
```

La Web no necesita polling ni cambios de endpoints porque RabbitMQ y Kafka se ejecutan después del flujo funcional ya existente.
