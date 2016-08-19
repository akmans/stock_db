package com.akmans.trade.core.config;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.akmans.trade.core.Application;
import com.akmans.trade.core.enums.RunningMode;

@Configuration
// @EnableAutoConfiguration
@Import({ TradeCoreConfig.class})
public class TestCoreConfig {

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