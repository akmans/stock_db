package com.akmans.trade.fx.springbatch.listener;

import java.util.Date;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.akmans.trade.core.Constants;
import com.akmans.trade.core.enums.FXJob;
import com.akmans.trade.core.utils.MailUtil;

@Component
public class FXJobExecutionListener implements JobExecutionListener {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXJobExecutionListener.class);

	private MailUtil mailUtil;

	private Date beginTime;

	@Autowired
	FXJobExecutionListener(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
	}

	public void beforeJob(JobExecution jobExecution) {
		beginTime = new Date();
	}

	public void afterJob(JobExecution jobExecution) {
		String jobId = jobExecution.getJobParameters().getString("jobId");
		// Get currency pair from job parameters.
		String currencyPair = jobExecution.getJobParameters().getString("currencyPair");
		// Get processed month from job parameters.
		String processedMonth = jobExecution.getJobParameters().getString("processedMonth");
		logger.debug("The jobId is {}", jobId);
		logger.debug("The currencyPair is {}", currencyPair);
		logger.debug("The processedMonth is {}", processedMonth);
		String subject = FXJob.get(jobId).getLabel() + " : " + jobExecution.getExitStatus().getExitCode() + "["
				+ currencyPair + "#" + processedMonth + "]";
		String body = "";
		long diff = new Date().getTime() - beginTime.getTime();
		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000);
		body = body + "Elapsed time is " + diffHours + " hours, " + diffMinutes + " minutes and " + diffSeconds
				+ " seconds \n";
		body = body + "------\n";
		if (FXJob.GENERATE_FX_CANDLESTICK_DATA_JOB.getValue().equals(jobId)) {
			int insertedRowsHour = jobExecution.getExecutionContext().getInt(Constants.INSERTED_ROWS + "Hour");
			int updatedRowsHour = jobExecution.getExecutionContext().getInt(Constants.UPDATED_ROWS + "Hour");
			int insertedRows6Hour = jobExecution.getExecutionContext().getInt(Constants.INSERTED_ROWS + "6Hour");
			int updatedRows6Hour = jobExecution.getExecutionContext().getInt(Constants.UPDATED_ROWS + "6Hour");
			int insertedRowsDay = jobExecution.getExecutionContext().getInt(Constants.INSERTED_ROWS + "Day");
			int updatedRowsDay = jobExecution.getExecutionContext().getInt(Constants.UPDATED_ROWS + "Day");
			int insertedRowsWeek = jobExecution.getExecutionContext().getInt(Constants.INSERTED_ROWS + "Week");
			int updatedRowsWeek = jobExecution.getExecutionContext().getInt(Constants.UPDATED_ROWS + "Week");
			int insertedRowsMonth = jobExecution.getExecutionContext().getInt(Constants.INSERTED_ROWS + "Month");
			int updatedRowsMonth = jobExecution.getExecutionContext().getInt(Constants.UPDATED_ROWS + "Month");
			body = body + "<Hour Data> \n";
			body = body + "Inserted Rows: " + insertedRowsHour + "\n";
			body = body + "Updated Rows: " + updatedRowsHour + "\n\n";
			body = body + "<6Hour Data> \n";
			body = body + "Inserted Rows: " + insertedRows6Hour + "\n";
			body = body + "Updated Rows: " + updatedRows6Hour + "\n\n";
			body = body + "<Day Data> \n";
			body = body + "Inserted Rows: " + insertedRowsDay + "\n";
			body = body + "Updated Rows: " + updatedRowsDay + "\n\n";
			body = body + "<Week Data> \n";
			body = body + "Inserted Rows: " + insertedRowsWeek + "\n";
			body = body + "Updated Rows: " + updatedRowsWeek + "\n\n";
			body = body + "<Month Data> \n";
			body = body + "Inserted Rows: " + insertedRowsMonth + "\n";
			body = body + "Updated Rows: " + updatedRowsMonth + "\n\n";
		}
		body = body + "------\n";
		body = body + "Exit Code: " + jobExecution.getExitStatus().getExitCode() + "\n";
		body = body + "Exit Description: " + jobExecution.getExitStatus().getExitDescription() + "\n";
		mailUtil.sendMail(subject, body);
	}
}
