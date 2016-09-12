package com.akmans.trade.fx.jobconfig;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.akmans.trade.core.enums.FXJob;
import com.akmans.trade.fx.springbatch.execution.FXDayGenerateExecution;
import com.akmans.trade.fx.springbatch.listener.FXJobExecutionListener;

@Configuration
public class GenerateFXDayJobConfig {

	@Bean
	public Job generateFXDayJob(JobBuilderFactory jobs, StepBuilderFactory stepBuilderFactory,
			FXDayGenerateExecution generateExecution, FXJobExecutionListener listener) {
		Step step = stepBuilderFactory.get("step").tasklet(generateExecution).build();
		return jobs.get(FXJob.GENERATE_FX_DAY_JOB.getValue()).start(step).listener(listener)
				.build();
	}
}
