package org.blbulyandavbulyan.blog.configs;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Данный класс предоставляет конфигурацию для работы с hibernate
 */
@Configuration
@EnableTransactionManagement
public class PersistenceConfiguration {
}
