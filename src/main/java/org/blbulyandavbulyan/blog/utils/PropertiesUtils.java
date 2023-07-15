package org.blbulyandavbulyan.blog.utils;

import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Collections;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Утилитный класс для работы со свойствами
 */
public class PropertiesUtils {
    /**
     * Данный метод находит свойства ключ которых начинается с заданного значения
     * @param keyPrefix ключ, с которого должно начинаться свойство
     * @param environment окружение spring
     * @return возвращает свойства, ключ которых, начинается с keyPrefix
     */
    public static Properties getPropertiesByPrefix(String keyPrefix, ConfigurableEnvironment environment){
        final Properties properties = new Properties();
        final var propertySources = environment.getPropertySources().stream().collect(Collectors.toList());
        Collections.reverse(propertySources);
        for (PropertySource<?> source : propertySources) {
            if (source instanceof MapPropertySource) {
                final var mapProperties = ((MapPropertySource) source).getSource();
                mapProperties.forEach((key, value) -> {
                    if (key.startsWith(keyPrefix)) {
                        properties.put(key, value instanceof OriginTrackedValue ? ((OriginTrackedValue) value).getValue() : value);
                    }
                });
            }
        }
        return properties;
    }
}
