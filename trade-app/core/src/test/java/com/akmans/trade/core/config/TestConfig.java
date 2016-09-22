package com.akmans.trade.core.config;

import java.util.Collections;
import java.util.Locale;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.ActiveProfiles;

import com.akmans.trade.core.Application;
import com.akmans.trade.core.console.config.LauncherConfig;
import com.akmans.trade.core.enums.RunningMode;

@Configuration
@ActiveProfiles("test")
@PropertySource("classpath:/META-INF/config/test-config.properties")
@Import({ LauncherConfig.class })
public class TestConfig {

	@Autowired
	private Environment env;

	@Bean
	public Application application() {
		Application application = new Application();
		application.setRunningMode(RunningMode.STANDALONE);
		// TODO new locale
		Locale.setDefault(Locale.ENGLISH);
		return application;
	}

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSourceConfig = new DriverManagerDataSource();
		dataSourceConfig.setDriverClassName(env.getRequiredProperty("db.driver"));
		dataSourceConfig.setUrl(env.getRequiredProperty("db.url"));
		dataSourceConfig.setUsername(env.getRequiredProperty("db.username"));
		dataSourceConfig.setPassword(env.getRequiredProperty("db.password"));

		return dataSourceConfig;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

		HibernateJpaVendorAdapter hibernateJpa = new HibernateJpaVendorAdapter();

		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(dataSource());
		emf.setPackagesToScan("com.akmans.trade.*.springdata.jpa.entities", "com.akmans.trade.*.springdata.jpa.keys");
		emf.setJpaVendorAdapter(hibernateJpa);
		emf.setJpaPropertyMap(Collections.singletonMap("javax.persistence.validation.mode", "none"));
		// JPA properties.
		Properties jpaProperties = new Properties();
	    jpaProperties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
	    jpaProperties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql", Boolean.class));
	    jpaProperties.put("hibernate.generate_statistics", env.getProperty("hibernate.generate_statistics", Boolean.class));
	    jpaProperties.put("hibernate.format_sql", env.getProperty("hibernate.format_sql", Boolean.class));
	    jpaProperties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
		emf.setJpaProperties(jpaProperties);
		emf.afterPropertiesSet();
		return emf;
	}

	@Bean
	public JpaTransactionManager transactionManager() {
		JpaTransactionManager txnMgr = new JpaTransactionManager();
		txnMgr.setEntityManagerFactory(entityManagerFactory().getObject());
		return txnMgr;
	}
}