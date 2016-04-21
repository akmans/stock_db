package com.akmans.trade.standalone.config;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.JpaTransactionManager;

import com.akmans.trade.core.springdata.jpa.config.DataSourceConfig;
import com.akmans.trade.core.springdata.jpa.config.JpaConfig;
import com.akmans.trade.core.springdata.jpa.config.RepositoryConfig;

@Configuration
@EnableBatchProcessing
//@EnableAutoConfiguration
@Import({
	DataSourceConfig.class,
	JpaConfig.class,
	RepositoryConfig.class,
	HelloWorldJobConfig.class
	})
public class StandaloneConfig {
	@Autowired
	private DataSource dataSource;

	@Autowired
	private JpaTransactionManager transactionManager;

	@Bean
	public JobRepository jobRepository() throws Exception {
		JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
		jobRepositoryFactoryBean.setDataSource(dataSource);
		jobRepositoryFactoryBean.setTransactionManager(transactionManager);
		jobRepositoryFactoryBean.setDatabaseType("POSTGRES");
		return jobRepositoryFactoryBean.getObject();
	}

	@Bean
	public SimpleJobLauncher jobLauncher() throws Exception {
		SimpleJobLauncher jobLauncher= new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository());
		return jobLauncher;
	}
}