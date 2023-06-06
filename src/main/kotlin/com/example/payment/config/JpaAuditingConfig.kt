package com.example.payment.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import javax.persistence.EntityListeners

@Configuration
@EnableJpaAuditing
class JpaAuditingConfig {
}