package com.nexora.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration class to enable JPA Auditing.
 * This activates the listener infrastructure required for automatically populating
 * audit timestamps such as {@link org.springframework.data.annotation.CreatedDate}
 * and {@link org.springframework.data.annotation.LastModifiedDate}.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
