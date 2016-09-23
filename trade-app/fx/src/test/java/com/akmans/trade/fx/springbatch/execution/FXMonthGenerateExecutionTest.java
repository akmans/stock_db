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
import com.akmans.trade.fx.service.FXMonthService;
import com.akmans.trade.fx.service.FXDayService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXMonth;
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
public class FXMonthGenerateExecutionTest {

	@Autowired
	private FXMonthGenerateExecution execution;

	@Test
	public void testBeforeStep() {
		StepExecution stepExecution = new StepExecution("Test Step",
				new JobExecution(new JobInstance(1L, "Test Job"), new JobParameters(), "Test Configuration"));
		execution.beforeStep(stepExecution);
		assertEquals(0, stepExecution.getJobExecution().getExecutionContext().getInt(Constants.INSERTED_ROWS + "Month"));
	}

	/** Test no data. */
	@Test
	public void testStepExecutionWithMock() throws Exception {
		FXDayService mockDayService = Mockito.mock(FXDayService.class);
		FXMonthService mockMonthService = Mockito.mock(FXMonthService.class);
		FXMonthGenerateExecution execution = new FXMonthGenerateExecution(mockDayService, mockMonthService);
		// New job parameters.
		JobParameters params = new JobParametersBuilder().addString("jobId", FXJob.GENERATE_FX_MONTH_JOB.getValue())
				.addString("currencyPair", "usdjpy").addString("processedMonth", "200905").toJobParameters();
		// New step execution.
		StepExecution stepExecution = new StepExecution("Test Step",
				new JobExecution(new JobInstance(1L, "Test Job"), params, "Test Configuration"));
		execution.beforeStep(stepExecution);
		RepeatStatus status = execution.execute(stepExecution.createStepContribution(),
				new ChunkContext(new StepContext(stepExecution)));
		assertEquals(RepeatStatus.FINISHED, status);
		assertEquals(0, stepExecution.getJobExecution().getExecutionContext().getInt(Constants.INSERTED_ROWS + "Month"));
		assertEquals(0, stepExecution.getJobExecution().getExecutionContext().getInt(Constants.UPDATED_ROWS + "Month"));
		// Verify
		verify(mockDayService, times(1)).generateFXPeriodData(eq(FXType.MONTH), eq("usdjpy"),
				any(LocalDateTime.class));
		verify(mockMonthService, times(0)).findPrevious(any(FXTickKey.class));
		verify(mockMonthService, times(0)).findOne(any(FXTickKey.class));
		verify(mockMonthService, times(0)).operation(any(TrnFXMonth.class), eq(OperationMode.DELETE));
		verify(mockMonthService, times(0)).operation(any(TrnFXMonth.class), eq(OperationMode.NEW));
	}

	/** Test normal data. */
	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/data/fx/springbatch/execution/fxmonth/input.xml")
	@ExpectedDatabase(value = "/data/fx/springbatch/execution/fxmonth/expectedData.xml", table = "trn_fx_month", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = "/data/fx/emptyAll.xml")
	public void testStepExecution() throws Exception {
		// New job parameters.
		JobParameters params = new JobParametersBuilder().addString("jobId", FXJob.GENERATE_FX_MONTH_JOB.getValue())
				.addString("currencyPair", "usdjpy").addString("processedMonth", "201601").toJobParameters();
		// New step execution.
		StepExecution stepExecution = new StepExecution("Test Step",
				new JobExecution(new JobInstance(1L, "Test Job"), params, "Test Configuration"));
		execution.beforeStep(stepExecution);
		RepeatStatus status = execution.execute(stepExecution.createStepContribution(),
				new ChunkContext(new StepContext(stepExecution)));
		assertEquals(RepeatStatus.FINISHED, status);
		assertEquals(1, stepExecution.getJobExecution().getExecutionContext().getInt(Constants.INSERTED_ROWS + "Month"));
		assertEquals(0, stepExecution.getJobExecution().getExecutionContext().getInt(Constants.UPDATED_ROWS + "Month"));
	}
}