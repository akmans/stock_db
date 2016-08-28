package com.akmans.trade.core.config;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import com.akmans.trade.core.spring.BaseReloadableResourceBundleMessageSource;
import com.akmans.trade.core.springdata.jpa.config.RepositoryConfig;

@Configuration
@ComponentScan(basePackages = { "com.akmans.trade.*.service.impl", "com.akmans.trade.core.utils" })
@Import({ Production.class, Development.class, AopConfiguration.class, RepositoryConfig.class })
public class TradeCoreConfig {

	@Bean(name = "messageSource")
	public MessageSource messageSource() {
		return new BaseReloadableResourceBundleMessageSource();
	}

	@Bean
	public CookieLocaleResolver localeResolver() {
		CookieLocaleResolver localeResolver = new CookieLocaleResolver();
		Locale defaultLocale = new Locale("en");
		localeResolver.setDefaultLocale(defaultLocale);
		return localeResolver;
	}
}
