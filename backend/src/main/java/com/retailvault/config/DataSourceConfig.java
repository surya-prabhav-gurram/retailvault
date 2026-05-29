package com.retailvault.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.retailvault.repository.oltp",
    entityManagerFactoryRef = "oltpEntityManagerFactory",
    transactionManagerRef = "oltpTransactionManager"
)
public class DataSourceConfig {

    // ============================================================
    // OLTP DataSource
    // ============================================================
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.oltp")
    public DataSourceProperties oltpDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.oltp.hikari")
    public DataSource oltpDataSource(@Qualifier("oltpDataSourceProperties") DataSourceProperties props) {
        return props.initializeDataSourceBuilder().build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean oltpEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("oltpDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.retailvault.entity.oltp")
                .persistenceUnit("oltp")
                .properties(Map.of(
                        "hibernate.hbm2ddl.auto", "none",
                        "hibernate.dialect", "org.hibernate.dialect.MySQLDialect"
                ))
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager oltpTransactionManager(
            @Qualifier("oltpEntityManagerFactory") LocalContainerEntityManagerFactoryBean factory) {
        return new JpaTransactionManager(factory.getObject());
    }

    // ============================================================
    // Warehouse DataSource
    // ============================================================
    @Bean
    @ConfigurationProperties("spring.datasource.warehouse")
    public DataSourceProperties warehouseDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.warehouse.hikari")
    public DataSource warehouseDataSource(@Qualifier("warehouseDataSourceProperties") DataSourceProperties props) {
        return props.initializeDataSourceBuilder().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean warehouseEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("warehouseDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.retailvault.entity.warehouse")
                .persistenceUnit("warehouse")
                .properties(Map.of(
                        "hibernate.hbm2ddl.auto", "none",
                        "hibernate.dialect", "org.hibernate.dialect.MySQLDialect"
                ))
                .build();
    }

    @Bean
    public PlatformTransactionManager warehouseTransactionManager(
            @Qualifier("warehouseEntityManagerFactory") LocalContainerEntityManagerFactoryBean factory) {
        return new JpaTransactionManager(factory.getObject());
    }
}
