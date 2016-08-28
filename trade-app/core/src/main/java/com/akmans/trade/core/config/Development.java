package com.akmans.trade.core.config;

import java.util.Collections;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.akmans.trade.core.Constants;
import com.akmans.trade.core.springdata.jpa.config.RepositoryConfig;

@Configuration
@Profile("development")
@PropertySources({ @PropertySource(Constants.JDBC_PROPERITES_FILE_PATH),
		@PropertySource(Constants.DEVELOPMENT_PROPERITES_FILE_PATH),
		@PropertySource(Constants.HIBERNATE_PROPERITES_FILE_PATH) })
@Import({ AopConfiguration.class, RepositoryConfig.class })
public class Development {

	@Autowired
	private Environment env;

	@Bean//(destroyMethod = "close")
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
		hibernateJpa.setDatabasePlatform(env.getProperty("hibernate.dialect"));
		hibernateJpa.setShowSql(env.getProperty("hibernate.show_sql", Boolean.class));

		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(dataSource());
		emf.setPackagesToScan("com.akmans.trade.*.springdata.jpa.entities", "com.akmans.trade.*.springdata.jpa.keys");
		emf.setJpaVendorAdapter(hibernateJpa);
		emf.setJpaPropertyMap(Collections.singletonMap("javax.persistence.validation.mode", "none"));
		return emf;
	}

	@Bean
	public JpaTransactionManager transactionManager() {
		JpaTransactionManager txnMgr = new JpaTransactionManager();
		txnMgr.setEntityManagerFactory(entityManagerFactory().getObject());
		return txnMgr;
	}
}