package com.akmans.trade.standalone.console;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.akmans.trade.core.enums.JapanStockJob;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.service.JapanStockLogService;
import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStockLog;
import com.akmans.trade.core.springdata.jpa.keys.JapanStockLogKey;
import com.akmans.trade.standalone.config.StandaloneConfig;

public class GenerateJapanStockWeeklyApp {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(GenerateJapanStockWeeklyApp.class);

	public static void main(String[] args) {

		if (args == null || args.length != 1) {
			logger.error("Parameter invalid!");
			return;
		}

		AnnotationConfigApplicationContext context = null;

		try {
			// Initialize application context
			context = new AnnotationConfigApplicationContext();
			context.register(StandaloneConfig.class);
			context.refresh();
			// Date process
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date endDate = formatter.parse(args[0] + "-12-31");
			Calendar calendar = new GregorianCalendar(Integer.valueOf(args[0]), 0, 1);

			while (calendar.getTime().compareTo(endDate) <= 0) {
				String jobId = JapanStockJob.GENERATE_JAPAN_STOCK_WEEKLY_JOB.getValue();
				TrnJapanStockLog japanStockLog = new TrnJapanStockLog();
				JapanStockLogKey japanStockLogKey = new JapanStockLogKey();
				japanStockLogKey.setJobId(jobId);
				japanStockLogKey.setProcessDate(calendar.getTime());
				japanStockLog.setJapanStockLogKey(japanStockLogKey);
				// do confirm operation.
				JapanStockLogService japanStockLogService = (JapanStockLogService) context
						.getBean(JapanStockLogService.class);
				japanStockLogService.operation(japanStockLog, OperationMode.NEW);
				JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
				Job job = (Job) context.getBean(jobId);
				logger.info("Job Restartable ? : " + job.isRestartable());

				JobParameters params = new JobParametersBuilder().addString("jobId", jobId)
						.addDate("processDate", calendar.getTime()).toJobParameters();
				JobExecution execution = jobLauncher.run(job, params);
				logger.info("Exit Status : " + execution.getStatus());
				calendar.add(Calendar.DAY_OF_MONTH, 7);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (context != null) {
				context.close();
			}
		}

		System.out.println("Done");

	}
}
