package com.akmans.trade.fx.jobconfig;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.akmans.trade.core.config.TestConfig;
import com.akmans.trade.core.enums.FXJob;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class GenerateFXCandlestickDataJobConfigTest {

	@Autowired
	private ApplicationContext context;

	/** Test normal data. */
	@Test
	@DatabaseSetup(type = DatabaseOperation.DELETE_ALL, value = {"/data/fx/emptyAll.xml"})
	@DatabaseSetup(type = DatabaseOperation.INSERT, value = {"/data/fx/jobconfig/fxcandlestickdata/input.xml"})
	@ExpectedDatabase(value = "/data/fx/jobconfig/fxcandlestickdata/expectedData.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testStepExecution() throws Exception {
		// New job parameters.
		JobParameters params = new JobParametersBuilder().addString("jobId", FXJob.GENERATE_FX_CANDLESTICK_DATA_JOB.getValue())
				.addString("currencyPair", "usdjpy").addString("processedMonth", "201601").toJobParameters();
		String jobId = FXJob.GENERATE_FX_CANDLESTICK_DATA_JOB.getValue();
		JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
		JobRepository jobRepository = (JobRepository) context.getBean("jobRepository");
		Job job = (Job) context.getBean(jobId);
		JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
		jobLauncherTestUtils.setJobLauncher(jobLauncher);
		jobLauncherTestUtils.setJobRepository(jobRepository);
		jobLauncherTestUtils.setJob(job);
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(params);
		assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
	}
}