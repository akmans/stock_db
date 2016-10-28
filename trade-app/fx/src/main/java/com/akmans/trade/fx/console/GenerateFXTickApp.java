package com.akmans.trade.fx.console;

import java.util.UUID;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.akmans.trade.core.console.config.StandaloneConfig;
import com.akmans.trade.core.enums.FXJob;
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

//		String jobId = FXJob.GENERATE_FX_TICK_JOB.getValue();
		FXDDEUtil ddeUtil = (FXDDEUtil) context.getBean(FXDDEUtil.class);
//		Job job = (Job) context.getBean(jobId);

		try {
//			JobParameters params = new JobParametersBuilder().addString("id", UUID.randomUUID().toString())
//					.toJobParameters();
//			JobExecution execution = jobLauncher.run(job, params);
//			logger.info("Exit Status : " + execution.getStatus());
			ddeUtil.execute();
		} catch (Exception e) {
			logger.error("Error occurred!", e);
		}

		context.close();

		System.out.println("Done");
	}
}
