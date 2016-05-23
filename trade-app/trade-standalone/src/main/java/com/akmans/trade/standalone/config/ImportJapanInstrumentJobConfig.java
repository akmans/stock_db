package com.akmans.trade.standalone.config;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.akmans.trade.core.utils.FileUtil;
import com.akmans.trade.core.utils.MailUtil;
import com.akmans.trade.standalone.springbatch.execution.JapanInstrumentDownloadExecution;
import com.akmans.trade.standalone.springbatch.execution.JapanInstrumentImportExecution;

@Configuration
public class ImportJapanInstrumentJobConfig {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ImportJapanInstrumentJobConfig.class);

	@Autowired
	private MailUtil mailUtil;

	@Bean
	public Job importJapanInstrumentJob(JobBuilderFactory jobs, StepBuilderFactory stepBuilderFactory,
			JapanInstrumentImportExecution importExecution, JapanInstrumentDownloadExecution downloadExecution) {
		Step step1 = stepBuilderFactory.get("step1").tasklet(downloadExecution).build();
		Step step2 = stepBuilderFactory.get("step2").tasklet(importExecution).build();
		Step step3 = stepBuilderFactory.get("step3").tasklet(new Step3Execution()).build();
		return jobs.get("importJapanInstrumentJob").start(step1).next(step2).next(step3).build();
		// return
		// jobs.get("importJapanInstrumentJob").start(step2).next(step3).build();
	}
/*
	private class Step1Execution extends StepExecutionListenerSupport implements Tasklet {
		private String applicationDate;

		public void beforeStep(StepExecution stepExecution) {
			JobParameters jobParameters = stepExecution.getJobParameters();
			applicationDate = jobParameters.getString("applicationDate");
			logger.debug("The applicationDate is {}", applicationDate);
		}

		public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
			String sourceUrl = env.getRequiredProperty("japan.instrument.url");
			String targetDirectory = env.getRequiredProperty("standalone.work.dir");
			String targetFile = targetDirectory + "/Instruments_" + applicationDate + ".xls";
			fileUtil.download(sourceUrl, targetFile);
			logger.debug("Downloaded file is {}", targetFile);
			chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("targetFile",
					targetFile);
			return RepeatStatus.FINISHED;
		}
	}*/

	private class Step3Execution implements Tasklet {
		public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
			mailUtil.sendMail();
			return RepeatStatus.FINISHED;
		}
	}
}
