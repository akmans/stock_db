package com.akmans.trade.standalone.console;

import java.util.Date;
import java.util.UUID;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.akmans.trade.standalone.config.StandaloneConfig;

public class ImportJapanInstrumentApp {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ImportJapanInstrumentApp.class);

	public static void main(String[] args) {

		// Initialize application context
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(StandaloneConfig.class);
		context.refresh();

		JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
		Job job = (Job) context.getBean("importJapanInstrumentJob");
		logger.info("Job Restartable ? : " + job.isRestartable());

		try {
			JobParameters params = new JobParametersBuilder().addString("jobId", "importJapanInstrumentJob")
					.addDate("processDate", new Date()).toJobParameters();
			JobExecution execution = jobLauncher.run(job, params);
			logger.info("Exit Status : " + execution.getStatus());

		} catch (Exception e) {
			e.printStackTrace();
		}

		context.close();

		System.out.println("Done");

	}
}
