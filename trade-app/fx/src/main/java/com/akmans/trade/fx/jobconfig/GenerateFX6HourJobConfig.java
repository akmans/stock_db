package com.akmans.trade.fx.jobconfig;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.akmans.trade.core.enums.FXJob;
import com.akmans.trade.fx.springbatch.execution.FX6HourGenerateExecution;
import com.akmans.trade.fx.springbatch.listener.FXJobExecutionListener;

@Configuration
public class GenerateFX6HourJobConfig {

	@Bean
	public Job generateJapanStockWeeklyJob(JobBuilderFactory jobs, StepBuilderFactory stepBuilderFactory,
			FX6HourGenerateExecution generateExecution, FXJobExecutionListener listener) {
		Step step = stepBuilderFactory.get("step").tasklet(generateExecution).build();
		return jobs.get(FXJob.GENERATE_FX_6HOUR_JOB.getValue()).start(step).listener(listener).build();
	}
}
