package com.akmans.trade.core.springdata.auditing;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import com.akmans.trade.core.Application;
import com.akmans.trade.core.enums.RunningMode;

public class UsernameAuditorAware implements AuditorAware<String> {
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(UsernameAuditorAware.class);

	@Autowired
	private Application application;

	public String getCurrentAuditor() {
		// In standalone environment.
		if (RunningMode.STANDALONE == application.getRunningMode()) {
			return "system";
		}

		// In web environment.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			logger.debug("Not authentication.");
			return null;
		}

		logger.debug("Username is " + ((User) authentication.getPrincipal()).getUsername());

		return ((User) authentication.getPrincipal()).getUsername();
	}
}
