package com.akmans.trade.standalone.springbatch.execution;

import java.util.Date;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.akmans.trade.core.Constants;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.service.InstrumentService;
import com.akmans.trade.core.service.JapanStockService;
import com.akmans.trade.core.service.JapanStockMonthlyService;
import com.akmans.trade.core.springdata.jpa.entities.MstInstrument;
import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStockMonthly;
import com.akmans.trade.core.utils.DateUtil;

@Component
public class JapanStockMonthlyGenerateExecution extends StepExecutionListenerSupport implements Tasklet {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(JapanStockMonthlyGenerateExecution.class);

	private Date applicationDate;

	@Autowired
	private InstrumentService instrumentService;

	@Autowired
	private JapanStockService japanStockService;

	@Autowired
	private JapanStockMonthlyService japanStockMonthlyService;

	private StepExecution stepExecution;

	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
		// Initialize inserted rows as 0.
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.INSERTED_ROWS, 0);
		// Initialize updated rows as 0.
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.UPDATED_ROWS, 0);
		JobParameters jobParameters = stepExecution.getJobParameters();
		// Get application date from job parameters.
		applicationDate = jobParameters.getDate("processDate");
		logger.debug("The applicationDate is {}", applicationDate);
	}

	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		// Get first day of week by the application date.
		Date dateFrom = DateUtil.getFirstDayOfMonth(applicationDate);
		// Get last day of week by the application date.
		Date dateTo = DateUtil.getLastDayOfMonth(applicationDate);
		// Inserted rows counter.
		int insertedCnt = 0;
		// updated rows counter.
		int updatedCnt = 0;
		// Get all instruments.
		List<MstInstrument> instruments = instrumentService.findAll();
		// Loop all instruments.
		for (MstInstrument instrument : instruments) {
			// Retrieve Monthly data.
			TrnJapanStockMonthly japanStockMonthly = japanStockService
					.generateJapanStockMonthlyData(instrument.getCode().intValue(), dateFrom, dateTo);
			// Continue to next instrument if no Monthly data found.
			if (japanStockMonthly == null) {
				continue;
			}
			// Do delete & insert if exist, or else do insert.
			if (japanStockMonthlyService.exist(japanStockMonthly.getJapanStockKey())) {
				// Get the current data.
				TrnJapanStockMonthly current = japanStockMonthlyService.findOne(japanStockMonthly.getJapanStockKey());
				// Adapt all prices data.
				current.setOpeningPrice(japanStockMonthly.getOpeningPrice());
				current.setHighPrice(japanStockMonthly.getHighPrice());
				current.setLowPrice(japanStockMonthly.getLowPrice());
				current.setFinishPrice(japanStockMonthly.getFinishPrice());
				current.setTurnover(japanStockMonthly.getTurnover());
				current.setTradingValue(japanStockMonthly.getTradingValue());
				// Copy all data.
				BeanUtils.copyProperties(current, japanStockMonthly);
				logger.debug("The updated data is {}", japanStockMonthly);
				// Delete the current Monthly data.
				japanStockMonthlyService.operation(current, OperationMode.DELETE);
				// Insert a new Monthly data.
				japanStockMonthlyService.operation(japanStockMonthly, OperationMode.NEW);
				// Count up the updated rows counter.
				updatedCnt++;
			} else {
				logger.debug("The inserted data is {}", japanStockMonthly);
				// Insert a new Monthly data.
				japanStockMonthlyService.operation(japanStockMonthly, OperationMode.NEW);
				// Count up the inserted rows counter.
				insertedCnt++;
			}
		}
		// Save updated rows counter into job execution context.
		countUpdatedRows(updatedCnt);
		// Save inserted rows counter into job execution context.
		countInsertedRows(insertedCnt);
		return RepeatStatus.FINISHED;
	}

	private void countInsertedRows(int cnt) {
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.INSERTED_ROWS,
				stepExecution.getJobExecution().getExecutionContext().getInt(Constants.INSERTED_ROWS, 0) + cnt);
	}

	private void countUpdatedRows(int cnt) {
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.UPDATED_ROWS,
				stepExecution.getJobExecution().getExecutionContext().getInt(Constants.UPDATED_ROWS, 0) + cnt);
	}
}
