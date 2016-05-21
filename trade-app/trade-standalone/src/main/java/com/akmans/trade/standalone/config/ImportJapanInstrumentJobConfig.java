package com.akmans.trade.standalone.config;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;

import com.akmans.trade.core.utils.FileUtil;
import com.akmans.trade.core.utils.MailUtil;
import com.akmans.trade.standalone.springbatch.execution.JapanInstrumentImportExecution;

public class ImportJapanInstrumentJobConfig {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ImportJapanInstrumentJobConfig.class);

	@Bean
	public Job importJapanInstrumentJob(JobBuilderFactory jobs, StepBuilderFactory stepBuilderFactory, JapanInstrumentImportExecution execution) {
		Step step1 = stepBuilderFactory.get("step1").tasklet(new Step1Execution()).build();
		Step step2 = stepBuilderFactory.get("step2").tasklet(execution).build();
		Step step3 = stepBuilderFactory.get("step3").tasklet(new Step3Execution()).build();
		return jobs.get("importJapanInstrumentJob").start(step1).next(step2).next(step3).build();
	}

	private class Step1Execution implements Tasklet {
		public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
			String sourceUrl = "http://www.jpx.co.jp/markets/statistics-equities/misc/tvdivq0000001vg2-att/data_j.xls";
			String targetDirectory = "/Users/owner99/Desktop/FX";
			FileUtil.download(sourceUrl, targetDirectory);
			return RepeatStatus.FINISHED;
		}
	}

	private class Step3Execution implements Tasklet {
		public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
			MailUtil.sendMail();
			return RepeatStatus.FINISHED;
		}
	}
}
