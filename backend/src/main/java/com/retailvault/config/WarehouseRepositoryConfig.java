package com.retailvault.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.retailvault.repository.warehouse",
    entityManagerFactoryRef = "warehouseEntityManagerFactory",
    transactionManagerRef = "warehouseTransactionManager"
)
public class WarehouseRepositoryConfig {
}
