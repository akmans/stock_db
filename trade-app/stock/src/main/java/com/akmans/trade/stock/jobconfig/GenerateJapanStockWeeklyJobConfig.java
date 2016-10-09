package com.akmans.trade.stock.jobconfig;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.akmans.trade.core.enums.JapanStockJob;
import com.akmans.trade.stock.springbatch.execution.JapanStockWeeklyGenerateExecution;
import com.akmans.trade.stock.springbatch.listener.JapanStockJobExecutionListener;

@Configuration
public class GenerateJapanStockWeeklyJobConfig {

	@Bean
	public Job generateJapanStockWeeklyJob(JobBuilderFactory jobs, StepBuilderFactory stepBuilderFactory,
			JapanStockWeeklyGenerateExecution generateExecution, JapanStockJobExecutionListener listener) {
		Step step = stepBuilderFactory.get("step").tasklet(generateExecution).build();
		return jobs.get(JapanStockJob.GENERATE_JAPAN_STOCK_WEEKLY_JOB.getValue()).start(step).listener(listener)
				.build();
	}
}
