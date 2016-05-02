package com.akmans.trade.web.config;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

import com.akmans.trade.core.config.TradeCoreConfig;
import com.akmans.trade.web.config.csrf2conversationsupport.CSRFHandlerInterceptor;
import com.akmans.trade.web.config.csrf2conversationsupport.CustomRequestDataValueProcessor;
import com.akmans.trade.web.config.SpringSecurityConfig;
import com.akmans.trade.web.config.csrf2conversationsupport.ConversationalSessionAttributeStore;

@EnableWebMvc
@Configuration
@Import({ TradeCoreConfig.class, /*DataSourceConfig.class, JpaConfig.class, RepositoryConfig.class, */ThymeleafConfig.class, SpringSecurityConfig.class })
@ComponentScan(basePackages = { "com.akmans.trade.web.controller"/*, "com.akmans.trade.core.service.impl"*/ })
@EnableSpringDataWebSupport
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	private static final String MESSAGE_SOURCE1 = "/WEB-INF/i18n/messages";
	private static final String MESSAGE_SOURCE2 = "classpath:/META-INF/messages/ValidationMessages";
	private static final int KEEP_ALIVE_CONVERSATIONS = 10;

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/403").setViewName("error/403");
		registry.addViewController("/login").setViewName("user/login");
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
		registry.addInterceptor(csrfHandlerInterceptor());
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("language");
		return localeChangeInterceptor;
	}

	@Bean
	public CSRFHandlerInterceptor csrfHandlerInterceptor() {
		return new CSRFHandlerInterceptor();
	}

	@Bean
	public CookieLocaleResolver localeResolver() {
		CookieLocaleResolver localeResolver = new CookieLocaleResolver();
		Locale defaultLocale = new Locale("en");
		localeResolver.setDefaultLocale(defaultLocale);
		return localeResolver;
	}

	@Bean(name = "messageSource")
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames(MESSAGE_SOURCE1, MESSAGE_SOURCE2);
		// reload messages every 3600 seconds
		messageSource.setCacheSeconds(3600);
		return messageSource;
	}

	@Bean(name = "validationMessageSource")
	public ReloadableResourceBundleMessageSource validationMessageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename(MESSAGE_SOURCE2);
		// reload messages every 3600 seconds
		messageSource.setCacheSeconds(3600);
		return messageSource;
	}

	@Override
	public Validator getValidator() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.setValidationMessageSource((MessageSource) validationMessageSource());
		return validator;
	}

	@Bean(name = "conversationalSessionAttributeStore")
	public SessionAttributeStore conversationalSessionAttributeStore() {
		ConversationalSessionAttributeStore store = new ConversationalSessionAttributeStore();
		store.setKeepAliveConversations(KEEP_ALIVE_CONVERSATIONS);
		return store;
	}

	@Bean(name = "requestDataValueProcessor")
	public RequestDataValueProcessor csrfRequestDataValueProcessor() {
		CustomRequestDataValueProcessor processor = new CustomRequestDataValueProcessor();
		return processor;
	}
}