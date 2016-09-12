package com.akmans.trade.fx.springbatch.listener;

import java.util.Date;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.akmans.trade.core.Constants;
import com.akmans.trade.core.enums.FXJob;
import com.akmans.trade.core.utils.MailUtil;

@Component
public class FXJobExecutionListener implements JobExecutionListener {

	@Autowired
	private MailUtil mailUtil;

	private Date beginTime;

	public void beforeJob(JobExecution jobExecution) {
		beginTime = new Date();
	}

	public void afterJob(JobExecution jobExecution) {
		String jobId = jobExecution.getJobParameters().getString("jobId");
		// Get currency pair from job parameters.
		String currencyPair = jobExecution.getJobParameters().getString("currencyPair");
		// Get processed month from job parameters.
		String processedMonth = jobExecution.getJobParameters().getString("processedMonth");
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
		if (FXJob.IMPORT_HISTORY_TICK_JOB.getValue().equals(jobId)) {
			int processedRows = jobExecution.getExecutionContext().getInt(Constants.PROCESSED_ROWS);
			int skippedRows = jobExecution.getExecutionContext().getInt(Constants.SKIPPED_ROWS);
			int insertedRows = jobExecution.getExecutionContext().getInt(Constants.INSERTED_ROWS);
			int updatedRows = jobExecution.getExecutionContext().getInt(Constants.UPDATED_ROWS);
			body = body + "Processed Rows: " + processedRows + "\n";
			body = body + "Skipped Rows: " + skippedRows + "\n";
			body = body + "Inserted Rows: " + insertedRows + "\n";
			body = body + "Updated Rows: " + updatedRows + "\n";
		} else if (FXJob.GENERATE_FX_HOUR_JOB.getValue().equals(jobId)) {
			int insertedRows = jobExecution.getExecutionContext().getInt(Constants.INSERTED_ROWS);
			int updatedRows = jobExecution.getExecutionContext().getInt(Constants.UPDATED_ROWS);
			body = body + "Inserted Rows: " + insertedRows + "\n";
			body = body + "Updated Rows: " + updatedRows + "\n";
		}
		body = body + "------\n";
		body = body + "Exit Code: " + jobExecution.getExitStatus().getExitCode() + "\n";
		body = body + "Exit Description: " + jobExecution.getExitStatus().getExitDescription() + "\n";
		mailUtil.sendMail(subject, body);
	}
}
