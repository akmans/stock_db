package com.akmans.trade.core.console.config;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.akmans.trade.core.Application;
import com.akmans.trade.core.config.TradeCoreConfig;
import com.akmans.trade.core.console.springbatch.CustomAsyncTaskExecutor;
import com.akmans.trade.core.enums.RunningMode;

@Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = { "com.akmans.trade.*.console.springbatch.execution",
		"com.akmans.trade.*.console.springbatch.listener", "com.akmans.trade.*.console.springbatch.processor",
		"com.akmans.trade.*.console.springbatch.writer", "com.akmans.trade.*.console.springbatch.runner",
		"com.akmans.trade.*.console.jobconfig",
		// new class path
		"com.akmans.trade.*.springbatch.execution",
		"com.akmans.trade.*.springbatch.listener", "com.akmans.trade.*.springbatch.processor",
		"com.akmans.trade.*.springbatch.writer", "com.akmans.trade.*.springbatch.runner",
		"com.akmans.trade.*.jobconfig"})
@Import({TradeCoreConfig.class })
@EnableScheduling
public class LauncherConfig {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JpaTransactionManager transactionManager;

	@Autowired
	private JobRegistry jobRegistry;

	@Autowired
	private JobExplorer jobExplorer;

	@Bean
	public JobRepository jobRepository() throws Exception {
		JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
		jobRepositoryFactoryBean.setDataSource(dataSource);
		jobRepositoryFactoryBean.setTransactionManager(transactionManager);
		jobRepositoryFactoryBean.setDatabaseType("POSTGRES");
		return jobRepositoryFactoryBean.getObject();
	}

	@Bean
	public SimpleJobLauncher jobLauncher(Application application) throws Exception {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository());
		if (RunningMode.WEB == application.getRunningMode()) {
			jobLauncher.setTaskExecutor(new CustomAsyncTaskExecutor());
		}
		return jobLauncher;
	}

	@Bean
	public SimpleJobOperator jobOperator(SimpleJobLauncher jobLauncher) throws Exception {
		SimpleJobOperator jobOperator = new SimpleJobOperator();
		jobOperator.setJobLauncher(jobLauncher);
		jobOperator.setJobRepository(jobRepository());
		jobOperator.setJobRegistry(jobRegistry);
		jobOperator.setJobExplorer(jobExplorer);
		return jobOperator;
	}
}