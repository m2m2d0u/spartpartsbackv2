package sn.symmetry.spareparts.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis cache configuration with specific TTLs for different cache types.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    // Cache names
    public static final String PERMISSIONS_CACHE = "permissions";
    public static final String PERMISSIONS_ALL_CACHE = "permissions:all";
    public static final String PERMISSIONS_BY_CATEGORY_CACHE = "permissions:by-category";
    public static final String PERMISSIONS_BY_LEVEL_CACHE = "permissions:by-level";

    public static final String ROLES_CACHE = "roles";
    public static final String ROLES_ALL_CACHE = "roles:all";
    public static final String ROLES_SYSTEM_CACHE = "roles:system";
    public static final String ROLES_CUSTOM_CACHE = "roles:custom";

    public static final String USER_ME_CACHE = "user:me";
    public static final String USER_WAREHOUSE_PERMISSIONS_CACHE = "user:warehouse:permissions";

    // Company settings - singleton, read very frequently, rarely updated
    public static final String COMPANY_SETTINGS_CACHE = "company-settings";

    // Reference data caches - read often, updated infrequently
    public static final String CATEGORIES_CACHE = "categories";
    public static final String SUPPLIERS_CACHE = "suppliers";
    public static final String CUSTOMERS_CACHE = "customers";
    public static final String INVOICE_TEMPLATES_CACHE = "invoice-templates";
    public static final String TAX_RATES_CACHE = "tax-rates";

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        GenericJacksonJsonRedisSerializer jsonSerializer = GenericJacksonJsonRedisSerializer.builder()
                .enableDefaultTyping(BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Object.class)
                        .build())
                .build();

        // Default configuration (1 hour TTL)
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));

        // Permissions cache - long TTL (24 hours) as permissions rarely change
        RedisCacheConfiguration permissionsConfig = defaultConfig.entryTtl(Duration.ofHours(24));

        // Roles cache - long TTL (12 hours) as roles change infrequently
        RedisCacheConfiguration rolesConfig = defaultConfig.entryTtl(Duration.ofHours(12));

        // User context cache - medium TTL (30 minutes) as user permissions can change
        RedisCacheConfiguration userMeConfig = defaultConfig.entryTtl(Duration.ofMinutes(30));

        // User warehouse permissions - medium TTL (30 minutes)
        RedisCacheConfiguration userWarehousePermConfig = defaultConfig.entryTtl(Duration.ofMinutes(30));

        // Company settings - long TTL (6 hours) as it's a singleton that rarely changes
        RedisCacheConfiguration companySettingsConfig = defaultConfig.entryTtl(Duration.ofHours(6));

        // Reference data - medium-long TTL (2 hours)
        RedisCacheConfiguration referenceDataConfig = defaultConfig.entryTtl(Duration.ofHours(2));

        // Customer cache - shorter TTL (1 hour) as customers are updated more frequently
        RedisCacheConfiguration customerConfig = defaultConfig.entryTtl(Duration.ofHours(1));

        // Configure specific caches with different TTLs
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Permissions caches
        cacheConfigurations.put(PERMISSIONS_CACHE, permissionsConfig);
        cacheConfigurations.put(PERMISSIONS_ALL_CACHE, permissionsConfig);
        cacheConfigurations.put(PERMISSIONS_BY_CATEGORY_CACHE, permissionsConfig);
        cacheConfigurations.put(PERMISSIONS_BY_LEVEL_CACHE, permissionsConfig);

        // Roles caches
        cacheConfigurations.put(ROLES_CACHE, rolesConfig);
        cacheConfigurations.put(ROLES_ALL_CACHE, rolesConfig);
        cacheConfigurations.put(ROLES_SYSTEM_CACHE, rolesConfig);
        cacheConfigurations.put(ROLES_CUSTOM_CACHE, rolesConfig);

        // User caches
        cacheConfigurations.put(USER_ME_CACHE, userMeConfig);
        cacheConfigurations.put(USER_WAREHOUSE_PERMISSIONS_CACHE, userWarehousePermConfig);

        // Company settings
        cacheConfigurations.put(COMPANY_SETTINGS_CACHE, companySettingsConfig);

        // Reference data
        cacheConfigurations.put(CATEGORIES_CACHE, referenceDataConfig);
        cacheConfigurations.put(SUPPLIERS_CACHE, referenceDataConfig);
        cacheConfigurations.put(INVOICE_TEMPLATES_CACHE, referenceDataConfig);
        cacheConfigurations.put(TAX_RATES_CACHE, referenceDataConfig);

        // Customers
        cacheConfigurations.put(CUSTOMERS_CACHE, customerConfig);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
