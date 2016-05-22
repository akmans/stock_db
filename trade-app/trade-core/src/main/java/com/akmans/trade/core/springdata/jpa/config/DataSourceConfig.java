package com.akmans.trade.core.springdata.jpa.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@PropertySources({ @PropertySource("classpath:/META-INF/core/config/jdbc.properties"),
		@PropertySource("classpath:/META-INF/core/config/environment.properties") })
public class DataSourceConfig {

	@Autowired
	private Environment env;

	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		HikariConfig dataSourceConfig = new HikariConfig();
		dataSourceConfig.setDriverClassName(env.getRequiredProperty("db.driver"));
		dataSourceConfig.setJdbcUrl(env.getRequiredProperty("db.url"));
		dataSourceConfig.setUsername(env.getRequiredProperty("db.username"));
		dataSourceConfig.setPassword(env.getRequiredProperty("db.password"));

		return new HikariDataSource(dataSourceConfig);
	}
}