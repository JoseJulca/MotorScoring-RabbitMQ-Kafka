# Validación de la ampliación RabbitMQ + Kafka

## Validaciones realizadas sobre la estructura

- Los cuatro nuevos módulos están declarados en el `pom.xml` padre.
- `motor-scoring-bootstrap` incluye los cuatro módulos para empaquetarlos en el mismo JAR ejecutable.
- Los POM XML fueron validados sintácticamente.
- Los archivos `docker-compose.yml`, `docker-compose.h2.yml`, `application.yml` y `application-test.yml` fueron validados como YAML.
- Los contratos Java agregados a Application compilan de forma aislada con JDK 21.
- Los módulos RabbitMQ no tienen imports ni dependencias Maven hacia Kafka.
- Los módulos Kafka no tienen imports ni dependencias Maven hacia RabbitMQ.
- Los adapters `in` no dependen directamente de los adapters `out` del mismo broker.
- Se agregaron reglas ArchUnit para proteger estas separaciones.

## Validación recomendada en el equipo local

Desde `backend-java`:

```bash
mvn clean verify
```

Desde la raíz del repositorio, MongoDB:

```bash
docker compose up --build
```

O H2:

```bash
docker compose -f docker-compose.h2.yml up --build
```

Después de evaluar una solicitud, verificar:

```bash
docker compose logs motor-scoring-api
```

Debe aparecer un log RabbitMQ similar a:

```text
[RABBITMQ][CORREO SIMULADO]
```

Y un log Kafka similar a:

```text
[KAFKA][AUDITORIA]
```

La auditoría queda en el volumen Docker `motor_scoring_audit_data`, archivo interno:

```text
/app/audit/auditoria-scoring.jsonl
```

RabbitMQ Management:

```text
http://localhost:15672
usuario: scoring
password: scoring123
```

Kafka para clientes del host:

```text
localhost:29092
```
