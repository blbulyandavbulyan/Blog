package org.blbulyandavbulyan.blog.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Данный класс предоставляет конфигурацию для работы с hibernate
 */
@Configuration
@EnableTransactionManagement
public class PersistenceConfiguration {
}
