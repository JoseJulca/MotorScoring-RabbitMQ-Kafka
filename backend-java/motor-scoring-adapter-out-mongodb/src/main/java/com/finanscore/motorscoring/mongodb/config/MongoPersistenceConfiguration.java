package com.finanscore.motorscoring.mongodb.config;

import com.finanscore.motorscoring.mongodb.repository.EvaluacionCrediticiaMongoSpringDataRepository;
import org.springframework.context.annotation.*;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configura el adaptador de salida MongoDB. Esta configuración solo se activa
 * con el profile mongodb; el núcleo no conoce MongoDB ni sus anotaciones.
 */
@Profile("mongodb")
@Configuration(proxyBeanMethods = false)
@EnableMongoRepositories(basePackageClasses = EvaluacionCrediticiaMongoSpringDataRepository.class)
public class MongoPersistenceConfiguration {

    @Bean
    PlatformTransactionManager mongoTransactionManager(MongoDatabaseFactory databaseFactory) {
        return new MongoTransactionManager(databaseFactory);
    }
}
