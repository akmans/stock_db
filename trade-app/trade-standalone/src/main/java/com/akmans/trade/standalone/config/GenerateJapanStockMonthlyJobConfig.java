package com.akmans.trade.standalone.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.akmans.trade.core.enums.JapanStockJob;
import com.akmans.trade.standalone.springbatch.execution.JapanStockMonthlyGenerateExecution;
import com.akmans.trade.standalone.springbatch.listeners.JapanStockJobExecutionListener;

@Configuration
public class GenerateJapanStockMonthlyJobConfig {

	@Bean
	public Job generateJapanStockMonthlyJob(JobBuilderFactory jobs, StepBuilderFactory stepBuilderFactory,
			JapanStockMonthlyGenerateExecution generateExecution, JapanStockJobExecutionListener listener) {
		Step step = stepBuilderFactory.get("step").tasklet(generateExecution).build();
		return jobs.get(JapanStockJob.GENERATE_JAPAN_STOCK_MONTHLY_JOB.getValue()).start(step).listener(listener)
				.build();
	}
}
