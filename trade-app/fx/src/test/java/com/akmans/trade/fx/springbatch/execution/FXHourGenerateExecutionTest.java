package com.akmans.trade.fx.springbatch.execution;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.time.LocalDateTime;

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
import com.akmans.trade.fx.service.FXHourService;
import com.akmans.trade.fx.service.FXTickService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXHour;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		StepScopeTestExecutionListener.class })
public class FXHourGenerateExecutionTest {

	@Autowired
	private FXHourGenerateExecution execution;

	@Test
	public void testBeforeStep() {
		StepExecution stepExecution = new StepExecution("Test Step",
				new JobExecution(new JobInstance(1L, "Test Job"), new JobParameters(), "Test Configuration"));
		execution.beforeStep(stepExecution);
		assertEquals(0, stepExecution.getJobExecution().getExecutionContext().getInt(Constants.INSERTED_ROWS + "Hour"));
	}

	/** Test no data. */
	@Test
	public void testStepExecutionWithMock() throws Exception {
		FXTickService mockTickService = Mockito.mock(FXTickService.class);
		FXHourService mockHourService = Mockito.mock(FXHourService.class);
		FXHourGenerateExecution execution = new FXHourGenerateExecution(mockTickService, mockHourService);
		// New job parameters.
		JobParameters params = new JobParametersBuilder().addString("jobId", FXJob.GENERATE_FX_HOUR_JOB.getValue())
				.addString("currencyPair", "usdjpy").addString("processedMonth", "200905").toJobParameters();
		// New step execution.
		StepExecution stepExecution = new StepExecution("Test Step",
				new JobExecution(new JobInstance(1L, "Test Job"), params, "Test Configuration"));
		execution.beforeStep(stepExecution);
		RepeatStatus status = execution.execute(stepExecution.createStepContribution(),
				new ChunkContext(new StepContext(stepExecution)));
		assertEquals(RepeatStatus.FINISHED, status);
		assertEquals(0, stepExecution.getJobExecution().getExecutionContext().getInt(Constants.INSERTED_ROWS + "Hour"));
		assertEquals(0, stepExecution.getJobExecution().getExecutionContext().getInt(Constants.UPDATED_ROWS + "Hour"));
		// Verify
		verify(mockTickService, times(24 * 31)).generateFXPeriodData(eq(FXType.HOUR), eq("usdjpy"),
				any(LocalDateTime.class));
		verify(mockHourService, times(0)).findPrevious(any(FXTickKey.class));
		verify(mockHourService, times(0)).findOne(any(FXTickKey.class));
		verify(mockHourService, times(0)).operation(any(TrnFXHour.class), eq(OperationMode.DELETE));
		verify(mockHourService, times(0)).operation(any(TrnFXHour.class), eq(OperationMode.NEW));
	}

	/** Test normal data. */
	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = "/data/fx/springbatch/execution/fxhour/input.xml")
	@ExpectedDatabase(value = "/data/fx/springbatch/execution/fxhour/expectedData.xml", table = "trn_fx_hour", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testStepExecution() throws Exception {
		// New job parameters.
		JobParameters params = new JobParametersBuilder().addString("jobId", FXJob.GENERATE_FX_HOUR_JOB.getValue())
				.addString("currencyPair", "usdjpy").addString("processedMonth", "201601").toJobParameters();
		// New step execution.
		StepExecution stepExecution = new StepExecution("Test Step",
				new JobExecution(new JobInstance(1L, "Test Job"), params, "Test Configuration"));
		execution.beforeStep(stepExecution);
		RepeatStatus status = execution.execute(stepExecution.createStepContribution(),
				new ChunkContext(new StepContext(stepExecution)));
		assertEquals(RepeatStatus.FINISHED, status);
		assertEquals(4, stepExecution.getJobExecution().getExecutionContext().getInt(Constants.INSERTED_ROWS + "Hour"));
		assertEquals(1, stepExecution.getJobExecution().getExecutionContext().getInt(Constants.UPDATED_ROWS + "Hour"));
	}
}