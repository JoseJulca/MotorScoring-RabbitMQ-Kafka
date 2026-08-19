package com.finanscore.motorscoring.mongodb.initializer;

import com.finanscore.motorscoring.domain.enums.*;
import com.finanscore.motorscoring.mongodb.document.*;
import com.finanscore.motorscoring.mongodb.document.ModeloScoringMongoDocument.*;
import com.finanscore.motorscoring.mongodb.repository.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Equivalente MongoDB de las migraciones Flyway V2 y V3. Es idempotente: solo
 * inserta el modelo y el producto de referencia cuando aún no existen.
 */
@Profile("mongodb")
@Component
public class MongoReferenceDataInitializer implements ApplicationRunner {
    private final ModeloScoringMongoSpringDataRepository modelos;
    private final ProductoCrediticioMongoSpringDataRepository productos;

    public MongoReferenceDataInitializer(ModeloScoringMongoSpringDataRepository modelos,
                                         ProductoCrediticioMongoSpringDataRepository productos) {
        this.modelos = modelos;
        this.productos = productos;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!modelos.existsById(1L)) {
            modelos.save(crearModelo());
        }
        if (productos.findByCodigo("PRESTAMO_PERSONAL").isEmpty()) {
            productos.save(new ProductoCrediticioMongoDocument(
                    1L, "PRESTAMO_PERSONAL", "Préstamo personal",
                    bd("1000"), bd("50000"), 6, 48,
                    Moneda.PEN, EstadoProducto.ACTIVO, 1L));
        }
    }

    private ModeloScoringMongoDocument crearModelo() {
        var version100 = new VersionMongoDocument(
                1L, "1.0.0", LocalDate.of(2025, 1, 1), LocalDate.of(2026, 7, 19),
                EstadoVersionModelo.INACTIVA,
                factoresVersion100());

        var version110 = new VersionMongoDocument(
                2L, "1.1.0", LocalDate.of(2026, 7, 20), null,
                EstadoVersionModelo.ACTIVA,
                factoresVersion110());

        return new ModeloScoringMongoDocument(
                1L, "MODELO_PERSONAL", "Modelo de préstamo personal",
                "Modelo inicial RF04-RF06 con versiones 1.0.0 y 1.1.0",
                EstadoModelo.ACTIVO, List.of(version100, version110));
    }

    private List<FactorMongoDocument> factoresVersion100() {
        return List.of(
                factor(1, "HISTORIAL_PAGOS", "Historial de pagos", "Comportamiento histórico", "25", reglasHistorial(1)),
                factor(2, "RELACION_DEUDA_INGRESO", "Relación deuda-ingreso", "Obligaciones respecto del ingreso", "20", reglasRdi(5)),
                factor(3, "CAPACIDAD_PAGO", "Capacidad de pago", "Ingreso disponible", "20", reglasCapacidad(9)),
                factor(4, "ESTABILIDAD_INGRESOS", "Estabilidad de ingresos", "Continuidad de ingresos", "15", reglasEstabilidad(13)),
                factor(5, "ANTIGUEDAD_LABORAL", "Antigüedad laboral", "Meses de permanencia", "10", reglasAntiguedad(17)),
                factor(6, "OBLIGACIONES_ACTIVAS", "Obligaciones activas", "Cantidad de obligaciones", "5", reglasObligaciones(20)),
                factor(7, "MONTO_CAPACIDAD", "Monto frente a capacidad", "Monto solicitado respecto a capacidad", "5", reglasMonto(24)),
                factor(8, "ALERTAS_MORA", "Alertas de mora", "Regla excluyente", "0", reglasAlertas(28)));
    }

    private List<FactorMongoDocument> factoresVersion110() {
        return List.of(
                factor(9, "HISTORIAL_PAGOS", "Historial de pagos", "Comportamiento histórico", "22.50", reglasHistorial(30)),
                factor(10, "RELACION_DEUDA_INGRESO", "Relación deuda-ingreso", "Obligaciones respecto del ingreso", "18.00", reglasRdi(34)),
                factor(11, "CAPACIDAD_PAGO", "Capacidad de pago", "Ingreso disponible", "18.00", reglasCapacidad(38)),
                factor(12, "ESTABILIDAD_INGRESOS", "Estabilidad de ingresos", "Continuidad de ingresos", "13.50", reglasEstabilidad(42)),
                factor(13, "ANTIGUEDAD_LABORAL", "Antigüedad laboral", "Meses de permanencia", "9.00", reglasAntiguedad(46)),
                factor(14, "OBLIGACIONES_ACTIVAS", "Obligaciones activas", "Cantidad de obligaciones", "4.50", reglasObligaciones(49)),
                factor(15, "MONTO_CAPACIDAD", "Monto frente a capacidad", "Monto solicitado respecto a capacidad", "4.50", reglasMonto(53)),
                factor(16, "ALERTAS_MORA", "Alertas de mora", "Regla excluyente", "0.00", reglasAlertas(57)),
                factor(17, "RELACION_CUOTA_INGRESO", "Relación cuota-ingreso", "Porcentaje de la cuota mensual respecto de los ingresos mensuales", "10.00", reglasCuota(59)));
    }

    private FactorMongoDocument factor(long id, String codigo, String nombre, String descripcion, String peso,
                                       List<ReglaMongoDocument> reglas) {
        return new FactorMongoDocument(id, codigo, nombre, descripcion, bd(peso), EstadoFactor.ACTIVO, reglas);
    }

    private List<ReglaMongoDocument> reglasHistorial(long id) {
        return List.of(
                regla(id, "HP_BAJO", "Historial deficiente", "0", "39.9999", 20),
                regla(id + 1, "HP_REGULAR", "Historial regular", "40", "59.9999", 50),
                regla(id + 2, "HP_BUENO", "Historial bueno", "60", "79.9999", 75),
                regla(id + 3, "HP_EXCELENTE", "Historial excelente", "80", "100", 100));
    }

    private List<ReglaMongoDocument> reglasRdi(long id) {
        return List.of(
                regla(id, "RDI_BAJA", "Endeudamiento bajo", "0", "20", 100),
                regla(id + 1, "RDI_MEDIA", "Endeudamiento moderado", "20.0001", "35", 80),
                regla(id + 2, "RDI_ALTA", "Endeudamiento elevado", "35.0001", "50", 50),
                regla(id + 3, "RDI_MUY_ALTA", "Endeudamiento muy elevado", "50.0001", "9999", 20));
    }

    private List<ReglaMongoDocument> reglasCapacidad(long id) {
        return List.of(
                regla(id, "CP_CRITICA", "Capacidad crítica", "0", "9.9999", 20),
                regla(id + 1, "CP_BAJA", "Capacidad baja", "10", "24.9999", 60),
                regla(id + 2, "CP_MEDIA", "Capacidad media", "25", "39.9999", 80),
                regla(id + 3, "CP_ALTA", "Capacidad alta", "40", "100", 100));
    }

    private List<ReglaMongoDocument> reglasEstabilidad(long id) {
        return List.of(
                regla(id, "EI_BAJA", "Ingresos poco estables", "0", "5", 20),
                regla(id + 1, "EI_MEDIA", "Estabilidad inicial", "6", "11", 40),
                regla(id + 2, "EI_BUENA", "Ingresos estables", "12", "23", 70),
                regla(id + 3, "EI_ALTA", "Ingresos muy estables", "24", "9999", 100));
    }

    private List<ReglaMongoDocument> reglasAntiguedad(long id) {
        return List.of(
                regla(id, "AL_BAJA", "Antigüedad menor a un año", "0", "11", 25),
                regla(id + 1, "AL_MEDIA", "Antigüedad entre uno y tres años", "12", "35", 60),
                regla(id + 2, "AL_ALTA", "Antigüedad mayor a tres años", "36", "9999", 100));
    }

    private List<ReglaMongoDocument> reglasObligaciones(long id) {
        return List.of(
                regla(id, "OA_BAJA", "Pocas obligaciones", "0", "1", 100),
                regla(id + 1, "OA_MEDIA", "Obligaciones moderadas", "2", "3", 75),
                regla(id + 2, "OA_ALTA", "Varias obligaciones", "4", "5", 50),
                regla(id + 3, "OA_MUY_ALTA", "Demasiadas obligaciones", "6", "9999", 20));
    }

    private List<ReglaMongoDocument> reglasMonto(long id) {
        return List.of(
                regla(id, "MC_BAJA", "Monto conservador", "0", "30", 100),
                regla(id + 1, "MC_MEDIA", "Monto moderado", "30.0001", "60", 75),
                regla(id + 2, "MC_ALTA", "Monto elevado", "60.0001", "100", 50),
                regla(id + 3, "MC_MUY_ALTA", "Monto superior a capacidad", "100.0001", "9999", 10));
    }

    private List<ReglaMongoDocument> reglasAlertas(long id) {
        return List.of(
                regla(id, "AM_SIN_ALERTAS", "Sin alertas", "0", "0", 100),
                new ReglaMongoDocument(id + 1, "AM_CON_ALERTAS", "Mora vigente", bd("1"), bd("9999"),
                        0, true, ResultadoScoring.RECHAZADA, EstadoRegla.ACTIVA));
    }

    private List<ReglaMongoDocument> reglasCuota(long id) {
        return List.of(
                regla(id, "RCI_BAJA", "Cuota menor o igual al 20% de los ingresos", "0.0000", "20.0000", 100),
                regla(id + 1, "RCI_MODERADA", "Cuota mayor al 20% y menor o igual al 30% de los ingresos", "20.0001", "30.0000", 75),
                regla(id + 2, "RCI_ALTA", "Cuota mayor al 30% y menor o igual al 40% de los ingresos", "30.0001", "40.0000", 40),
                regla(id + 3, "RCI_MUY_ALTA", "Cuota mayor al 40% de los ingresos", "40.0001", "9999.0000", 0));
    }

    private ReglaMongoDocument regla(long id, String codigo, String descripcion, String minimo, String maximo, int puntaje) {
        return new ReglaMongoDocument(id, codigo, descripcion, bd(minimo), bd(maximo), puntaje,
                false, null, EstadoRegla.ACTIVA);
    }

    private BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}
