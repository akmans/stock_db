package com.akmans.trade.fx.console;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.akmans.trade.core.enums.FXJob;
import com.akmans.trade.core.console.config.StandaloneConfig;

public class GenerateCandlestickDataApp {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(GenerateCandlestickDataApp.class);

	public static void main(String[] args) throws Exception {

		if (args == null || args.length != 2) {
			throw new Exception("Two parameters are needed.");
		}

		// Initialize application context
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		// Enable a "development" profile
		context.getEnvironment().setActiveProfiles("production");
		context.register(StandaloneConfig.class);
		context.refresh();

		String jobId = FXJob.GENERATE_FX_CANDLESTICK_DATA_JOB.getValue();
		JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
		Job job = (Job) context.getBean(jobId);
		logger.info("Job Parameters : {}", (Object[])args);

		try {
			JobParameters params = new JobParametersBuilder().addString("jobId", jobId)
					.addString("currencyPair", args[0].toLowerCase()).addString("processedMonth", args[1])
					.toJobParameters();
			JobExecution execution = jobLauncher.run(job, params);
			logger.info("Exit Status : " + execution.getStatus());

		} catch (Exception e) {
			logger.error("Job Failed", e);
			System.exit(1);
		}

		context.close();

		logger.info("Done!");
		System.exit(0);
	}
}
