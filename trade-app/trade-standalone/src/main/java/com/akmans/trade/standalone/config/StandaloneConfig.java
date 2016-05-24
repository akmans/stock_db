package com.akmans.trade.standalone.config;

import java.util.Locale;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.orm.jpa.JpaTransactionManager;

import com.akmans.trade.core.Application;
import com.akmans.trade.core.config.TradeCoreConfig;
import com.akmans.trade.core.enums.RunningMode;

@Configuration
// @EnableAutoConfiguration
@Import({// TradeCoreConfig.class,
	LauncherConfig.class,
		// ImportJapanStockJobConfig.class,
		ImportJapanInstrumentJobConfig.class })
public class StandaloneConfig {

	private static final String MESSAGE_SOURCE1 = "classpath:/META-INF/core/i18n/messages";

	@Bean(name = "messageSource")
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename(MESSAGE_SOURCE1);
		// reload messages every 3600 seconds
		messageSource.setCacheSeconds(3600);
		return messageSource;
	}

	@Bean
	public Application application() {
		Application application = new Application();
		application.setRunningMode(RunningMode.STANDALONE);
		// TODO new locale
		Locale.setDefault(Locale.ENGLISH);
		return application;
	}
}