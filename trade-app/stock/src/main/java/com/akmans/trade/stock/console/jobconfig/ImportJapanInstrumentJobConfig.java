package com.akmans.trade.stock.console.jobconfig;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.akmans.trade.core.enums.JapanStockJob;
import com.akmans.trade.stock.console.springbatch.execution.JapanInstrumentDownloadExecution;
import com.akmans.trade.stock.console.springbatch.execution.JapanInstrumentImportExecution;
import com.akmans.trade.stock.console.springbatch.listener.JapanStockJobExecutionListener;

@Configuration
public class ImportJapanInstrumentJobConfig {

	@Bean
	public Job importJapanInstrumentJob(JobBuilderFactory jobs, StepBuilderFactory stepBuilderFactory,
			JapanInstrumentImportExecution importExecution, JapanInstrumentDownloadExecution downloadExecution,
			JapanStockJobExecutionListener listener) {
		Step step1 = stepBuilderFactory.get("step1").tasklet(downloadExecution).build();
		Step step2 = stepBuilderFactory.get("step2").tasklet(importExecution).build();
		return jobs.get(JapanStockJob.IMPORT_JAPAN_INSTRUMENT_JOB.getValue()).start(step1).next(step2)
				.listener(listener).build();
	}
}
