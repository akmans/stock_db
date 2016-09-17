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

public class GenerateMonthApp {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(GenerateMonthApp.class);

	public static void main(String[] args) throws Exception {

		if (args == null || args.length != 2) {
			throw new Exception("Two parameters are needed.");
		}

		// Initialize application context
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		// Enable a "development" profile
		context.getEnvironment().setActiveProfiles("development");
		context.register(StandaloneConfig.class);
		context.refresh();

		String jobId = FXJob.GENERATE_FX_MONTH_JOB.getValue();
		JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
		Job job = (Job) context.getBean(jobId);
		logger.info("Job Restartable ? : " + job.isRestartable());

		try {
			JobParameters params = new JobParametersBuilder().addString("jobId", jobId)
					.addString("currencyPair", args[0]).addString("processedMonth", args[1]).toJobParameters();
			JobExecution execution = jobLauncher.run(job, params);
			logger.info("Exit Status : " + execution.getStatus());

		} catch (Exception e) {
			e.printStackTrace();
		}

		context.close();

		System.out.println("Done");

	}
}
