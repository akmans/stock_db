package com.akmans.trade.core.web.config;

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
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

import com.akmans.trade.core.Application;
import com.akmans.trade.core.enums.RunningMode;
import com.akmans.trade.core.spring.BaseReloadableResourceBundleMessageSource;
import com.akmans.trade.core.config.LauncherConfig;
import com.akmans.trade.core.web.config.csrf2conversationsupport.CSRFHandlerInterceptor;
import com.akmans.trade.core.web.config.csrf2conversationsupport.CustomRequestDataValueProcessor;
import com.akmans.trade.core.web.config.SpringSecurityConfig;
import com.akmans.trade.core.web.config.csrf2conversationsupport.ConversationalSessionAttributeStore;
import com.akmans.trade.core.web.utils.PathConstants;
import com.akmans.trade.core.web.utils.ViewConstants;

@EnableWebMvc
@Configuration
@Import({ ThymeleafConfig.class, SpringSecurityConfig.class, LauncherConfig.class })
@ComponentScan(basePackages = { "com.akmans.trade.*.web.controller" })
@EnableSpringDataWebSupport
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	private static final int KEEP_ALIVE_CONVERSATIONS = 10;

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController(PathConstants.PATH_ACCESS_DENIED).setViewName(ViewConstants.VIEW_ACCESS_DENIED);
		registry.addViewController(PathConstants.PATH_LOGIN).setViewName(ViewConstants.VIEW_LOGIN);
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

	@Bean(name = "validationMessageSource")
	public ReloadableResourceBundleMessageSource validationMessageSource() {
		return new BaseReloadableResourceBundleMessageSource();
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

	@Bean
	public Application application() {
		Application application = new Application();
		application.setRunningMode(RunningMode.WEB);
		return application;
	}
}