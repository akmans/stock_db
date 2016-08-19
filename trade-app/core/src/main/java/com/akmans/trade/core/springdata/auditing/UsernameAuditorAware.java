package com.akmans.trade.core.springdata.auditing;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

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

		String auditor = null;
		// In web environment.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			logger.debug("Not authentication.");
			return null;
		}

		Object principal = authentication.getPrincipal();
		if (principal instanceof UserDetails) {
			auditor = ((UserDetails) principal).getUsername();
		} else {
			auditor = principal.toString();
		}
		logger.debug("Username is " + auditor);

		return auditor;
	}
}
