package com.akmans.trade.core.config;

import java.beans.PropertyVetoException;
import java.util.Collections;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.akmans.trade.core.Constants;
import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@Profile("production")
@PropertySources({ @PropertySource(Constants.SYSTEM_PROPERITES_FILE_PATH)})
public class Production {

	@Autowired
	private Environment env;

	@Bean
	public DataSource dataSource() throws PropertyVetoException {
//		DriverManagerDataSource dataSourceConfig = new DriverManagerDataSource();
//		dataSourceConfig.setDriverClassName(env.getRequiredProperty("db.driver"));
//		dataSourceConfig.setUrl(env.getRequiredProperty("db.url"));
//		dataSourceConfig.setUsername(env.getRequiredProperty("db.username"));
//		dataSourceConfig.setPassword(env.getRequiredProperty("db.password"));
		ComboPooledDataSource dataSourceConfig = new ComboPooledDataSource();
		dataSourceConfig.setDriverClass(env.getRequiredProperty("db.driver"));
		dataSourceConfig.setJdbcUrl(env.getRequiredProperty("db.url"));
		dataSourceConfig.setUser(env.getRequiredProperty("db.username"));
		dataSourceConfig.setPassword(env.getRequiredProperty("db.password"));

        // the settings below are optional -- c3p0 can work with defaults
		dataSourceConfig.setMinPoolSize(5);
		dataSourceConfig.setAcquireIncrement(5);
		dataSourceConfig.setMaxPoolSize(20);
		dataSourceConfig.setMaxStatements(180);

		return dataSourceConfig;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws PropertyVetoException {

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
	public JpaTransactionManager transactionManager() throws PropertyVetoException {
		JpaTransactionManager txnMgr = new JpaTransactionManager();
		txnMgr.setEntityManagerFactory(entityManagerFactory().getObject());
		return txnMgr;
	}
}