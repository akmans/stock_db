package com.akmans.trade.stock.springbatch.runner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.akmans.trade.core.enums.JapanStockJob;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.CalendarService;
import com.akmans.trade.stock.service.JapanStockLogService;
import com.akmans.trade.stock.springdata.jpa.entities.TrnJapanStockLog;
import com.akmans.trade.stock.springdata.jpa.keys.JapanStockLogKey;

@Component
public class JapanStockJobScheduler {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(JapanStockJobScheduler.class);

	@Autowired
	private CalendarService calendarService;

	@Autowired
	private JapanStockLogService japanStockLogService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private JobExplorer jobExplorer;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private JobOperator jobOperator;

	// @Autowired
	// private JobLauncher jobLauncher;

	// @Autowired
	// private Job job;

	@Autowired
	private ApplicationContext appContext;

//	@Scheduled(initialDelay = 60000, fixedDelay = 120000)
	public void bulkRun() {
		logger.debug("JapanStockJobScheduler start!!!");
		try {
			SecurityContext ctx = SecurityContextHolder.createEmptyContext();
			ctx.setAuthentication(authenticate());
			SecurityContextHolder.setContext(ctx);
			String jobId = JapanStockJob.IMPORT_JAPAN_STOCK_JOB.getValue();
			// Rescue uncompleted job.
			if (!rescue(jobId)) {
				Calendar cal1 = Calendar.getInstance();
				cal1.set(2099, 11, 31); // 20991231
				TrnJapanStockLog log = japanStockLogService
						.findMaxRegistDate(jobId, cal1.getTime());
				logger.debug("JapanStockJobScheduler log is !!!" + log);
				Date currentDate = log.getJapanStockLogKey().getProcessDate();
				logger.debug("JapanStockJobScheduler !!!" + currentDate);
				Calendar cal = Calendar.getInstance();
				cal.setTime(currentDate);
				do {
					cal.add(Calendar.DAY_OF_MONTH, 1);
					logger.debug("JapanStockJobScheduler cal is !!!" + cal.getTime());
				} while (!calendarService.isJapanBusinessDay(cal.getTime()) && cal.getTime().compareTo(new Date()) < 0);

				Date processDate = cal.getTime();
				// launch new job.
				launch(jobId, processDate);
			}
		} catch (Exception e) {
			logger.error("Some error occurred!", e);
//			e.printStackTrace();
		} finally {
			SecurityContextHolder.clearContext();
		}
		logger.debug("JapanStockJobScheduler end!!!");
	}

//	@Scheduled(cron = "0 0 20 * * MON-FRI")
	public void run() {
		logger.debug("JapanStockJobScheduler start!!!");
		try {
			SecurityContext ctx = SecurityContextHolder.createEmptyContext();
			ctx.setAuthentication(authenticate());
			SecurityContextHolder.setContext(ctx);
			String jobId = JapanStockJob.IMPORT_JAPAN_STOCK_JOB.getValue();
			Date processDate = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DAY_OF_MONTH);
			// launch new job.
			launch(jobId, processDate);
		} catch (Exception e) {
			logger.error("Some error occurred!", e);
//			e.printStackTrace();
		} finally {
			SecurityContextHolder.clearContext();
		}
		logger.debug("JapanStockJobScheduler end!!!");
	}

	private Authentication authenticate() {
		final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("USER"));
		return new UsernamePasswordAuthenticationToken("scheduler", "scheduler", authorities);
	}

	private boolean rescue(String jobId) throws Exception {
		// Get one latest job from the database
		List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobId, 0, 1);
		if (CollectionUtils.isNotEmpty(jobInstances)) {
			logger.debug("rescue step in jobInstances");
			JobInstance jobInstance = jobInstances.get(0);
			List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
			if (CollectionUtils.isNotEmpty(jobExecutions)) {
				logger.debug("rescue step in jobExecutions");
				for (JobExecution execution : jobExecutions) {
					// If the job status is STARTED then update the status
					// to FAILED and restart the job using JobOperator.java
					if (execution.getStatus().equals(BatchStatus.STARTED)) {
						logger.debug("rescue step in BatchStatus.STARTED");
						execution.setEndTime(new Date());
						execution.setStatus(BatchStatus.FAILED);
						execution.setExitStatus(ExitStatus.FAILED);
						jobRepository.update(execution);
						jobOperator.restart(execution.getId());
						return true;
					}
				}
			}
		}
		return false;
	}

	private void launch(String jobId, Date processDate) throws TradeException, Exception{
		JapanStockLogKey japanStockLogKey = new JapanStockLogKey();
		japanStockLogKey.setJobId(jobId);
		japanStockLogKey.setProcessDate(processDate);
		TrnJapanStockLog japanStockLog = null;
		Optional<TrnJapanStockLog> optional = japanStockLogService.findOne(japanStockLogKey);
		if (optional.isPresent()) {
//			japanStockLog = japanStockLogService.findOne(japanStockLogKey);
			if (ExitStatus.COMPLETED.getExitCode().equals(optional.get().getStatus())) {
				// get message.
				String message = messageSource.getMessage("controller.japanstocklog.job.already.completed",
						new Object[] { japanStockLogKey }, Locale.ENGLISH); // TODO locale
				throw new TradeException(message);
			}
			// do confirm operation.
			japanStockLogService.operation(japanStockLog, OperationMode.EDIT);
		} else {
			japanStockLog = new TrnJapanStockLog();
			japanStockLog.setJapanStockLogKey(japanStockLogKey);
			// do confirm operation.
			japanStockLogService.operation(japanStockLog, OperationMode.NEW);
		}

//		JobLauncher jobLauncher = (JobLauncher) appContext.getBean("jobLauncher");
		Job job = (Job) appContext.getBean(jobId);
		logger.info("Job Restartable ? : " + job.isRestartable());
		JobParameters params = new JobParametersBuilder().addString("jobId", jobId)
				.addDate("processDate", processDate).toJobParameters();
		JobExecution execution = jobLauncher.run(job, params);
		logger.info("Status : [" + execution.getStatus() + "] Eixt Code: [" + execution.getExitStatus() + "]");
	}
}
