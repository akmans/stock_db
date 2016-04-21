package com.akmans.trade.core.springdata.jpa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages="com.akmans.trade.core.springdata.jpa.dao")
public class RepositoryConfig {
}