package com.akmans.trade.core.springdata.jpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.akmans.trade.core.springdata.auditing.AuditingDateTimeProvider;
import com.akmans.trade.core.springdata.auditing.CurrentTimeDateTimeService;
import com.akmans.trade.core.springdata.auditing.DateTimeService;
import com.akmans.trade.core.springdata.auditing.UsernameAuditorAware;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider", auditorAwareRef = "auditorProvider")
@EnableJpaRepositories(basePackages = "com.akmans.trade.core.springdata.jpa.repositories", namedQueriesLocation = "classpath:/META-INF/core/jpa/jpa-named-queries.properties")
@EnableTransactionManagement
public class RepositoryConfig {

	@Bean
	AuditorAware<String> auditorProvider() {
		return new UsernameAuditorAware();
	}

	@Bean
	DateTimeService currentTimeDateTimeService() {
		return new CurrentTimeDateTimeService();
	}

	@Bean
	DateTimeProvider dateTimeProvider(DateTimeService dateTimeService) {
		return new AuditingDateTimeProvider(dateTimeService);
	}
}