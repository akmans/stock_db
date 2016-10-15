package com.akmans.trade.fx.console;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.akmans.trade.fx.springbatch.runner.TrueFXRunner;
import com.akmans.trade.core.console.config.StandaloneConfig;

public class GenerateFXTickApp {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(GenerateFXTickApp.class);

	public static void main(String[] args) throws Exception {
		// Initialize application context
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		// Enable a "development" profile
		context.getEnvironment().setActiveProfiles("production");
		context.register(StandaloneConfig.class);
		context.refresh();

		try {
			TrueFXRunner runner = (TrueFXRunner) context.getBean("trueFXRunner");
			runner.execute();
		} catch (Exception e) {
			logger.error("Error occurred!", e);
		}

		context.close();

		System.out.println("Done");
	}
}
