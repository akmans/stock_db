package com.akmans.trade.fx.springbatch.listener;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.containsString;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.akmans.trade.core.Constants;
import com.akmans.trade.core.config.TestConfig;
import com.akmans.trade.core.enums.FXJob;
import com.akmans.trade.core.utils.MailUtil;
import com.github.springtestdbunit.DbUnitTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class FXJobExecutionListenerTest {

	/** Test no data. */
	@Test
	public void testAfterJobWithMock() throws Exception {
		final Map<String, String> map = new HashMap<String, String>();
		MailUtil mailUtil = Mockito.mock(MailUtil.class);
		FXJobExecutionListener listener = new FXJobExecutionListener(mailUtil);
		// New job parameters.
		JobParameters params = new JobParametersBuilder()
				.addString("jobId", FXJob.GENERATE_FX_CANDLESTICK_DATA_JOB.getValue())
				.addString("currencyPair", "usdjpy").addString("processedMonth", "200905").toJobParameters();
		// New step execution.
		JobExecution jobExecution = new JobExecution(new JobInstance(1L, "Test Job"), params, "Test Configuration");
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] arguments = invocation.getArguments();
				if (arguments != null && arguments.length > 0 && arguments[0] != null) {
					map.put("subject", (String) arguments[0]);
				}
				if (arguments != null && arguments.length > 1 && arguments[1] != null) {
					map.put("body", (String) arguments[1]);
				}
				return null;
			}
		}).when(mailUtil).sendMail(anyString(), anyString());
		listener.beforeJob(jobExecution);
		jobExecution.getExecutionContext().putInt(Constants.INSERTED_ROWS + "Hour", 1);
		jobExecution.getExecutionContext().putInt(Constants.UPDATED_ROWS + "Hour", 1);
		jobExecution.getExecutionContext().putInt(Constants.INSERTED_ROWS + "6Hour", 1);
		jobExecution.getExecutionContext().putInt(Constants.UPDATED_ROWS + "6Hour", 1);
		jobExecution.getExecutionContext().putInt(Constants.INSERTED_ROWS + "Day", 1);
		jobExecution.getExecutionContext().putInt(Constants.UPDATED_ROWS + "Day", 1);
		jobExecution.getExecutionContext().putInt(Constants.INSERTED_ROWS + "Week", 1);
		jobExecution.getExecutionContext().putInt(Constants.UPDATED_ROWS + "Week", 1);
		jobExecution.getExecutionContext().putInt(Constants.INSERTED_ROWS + "Month", 1);
		jobExecution.getExecutionContext().putInt(Constants.UPDATED_ROWS + "Month", 1);
		jobExecution.setExitStatus(ExitStatus.COMPLETED);
		listener.afterJob(jobExecution);
		assertNotNull(map.get("subject"));
		assertEquals("ローソク足データ生成処理 : COMPLETED[usdjpy#200905]", map.get("subject"));
		assertNotNull(map.get("body"));
		assertThat(map.get("body"), containsString("<Hour Data>"));
		assertThat(map.get("body"), containsString("<6Hour Data>"));
		assertThat(map.get("body"), containsString("<Day Data>"));
		assertThat(map.get("body"), containsString("<Week Data>"));
		assertThat(map.get("body"), containsString("<Month Data>"));
	}
}