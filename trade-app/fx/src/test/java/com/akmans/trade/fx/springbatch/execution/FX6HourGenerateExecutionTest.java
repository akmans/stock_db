package com.akmans.trade.fx.springbatch.execution;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.time.ZonedDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.akmans.trade.core.Constants;
import com.akmans.trade.core.config.TestConfig;
import com.akmans.trade.core.enums.FXJob;
import com.akmans.trade.core.enums.FXType;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.fx.service.FX6HourService;
import com.akmans.trade.fx.service.FXHourService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFX6Hour;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		StepScopeTestExecutionListener.class })
public class FX6HourGenerateExecutionTest {

	@Autowired
	private FX6HourGenerateExecution execution;

	@Test
	public void testBeforeStep() {
		StepExecution stepExecution = new StepExecution("Test Step",
				new JobExecution(new JobInstance(1L, "Test Job"), new JobParameters(), "Test Configuration"));
		execution.beforeStep(stepExecution);
		assertEquals(0, stepExecution.getJobExecution().getExecutionContext().getInt(Constants.INSERTED_ROWS + "6Hour"));
	}

	/** Test no data. */
	@Test
	public void testStepExecutionWithMock() throws Exception {
		FXHourService mockHourService = Mockito.mock(FXHourService.class);
		FX6HourService mock6HourService = Mockito.mock(FX6HourService.class);
		FX6HourGenerateExecution execution = new FX6HourGenerateExecution(mockHourService, mock6HourService);
		// New job parameters.
		JobParameters params = new JobParametersBuilder().addString("jobId", FXJob.GENERATE_FX_6HOUR_JOB.getValue())
				.addString("currencyPair", "usdjpy").addString("processedMonth", "200905").toJobParameters();
		// New step execution.
		StepExecution stepExecution = new StepExecution("Test Step",
				new JobExecution(new JobInstance(1L, "Test Job"), params, "Test Configuration"));
		execution.beforeStep(stepExecution);
		RepeatStatus status = execution.execute(stepExecution.createStepContribution(),
				new ChunkContext(new StepContext(stepExecution)));
		assertEquals(RepeatStatus.FINISHED, status);
		assertEquals(0, stepExecution.getJobExecution().getExecutionContext().getInt(Constants.INSERTED_ROWS + "6Hour"));
		assertEquals(0, stepExecution.getJobExecution().getExecutionContext().getInt(Constants.UPDATED_ROWS + "6Hour"));
		// Verify
		verify(mockHourService, times(4 * 31)).generateFXPeriodData(eq(FXType.SIXHOUR), eq("usdjpy"),
				any(ZonedDateTime.class));
		verify(mock6HourService, times(0)).findPrevious(any(FXTickKey.class));
		verify(mock6HourService, times(0)).findOne(any(FXTickKey.class));
		verify(mock6HourService, times(0)).operation(any(TrnFX6Hour.class), eq(OperationMode.DELETE));
		verify(mock6HourService, times(0)).operation(any(TrnFX6Hour.class), eq(OperationMode.NEW));
	}

	/** Test normal data. */
	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/springbatch/execution/fx6hour/input.xml")
	@ExpectedDatabase(value = "/data/fx/springbatch/execution/fx6hour/expectedData.xml", table = "trn_fx_6hour", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testStepExecution() throws Exception {
		// New job parameters.
		JobParameters params = new JobParametersBuilder().addString("jobId", FXJob.GENERATE_FX_6HOUR_JOB.getValue())
				.addString("currencyPair", "usdjpy").addString("processedMonth", "201601").toJobParameters();
		// New step execution.
		StepExecution stepExecution = new StepExecution("Test Step",
				new JobExecution(new JobInstance(1L, "Test Job"), params, "Test Configuration"));
		execution.beforeStep(stepExecution);
		RepeatStatus status = execution.execute(stepExecution.createStepContribution(),
				new ChunkContext(new StepContext(stepExecution)));
		assertEquals(RepeatStatus.FINISHED, status);
		assertEquals(4, stepExecution.getJobExecution().getExecutionContext().getInt(Constants.INSERTED_ROWS + "6Hour"));
		assertEquals(1, stepExecution.getJobExecution().getExecutionContext().getInt(Constants.UPDATED_ROWS + "6Hour"));
	}
}