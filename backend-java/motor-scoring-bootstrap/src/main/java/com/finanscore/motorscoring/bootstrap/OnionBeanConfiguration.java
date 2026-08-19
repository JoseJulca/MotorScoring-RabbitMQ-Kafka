package com.finanscore.motorscoring.bootstrap;

import com.finanscore.motorscoring.application.service.*;
import com.finanscore.motorscoring.application.usecase.*;
import com.finanscore.motorscoring.application.port.out.*;
import com.finanscore.motorscoring.bootstrap.messaging.PublicarEventosEvaluacionUseCase;
import com.finanscore.motorscoring.domain.repository.*;
import com.finanscore.motorscoring.domain.service.*;
import com.finanscore.motorscoring.infrastructure.transaction.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.time.Clock;


/**
 * Composition Root del sistema. Se conserva el nombre original para evidenciar
 * la evolución desde Onion hacia Hexagonal: los casos de uso siguen recibiendo
 * puertos del núcleo y el profile activo selecciona el adaptador H2 o MongoDB.
 */
@Configuration
public class OnionBeanConfiguration {
	@Bean
	Clock clock() {
		return Clock.systemUTC();
	}

	@Bean
	CalculadorCapacidadPago capacidad() {
		return new CalculadorCapacidadPago();
	}

	@Bean
	CalculadorRelacionDeudaIngreso relacion() {
		return new CalculadorRelacionDeudaIngreso();
	}

	@Bean
	CalculadorRelacionCuotaIngreso relacionCuotaIngreso() {
		return new CalculadorRelacionCuotaIngreso();
	}

	@Bean
	EvaluadorReglasExcluyentes excluyentes() {
		return new EvaluadorReglasExcluyentes();
	}

	@Bean
	CalculadorScoring calculador(CalculadorCapacidadPago c, CalculadorRelacionDeudaIngreso r,
		    CalculadorRelacionCuotaIngreso rci,
			EvaluadorReglasExcluyentes e) {
		return new CalculadorScoring(c, r,rci, e);
	}

	@Bean(name = "crearCore")
	CrearSolicitudCreditoUseCase crearCore(SolicitanteRepository s, SolicitudCreditoRepository q, ProductoCrediticioRepository p, Clock c) {
		return new CrearSolicitudCreditoService(s, q, p, c);
	}

	@Bean(name = "evaluarCore")
	EjecutarEvaluacionScoringUseCase evaluarCore(SolicitudCreditoRepository q, SolicitanteRepository s, ProductoCrediticioRepository p, ModeloScoringRepository m, EvaluacionCrediticiaRepository e, CalculadorScoring c, Clock clock) {
		return new EjecutarEvaluacionScoringService(q, s, p, m, e, c, clock);
	}

	@Bean
	@Primary
	CrearSolicitudCreditoUseCase crear(@Qualifier("crearCore") CrearSolicitudCreditoUseCase core, PlatformTransactionManager tm) {
		return new TransactionalCrearSolicitudCreditoUseCase(core, new TransactionTemplate(tm));
	}

	@Bean(name = "evaluarTransactional")
	EjecutarEvaluacionScoringUseCase evaluarTransactional(@Qualifier("evaluarCore") EjecutarEvaluacionScoringUseCase core, PlatformTransactionManager tm) {
		return new TransactionalEjecutarEvaluacionScoringUseCase(core, new TransactionTemplate(tm));
	}

	@Bean
	@Primary
	@ConditionalOnProperty(name = "motor-scoring.messaging.enabled", havingValue = "true", matchIfMissing = true)
	EjecutarEvaluacionScoringUseCase evaluarConMensajeria(
			@Qualifier("evaluarTransactional") EjecutarEvaluacionScoringUseCase transactional,
			NotificacionEvaluacionPublisher notificaciones,
			AuditoriaScoringPublisher auditoria,
			Clock clock) {
		return new PublicarEventosEvaluacionUseCase(transactional, notificaciones, auditoria, clock);
	}

	@Bean
	@Primary
	@ConditionalOnProperty(name = "motor-scoring.messaging.enabled", havingValue = "false")
	EjecutarEvaluacionScoringUseCase evaluarSinMensajeria(
			@Qualifier("evaluarTransactional") EjecutarEvaluacionScoringUseCase transactional) {
		return transactional;
	}
}
