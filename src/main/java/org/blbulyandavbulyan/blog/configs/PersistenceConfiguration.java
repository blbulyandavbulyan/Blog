package org.blbulyandavbulyan.blog.configs;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.persistence.EntityManagerFactory;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

import static org.blbulyandavbulyan.blog.utils.PropertiesUtils.getPropertiesByPrefix;

/**
 * Данный класс предоставляет конфигурацию для работы с hibernate
 */
@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = "org.blbulyandavbulyan.blog.entities")
@EnableJpaRepositories(basePackages = "org.blbulyandavbulyan.blog.repositories")
public class PersistenceConfiguration {
    @Bean(destroyMethod = "close")
    DataSource dataSource(Environment env) {
        HikariConfig dataSourceConfig = new HikariConfig();
        dataSourceConfig.setDriverClassName(env.getRequiredProperty("db.driver"));
        dataSourceConfig.setJdbcUrl(env.getRequiredProperty("db.url"));
        dataSourceConfig.setUsername(env.getRequiredProperty("db.username"));
        dataSourceConfig.setPassword(env.getRequiredProperty("db.password"));
        return new HikariDataSource(dataSourceConfig);
    }
    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, ConfigurableEnvironment env) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.setPackagesToScan(PersistenceConfiguration.class.getAnnotation(EntityScan.class).basePackages());
        //находим все свойства по префиксу hibernate
        entityManagerFactoryBean.setJpaProperties(getPropertiesByPrefix("hibernate", env));
        return entityManagerFactoryBean;
    }
    @Bean
    JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
