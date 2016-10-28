package com.akmans.trade.stock.springbatch.listener;

import java.util.Date;
import java.util.Optional;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.akmans.trade.core.Constants;
import com.akmans.trade.core.enums.JapanStockJob;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.MessageService;
import com.akmans.trade.core.utils.DateUtil;
import com.akmans.trade.core.utils.MailUtil;
import com.akmans.trade.stock.service.JapanStockLogService;
import com.akmans.trade.stock.springdata.jpa.entities.TrnJapanStockLog;
import com.akmans.trade.stock.springdata.jpa.keys.JapanStockLogKey;

@Component
public class JapanStockJobExecutionListener implements JobExecutionListener {

	@Autowired
	private MessageService messageService;

	@Autowired
	private JapanStockLogService japanStockLogService;

	@Autowired
	private MailUtil mailUtil;

	private Date beginTime;

	public void beforeJob(JobExecution jobExecution) {
		beginTime = new Date();
		String jobId = jobExecution.getJobParameters().getString("jobId");
		Date processDate = jobExecution.getJobParameters().getDate("processDate");
		updateJapanStockLog(jobId, processDate, new ExitStatus("EXECUTING").getExitCode());
	}

	public void afterJob(JobExecution jobExecution) {
		String jobId = jobExecution.getJobParameters().getString("jobId");
		Date processDate = jobExecution.getJobParameters().getDate("processDate");
		updateJapanStockLog(jobId, processDate, jobExecution.getExitStatus().getExitCode());
		String subject = JapanStockJob.get(jobId).getLabel() + " : " + jobExecution.getExitStatus().getExitCode() + "["
				+ DateUtil.formatDate(processDate, "yyyy-MM-dd") + "]";
		String body = "";
		long diff = new Date().getTime() - beginTime.getTime();
		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000);
		body = body + "Elapsed time is " + diffHours + " hours, " + diffMinutes + " minutes and " + diffSeconds
				+ " seconds \n";
		body = body + "------\n";
		if (JapanStockJob.IMPORT_JAPAN_STOCK_JOB.getValue().equals(jobId)) {
			int processedRows = jobExecution.getExecutionContext().getInt(Constants.PROCESSED_ROWS + "Day");
			int skippedRows = jobExecution.getExecutionContext().getInt(Constants.SKIPPED_ROWS + "Day");
			int insertedRows = jobExecution.getExecutionContext().getInt(Constants.INSERTED_ROWS + "Day");
			int updatedRows = jobExecution.getExecutionContext().getInt(Constants.UPDATED_ROWS + "Day");
			int insertedWeekRows = jobExecution.getExecutionContext().getInt(Constants.INSERTED_ROWS + "Week");
			int updatedWeekRows = jobExecution.getExecutionContext().getInt(Constants.UPDATED_ROWS + "Week");
			int insertedMonthRows = jobExecution.getExecutionContext().getInt(Constants.INSERTED_ROWS + "Month");
			int updatedMonthRows = jobExecution.getExecutionContext().getInt(Constants.UPDATED_ROWS + "Month");
			body = body + "<Day Data> \n";
			body = body + "Processed Rows: " + processedRows + "\n";
			body = body + "Skipped Rows: " + skippedRows + "\n";
			body = body + "Inserted Rows: " + insertedRows + "\n";
			body = body + "Updated Rows: " + updatedRows + "\n";
			body = body + "<Week Data> \n";
			body = body + "Inserted Rows: " + insertedWeekRows + "\n";
			body = body + "Updated Rows: " + updatedWeekRows + "\n";
			body = body + "<Month Data> \n";
			body = body + "Inserted Rows: " + insertedMonthRows + "\n";
			body = body + "Updated Rows: " + updatedMonthRows + "\n";
		}
		body = body + "------\n";
		body = body + "Exit Code: " + jobExecution.getExitStatus().getExitCode() + "\n";
		body = body + "Exit Description: " + jobExecution.getExitStatus().getExitDescription() + "\n";
		mailUtil.sendMail(subject, body);
	}

	private void updateJapanStockLog(String jobId, Date processDate, String status) {
		JapanStockLogKey japanStockLogKey = new JapanStockLogKey();
		japanStockLogKey.setJobId(jobId);
		japanStockLogKey.setProcessDate(processDate);
		try {
			Optional<TrnJapanStockLog> optional = japanStockLogService.findOne(japanStockLogKey);
			if (!optional.isPresent()) {
				throw new TradeException(messageService.getMessage("core.service.record.notfound", japanStockLogKey));
			}
			TrnJapanStockLog japanStockLog = optional.get();
			japanStockLog.setStatus(status);
			japanStockLogService.operation(japanStockLog, OperationMode.EDIT);
		} catch (TradeException te) {
			throw new RuntimeException(te);
		}
	}
}
