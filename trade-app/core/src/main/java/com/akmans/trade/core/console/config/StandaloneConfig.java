package com.akmans.trade.core.console.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.akmans.trade.core.Application;
import com.akmans.trade.core.config.LauncherConfig;
import com.akmans.trade.core.enums.RunningMode;

@Configuration
// @EnableAutoConfiguration
@Import({ LauncherConfig.class})
public class StandaloneConfig {

	@Bean
	public Application application() {
		Application application = new Application();
		application.setRunningMode(RunningMode.STANDALONE);
		// TODO new locale
		Locale.setDefault(Locale.ENGLISH);
		return application;
	}
}