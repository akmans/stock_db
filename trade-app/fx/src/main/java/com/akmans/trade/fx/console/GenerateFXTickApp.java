package com.akmans.trade.fx.console;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.akmans.trade.core.console.config.StandaloneConfig;
import com.akmans.trade.fx.utils.FXDDEUtil;

public class GenerateFXTickApp {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(GenerateFXTickApp.class);

	public static void main(String[] args) throws Exception {
		// Initialize application context
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		// Enable a "development" profile
		context.getEnvironment().setActiveProfiles("production");
		context.register(StandaloneConfig.class);
		context.refresh();

		FXDDEUtil ddeUtil = (FXDDEUtil) context.getBean(FXDDEUtil.class);

		try {
			ddeUtil.execute();
		} catch (Exception e) {
			logger.error("Error occurred!", e);
		}

		context.close();

		System.out.println("Done");
	}
}
