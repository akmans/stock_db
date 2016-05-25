package com.akmans.trade.standalone.springbatch.listener;

import java.util.Date;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.JapanStockLogService;
import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStockLog;
import com.akmans.trade.core.springdata.jpa.keys.JapanStockLogKey;
import com.akmans.trade.core.utils.MailUtil;

@Component
public class JapanStockJobExecutionListener implements JobExecutionListener {

	@Autowired
	private JapanStockLogService japanStockLogService;

	@Autowired
	private MailUtil mailUtil;

	public void beforeJob(JobExecution jobExecution) {
		String jobId = jobExecution.getJobParameters().getString("jobId");
		Date processDate = jobExecution.getJobParameters().getDate("processDate");
		updateJapanStockLog(jobId, processDate, new ExitStatus("EXECUTING").getExitCode());
	}

	public void afterJob(JobExecution jobExecution) {
		String jobId = jobExecution.getJobParameters().getString("jobId");
		Date processDate = jobExecution.getJobParameters().getDate("processDate");
		updateJapanStockLog(jobId, processDate, jobExecution.getExitStatus().getExitCode());
		// mailUtil.sendMail();
	}

	private void updateJapanStockLog(String jobId, Date processDate, String status) {
		JapanStockLogKey japanStockLogKey = new JapanStockLogKey();
		japanStockLogKey.setJobId(jobId);
		japanStockLogKey.setProcessDate(processDate);
		try {
			TrnJapanStockLog japanStockLog = japanStockLogService.findOne(japanStockLogKey);
			japanStockLog.setStatus(status);
			japanStockLogService.operation(japanStockLog, OperationMode.EDIT);
		} catch (TradeException te) {
			throw new RuntimeException(te);
		}
	}
}
