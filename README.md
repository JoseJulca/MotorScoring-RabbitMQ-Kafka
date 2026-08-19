# Integración de RabbitMQ y Kafka en el Motor de Scoring Crediticio

## 1. Objetivo

Se agregaron RabbitMQ y Kafka al backend Java con Arquitectura Hexagonal sin reemplazar el flujo funcional existente de registro y evaluación de solicitudes.

La evaluación continúa funcionando de forma síncrona:

```text
Web
  ↓
API
  ↓
Caso de uso de evaluación
  ↓
Lógica de scoring
  ↓
MongoDB / H2
  ↓
Respuesta a la Web
```

Una vez finalizada correctamente la evaluación, se publican dos eventos adicionales:

- **RabbitMQ**: para notificación por correo.
- **Kafka**: para auditoría.

Esto permite incorporar mensajería sin modificar el comportamiento actual de la Web ni la lógica de negocio del scoring.

---

## 2. Módulos agregados

Se agregaron cuatro módulos independientes al proyecto Hexagonal:

```text
motor-scoring-adapter-out-rabbitmq
motor-scoring-adapter-in-rabbitmq

motor-scoring-adapter-out-kafka
motor-scoring-adapter-in-kafka
```

La separación por módulos permite que cada adapter declare únicamente las dependencias de la tecnología que utiliza.

Por ejemplo:

```text
motor-scoring-adapter-out-rabbitmq
    → Spring AMQP / RabbitMQ

motor-scoring-adapter-in-rabbitmq
    → Spring AMQP / RabbitMQ
    → Mail

motor-scoring-adapter-out-kafka
    → Spring Kafka

motor-scoring-adapter-in-kafka
    → Spring Kafka
    → SLF4J
```

De esta forma, el código RabbitMQ no necesita referencias de Kafka y el código Kafka no necesita referencias de RabbitMQ.

---

## 3. Estructura resultante

```text
motor-scoring-crediticio/
│
├── motor-scoring-domain/
│
├── motor-scoring-application/
│
├── motor-scoring-infrastructure/
│   └── Persistencia JPA / H2
│
├── motor-scoring-adapter-out-mongodb/
│   └── Persistencia MongoDB
│
├── motor-scoring-adapter-out-rabbitmq/
│   └── Publica mensajes de notificación
│
├── motor-scoring-adapter-in-rabbitmq/
│   └── Consume mensajes y ejecuta notificación por correo
│
├── motor-scoring-adapter-out-kafka/
│   └── Publica eventos de auditoría
│
├── motor-scoring-adapter-in-kafka/
│   └── Consume eventos y registra auditoría
│
├── motor-scoring-presentation/
│
└── motor-scoring-bootstrap/
```

---

## 4. Diagrama del flujo completo

```mermaid
flowchart TD
    A[Web] --> B[API Java]
    B --> C[EjecutarEvaluacionScoringUseCase]
    C --> D[EjecutarEvaluacionScoringService]
    D --> E[CalculadorScoring]
    E --> F[Repository]

    F --> G[(MongoDB)]
    F --> H[(H2)]

    D --> I[Resultado de evaluación]
    I --> A

    I --> J[Publisher RabbitMQ]
    J --> K[(RabbitMQ)]
    K --> L[NotificacionEmailConsumer]
    L --> M[Correo simulado]

    I --> N[Publisher Kafka]
    N --> O[(Kafka)]
    O --> P[AuditoriaScoringConsumer]
    P --> Q[/app/audit/auditoria-scoring.jsonl]
```

El punto importante es que RabbitMQ y Kafka se ejecutan después de completar la evaluación.

La Web sigue recibiendo el resultado del scoring como antes.

---

## 5. Flujo de RabbitMQ

RabbitMQ se usa para desacoplar el envío de una notificación por correo.

```text
Evaluación completada
        ↓
RabbitMQ Publisher
        ↓
Exchange / Routing Key
        ↓
Queue: scoring.notificaciones.email
        ↓
NotificacionEmailConsumer
        ↓
Correo simulado
```

En la implementación actual el correo es simulado.

Ejemplo observado en logs:

```text
[RABBITMQ][CORREO SIMULADO]
Para: demo@local.test
Asunto: Resultado de evaluación de scoring - Solicitud 10
La evaluación 7 finalizó con resultado PREAPROBADA y puntaje 1000.
Versión del modelo: 1.1.0.
```

Esto confirma que:

1. El evento fue publicado.
2. RabbitMQ lo recibió.
3. El consumer estaba conectado.
4. El mensaje fue consumido.
5. Se ejecutó la lógica de notificación.

---

## 6. Flujo de Kafka

Kafka se utiliza para registrar auditoría de las evaluaciones realizadas.

```text
Evaluación completada
        ↓
Kafka Publisher
        ↓
Topic de auditoría
        ↓
AuditoriaScoringConsumer
        ↓
Archivo de auditoría
        ↓
/app/audit/auditoria-scoring.jsonl
```

Ejemplo observado en logs:

```text
[KAFKA][AUDITORIA] Evento d4bccefd-... registrado para solicitud 10
```

Esto confirma que:

1. El producer publicó el evento.
2. Kafka recibió el evento.
3. El consumer del topic estaba activo.
4. El consumer procesó el evento.
5. Se registró la auditoría.

---

## 7. Servicios Docker

Al levantar el proyecto con:

```powershell
docker compose up --build
```

se levantan los servicios principales:

```text
motor-scoring-web
motor-scoring-api
motor-scoring-mongodb
motor-scoring-mongodb-init
motor-scoring-rabbitmq
motor-scoring-kafka
```

Los adapters Java de RabbitMQ y Kafka no son containers independientes.

Se compilan dentro del mismo backend:

```text
motor-scoring-api
```

Dentro del proceso Spring Boot quedan activos:

```text
REST Controllers
RabbitMQ Publisher
RabbitMQ Consumer
Kafka Publisher
Kafka Consumer
```

---

## 8. Validación de RabbitMQ

### 8.1 Ingresar a la consola

Abrir:

```text
http://localhost:15672
```

Usar las credenciales configuradas en `docker-compose.yml`.

Ir a:

```text
Queues and Streams
```

Seleccionar:

```text
scoring.notificaciones.email
```

Se debe observar:

```text
Consumers: 1
Ready: 0
Unacked: 0
```

Que `Ready` esté en cero es normal si el consumer procesa el mensaje inmediatamente.

La validación importante es:

```text
Consumers = 1
```

Esto confirma que el listener está conectado a la cola.

---

## 9. Validación de RabbitMQ por logs

Ejecutar:

```powershell
docker compose logs -f motor-scoring-api
```

Luego realizar una evaluación desde la Web.

Buscar:

```text
NotificacionEmailConsumer
```

y un mensaje similar a:

```text
[RABBITMQ][CORREO SIMULADO]
```

Si aparece, RabbitMQ está funcionando correctamente de extremo a extremo.

---

## 10. Validación de Kafka por logs

Con el mismo comando:

```powershell
docker compose logs -f motor-scoring-api
```

buscar:

```text
AuditoriaScoringConsumer
```

y un mensaje similar a:

```text
[KAFKA][AUDITORIA] Evento ... registrado para solicitud ...
```

Esto confirma que Kafka publicó y consumió correctamente el evento.

---

## 11. Validación del archivo de auditoría

Entrar al contenedor:

```powershell
docker exec -it motor-scoring-api sh
```

Cuando aparezca:

```text
#
```

significa que ya se está dentro del contenedor.

Ejecutar:

```bash
ls -la /app/audit
```

Después:

```bash
cat /app/audit/auditoria-scoring.jsonl
```

También puede ejecutarse directamente desde PowerShell sin entrar al contenedor:

```powershell
docker exec motor-scoring-api cat /app/audit/auditoria-scoring.jsonl
```

Si aparece el evento correspondiente a la solicitud evaluada, Kafka quedó validado completamente.

---

## 12. Validación recomendada de extremo a extremo

La prueba completa es:

```text
1. Levantar Docker
      ↓
2. Abrir la Web
      ↓
3. Registrar una solicitud
      ↓
4. Evaluar la solicitud
      ↓
5. Verificar que la Web muestre el scoring
      ↓
6. Revisar RabbitMQ
      ↓
7. Revisar logs de NotificacionEmailConsumer
      ↓
8. Revisar logs de AuditoriaScoringConsumer
      ↓
9. Revisar auditoria-scoring.jsonl
```

Resultado esperado:

```text
                    EVALUAR SOLICITUD
                           │
                           ▼
                  Evaluación de Scoring
                           │
             ┌─────────────┼─────────────┐
             │             │             │
             ▼             ▼             ▼
         Web recibe     RabbitMQ        Kafka
          resultado        │              │
             │             ▼              ▼
             │       Consumer correo  Consumer auditoría
             │             │              │
             │             ▼              ▼
             │       Correo simulado  Archivo JSONL
             │
             ▼
         PREAPROBADA
         Puntaje 1000
```

---

## 13. Cambios técnicos realizados durante la integración

### 13.1 Dependencia SLF4J en Kafka Consumer

`AuditoriaScoringConsumer` utiliza:

```java
Logger
LoggerFactory
```

Por ello se agregó `slf4j-api` en:

```text
motor-scoring-adapter-in-kafka/pom.xml
```

### 13.2 Regla ArchUnit del Bootstrap

El test de arquitectura impedía que las clases de `bootstrap` dependieran de SLF4J.

Se agregó:

```java
"org.slf4j.."
```

a la lista permitida en:

```text
HexagonalArchitectureTest.bootstrapPuedeConocerTodosLosModulosDelProyecto
```

Esto permite logging en Bootstrap sin alterar las reglas principales de la Arquitectura Hexagonal.

---

## 14. Resultado final

Con los cambios realizados, el Motor de Scoring conserva el flujo funcional original y agrega capacidades adicionales desacopladas:

```text
Persistencia:
    H2
    MongoDB

Mensajería:
    RabbitMQ → notificaciones
    Kafka    → auditoría
```

La lógica de scoring no conoce directamente:

```text
MongoDB
H2
RabbitMQ
Kafka
```

Estas tecnologías permanecen en adapters externos, manteniendo el objetivo principal de la Arquitectura Hexagonal: aislar la lógica de negocio de los detalles técnicos.
