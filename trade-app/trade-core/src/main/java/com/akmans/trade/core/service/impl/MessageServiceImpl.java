package com.akmans.trade.core.service.impl;

import java.util.Locale;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.akmans.trade.core.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

	@Autowired
	private MessageSource messageSource;

	public String getMessage(String key, Object... objects) {
		try {
			Locale locale = LocaleContextHolder.getLocale();
			String message = messageSource.getMessage(key, objects, locale);
			logger.debug("The content of key [" + key + "] is [" + message + "]");
			return message;
		} catch (Exception e) {
			return "Unresolved key: " + key;
		}

	}
}
