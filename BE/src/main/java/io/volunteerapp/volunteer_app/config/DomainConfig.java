package io.volunteerapp.volunteer_app.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EntityScan("io.volunteerapp.volunteer_app.domain")
@EnableJpaRepositories("io.volunteerapp.volunteer_app.repos")
@EnableTransactionManagement
public class DomainConfig {
}
