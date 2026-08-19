package com.finanscore.motorscoring.bootstrap.architecture;

import com.finanscore.motorscoring.domain.repository.*;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

class HexagonalArchitectureTest {
    private static final String ROOT = "com.finanscore.motorscoring";

    @Test
    void domainNoDependeDeSpringMongoDbNiJpa() {
        var classes = new ClassFileImporter().importPackages(ROOT);
        noClasses().that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..", "com.mongodb..", "org.bson..",
                        "jakarta.persistence..", "org.hibernate..")
                .check(classes);
    }

    @Test
    void applicationNoDependeDeAdaptadoresNiPresentation() {
        var classes = new ClassFileImporter().importPackages(ROOT);
        noClasses().that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..mongodb..", "..infrastructure..", "..presentation..",
                        "..rabbitmq..", "..kafka..")
                .check(classes);
    }

    @Test
    void adaptadoresMongoImplementanPuertosDelNucleo() {
        var classes = new ClassFileImporter().importPackages(ROOT);
        classes().that().haveSimpleName("SolicitanteMongoRepositoryAdapter").should().implement(SolicitanteRepository.class).check(classes);
        classes().that().haveSimpleName("SolicitudCreditoMongoRepositoryAdapter").should().implement(SolicitudCreditoRepository.class).check(classes);
        classes().that().haveSimpleName("ProductoCrediticioMongoRepositoryAdapter").should().implement(ProductoCrediticioRepository.class).check(classes);
        classes().that().haveSimpleName("ModeloScoringMongoRepositoryAdapter").should().implement(ModeloScoringRepository.class).check(classes);
        classes().that().haveSimpleName("EvaluacionCrediticiaMongoRepositoryAdapter").should().implement(EvaluacionCrediticiaRepository.class).check(classes);
    }

    @Test
    void adaptadoresH2ImplementanPuertosDelNucleo() {
        var classes = new ClassFileImporter().importPackages(ROOT);
        classes().that().haveSimpleName("SolicitanteRepositoryAdapter").should().implement(SolicitanteRepository.class).check(classes);
        classes().that().haveSimpleName("SolicitudCreditoRepositoryAdapter").should().implement(SolicitudCreditoRepository.class).check(classes);
        classes().that().haveSimpleName("ProductoCrediticioRepositoryAdapter").should().implement(ProductoCrediticioRepository.class).check(classes);
        classes().that().haveSimpleName("ModeloScoringRepositoryAdapter").should().implement(ModeloScoringRepository.class).check(classes);
        classes().that().haveSimpleName("EvaluacionCrediticiaRepositoryAdapter").should().implement(EvaluacionCrediticiaRepository.class).check(classes);
    }

    @Test
    void presentationConsumePuertosDeEntradaYNoPersistencia() {
        var classes = new ClassFileImporter().importPackages(ROOT);
        classes().that().resideInAPackage("..presentation.controller..")
                .should().dependOnClassesThat().resideInAPackage("..application.usecase..")
                .check(classes);
        noClasses().that().resideInAPackage("..presentation..")
                .should().dependOnClassesThat().resideInAnyPackage("..mongodb..", "..infrastructure.persistence..")
                .check(classes);
    }

    @Test
    void adaptadoresDePersistenciaNoDependenEntreSi() {
        var classes = new ClassFileImporter().importPackages(ROOT);
        noClasses().that().resideInAPackage("..mongodb..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
                .check(classes);
        noClasses().that().resideInAPackage("..infrastructure..")
                .should().dependOnClassesThat().resideInAPackage("..mongodb..")
                .check(classes);
    }

    @Test
    void adaptadoresDeMensajeriaNoDependenEntreSi() {
        var classes = new ClassFileImporter().importPackages(ROOT);
        noClasses().that().resideInAPackage("..rabbitmq..")
                .should().dependOnClassesThat().resideInAPackage("..kafka..")
                .check(classes);
        noClasses().that().resideInAPackage("..kafka..")
                .should().dependOnClassesThat().resideInAPackage("..rabbitmq..")
                .check(classes);
    }

    @Test
    void adaptadoresInYOutDeCadaBrokerNoSeReferencianDirectamente() {
        var classes = new ClassFileImporter().importPackages(ROOT);
        noClasses().that().resideInAPackage("..rabbitmq.in..")
                .should().dependOnClassesThat().resideInAPackage("..rabbitmq.out..")
                .check(classes);
        noClasses().that().resideInAPackage("..kafka.in..")
                .should().dependOnClassesThat().resideInAPackage("..kafka.out..")
                .check(classes);
    }

    @Test
    void bootstrapPuedeConocerTodosLosModulosDelProyecto() {
        var classes = new ClassFileImporter().importPackages(ROOT);
        classes().that().resideInAPackage("..bootstrap..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "java..", "org.springframework..","org.slf4j..", "org.junit..", "org.testcontainers..",
                        "com.tngtech.archunit..", ROOT + "..")
                .check(classes);
    }
}
