package com.akmans.trade.core.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import com.akmans.trade.core.springdata.jpa.config.DataSourceConfig;
import com.akmans.trade.core.springdata.jpa.config.JpaConfig;
import com.akmans.trade.core.springdata.jpa.config.RepositoryConfig;

@Configuration
@ComponentScan(basePackages = { "com.akmans.trade.core.service.impl" })
@Import({DataSourceConfig.class, JpaConfig.class, RepositoryConfig.class})
public class TradeCoreConfig {

	@Bean
	public CookieLocaleResolver localeResolver() {
		CookieLocaleResolver localeResolver = new CookieLocaleResolver();
		Locale defaultLocale = new Locale("en");
		localeResolver.setDefaultLocale(defaultLocale);
		return localeResolver;
	}
}
