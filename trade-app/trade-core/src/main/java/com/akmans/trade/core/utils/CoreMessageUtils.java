package com.akmans.trade.core.utils;

import java.util.Locale;

import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

public class CoreMessageUtils {
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(CoreMessageUtils.class);

	public static String getMessage(String key, Object... objects) {
		try {
			ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
			messageSource.setBasename("classpath:/META-INF/core/i18n/messages");
			messageSource.setFallbackToSystemLocale(false);
			Locale locale = LocaleContextHolder.getLocale();
			String message = messageSource.getMessage(key, objects, locale);
			logger.debug("The content of key [" + key + "] is [" + message + "]");
			return message;
		} catch (Exception e) {
			e.printStackTrace();
			return "Unresolved key: " + key;
		}
	}
}
