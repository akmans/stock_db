package com.akmans.trade.fx.jobconfig;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.akmans.trade.core.enums.FXJob;
import com.akmans.trade.fx.springbatch.execution.FX6HourGenerateExecution;
import com.akmans.trade.fx.springbatch.execution.FXDayGenerateExecution;
import com.akmans.trade.fx.springbatch.execution.FXHourGenerateExecution;
import com.akmans.trade.fx.springbatch.execution.FXMonthGenerateExecution;
import com.akmans.trade.fx.springbatch.execution.FXWeekGenerateExecution;
import com.akmans.trade.fx.springbatch.listener.FXJobExecutionListener;

@Configuration
public class GenerateFXCandlestickDataJobConfig {

	@Bean
	public Job generateFXCandlestickDataJob(JobBuilderFactory jobs, StepBuilderFactory stepBuilderFactory,
			FXHourGenerateExecution generateHourExecution, FX6HourGenerateExecution generate6HourExecution,
			FXDayGenerateExecution generateDayExecution, FXWeekGenerateExecution generateWeekExecution,
			FXMonthGenerateExecution generateMonthExecution, FXJobExecutionListener listener) {
		Step step1 = stepBuilderFactory.get("step1").tasklet(generateHourExecution).build();
		Step step2 = stepBuilderFactory.get("step2").tasklet(generate6HourExecution).build();
		Step step3 = stepBuilderFactory.get("step3").tasklet(generateDayExecution).build();
		Step step4 = stepBuilderFactory.get("step4").tasklet(generateWeekExecution).build();
		Step step5 = stepBuilderFactory.get("step5").tasklet(generateMonthExecution).build();
		return jobs.get(FXJob.GENERATE_FX_WEEK_JOB.getValue()).start(step1).next(step2).next(step3).next(step4)
				.next(step5).listener(listener).build();
	}
}
