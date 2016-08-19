package com.akmans.trade.stock.console;

import java.util.Date;
import java.util.UUID;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.akmans.trade.core.enums.JapanStockJob;
import com.akmans.trade.core.console.config.StandaloneConfig;

public class ImportJapanStockApp {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ImportJapanStockApp.class);

	public static void main(String[] args) {

		// Initialize application context
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(StandaloneConfig.class);
		context.refresh();

		String jobId = JapanStockJob.IMPORT_JAPAN_STOCK_JOB.getValue();
		Date applicationDate = new Date();
		JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
		Job job = (Job) context.getBean(jobId);
		logger.info("Job Restartable ? : " + job.isRestartable());

		try {
			JobParameters params = new JobParametersBuilder().addString("jobId", jobId).addDate("processDate", applicationDate)
					.addString("uuid", UUID.randomUUID().toString()).toJobParameters();
			JobExecution execution = jobLauncher.run(job, params);
			logger.info("Exit Status : " + execution.getStatus());

		} catch (Exception e) {
			e.printStackTrace();
		}

		context.close();

		System.out.println("Done");

	}
}
