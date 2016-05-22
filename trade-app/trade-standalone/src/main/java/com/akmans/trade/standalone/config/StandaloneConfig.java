package com.akmans.trade.standalone.config;

import java.util.Locale;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.JpaTransactionManager;

import com.akmans.trade.core.Application;
import com.akmans.trade.core.config.TradeCoreConfig;
import com.akmans.trade.core.enums.RunningMode;

@Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = { "com.akmans.trade.standalone.springbatch.execution" })
//@EnableAutoConfiguration
@Import({
	TradeCoreConfig.class,
//	ImportJapanStockJobConfig.class,
	ImportJapanInstrumentJobConfig.class
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

	@Bean
	public Application application() {
		Application application = new Application();
		application.setRunningMode(RunningMode.STANDALONE);
		//TODO new locale
		Locale.setDefault(Locale.ENGLISH);
		return application;
	}
}