package com.akmans.trade.web.config;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.support.SessionAttributeStore;
//import org.springframework.context.support.ReloadableResourceBundleMessageSource;
//import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

import com.akmans.trade.core.springdata.jpa.config.DataSourceConfig;
import com.akmans.trade.core.springdata.jpa.config.JpaConfig;
import com.akmans.trade.core.springdata.jpa.config.RepositoryConfig;
import com.akmans.trade.web.config.csrf2conversationsupport.CSRFHandlerInterceptor;
import com.akmans.trade.web.config.csrf2conversationsupport.CustomRequestDataValueProcessor;
import com.akmans.trade.web.config.security.SpringSecurityConfig;
import com.akmans.trade.web.config.csrf2conversationsupport.ConversationalSessionAttributeStore;

@EnableWebMvc
@Configuration
@Import({ DataSourceConfig.class, JpaConfig.class, RepositoryConfig.class, ThymeleafConfig.class,
		SpringSecurityConfig.class })
@ComponentScan(basePackages = { "com.akmans.trade.web.controller" })
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	private static final String MESSAGE_SOURCE1 = "/WEB-INF/i18n/messages";
	private static final String MESSAGE_SOURCE2 = "classpath:/META-INF/messages/ValidationMessages";
	private static final int KEEP_ALIVE_CONVERSATIONS = 10;

	// @Override
	// public void addResourceHandlers(ResourceHandlerRegistry registry) {
	// registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	// }

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

	// @Bean(name = "multipartResolver")
	// public CommonsMultipartResolver getMultipartResolver() {
	// return new CommonsMultipartResolver();
	// }

	@Bean(name = "messageSource")
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename(MESSAGE_SOURCE1);
		// reload messages every 10 seconds
		messageSource.setCacheSeconds(10);
		return messageSource;
	}

	@Bean(name = "validationMessageSource")
	public ReloadableResourceBundleMessageSource validationMessageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename(MESSAGE_SOURCE2);
		// reload messages every 10 seconds
		messageSource.setCacheSeconds(10);
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