package com.akmans.trade.stock.springbatch.execution;

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
import com.akmans.trade.core.utils.DateUtil;
import com.akmans.trade.stock.service.InstrumentService;
import com.akmans.trade.stock.service.JapanStockService;
import com.akmans.trade.stock.service.JapanStockWeeklyService;
import com.akmans.trade.stock.springdata.jpa.entities.MstInstrument;
import com.akmans.trade.stock.springdata.jpa.entities.TrnJapanStockWeekly;

@Component
public class JapanStockWeeklyGenerateExecution extends StepExecutionListenerSupport implements Tasklet {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(JapanStockWeeklyGenerateExecution.class);

	private Date applicationDate;

	@Autowired
	private InstrumentService instrumentService;

	@Autowired
	private JapanStockService japanStockService;

	@Autowired
	private JapanStockWeeklyService japanStockWeeklyService;

	private StepExecution stepExecution;

	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
		// Initialize inserted rows as 0.
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.INSERTED_ROWS + "Week", 0);
		// Initialize updated rows as 0.
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.UPDATED_ROWS + "Week", 0);
		JobParameters jobParameters = stepExecution.getJobParameters();
		// Get application date from job parameters.
		applicationDate = jobParameters.getDate("processDate");
		logger.debug("The applicationDate is {}", applicationDate);
	}

	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		// Get first day of week by the application date.
		Date dateFrom = DateUtil.getFirstDayOfWeek(applicationDate);
		// Get last day of week by the application date.
		Date dateTo = DateUtil.getLastDayOfWeek(applicationDate);
		// Inserted rows counter.
		int insertedCnt = 0;
		// updated rows counter.
		int updatedCnt = 0;
		// Get all instruments.
		List<MstInstrument> instruments = instrumentService.findAll();
		// Loop all instruments.
		for (MstInstrument instrument : instruments) {
			// Retrieve weekly data.
			TrnJapanStockWeekly japanStockWeekly = japanStockService
					.generateJapanStockWeeklyData(instrument.getCode().intValue(), dateFrom, dateTo);
			// Continue to next instrument if no weekly data found.
			if (japanStockWeekly == null) {
				continue;
			}
			// Do delete & insert if exist, or else do insert.
			if (japanStockWeeklyService.exist(japanStockWeekly.getJapanStockKey())) {
				// Get the current data.
				TrnJapanStockWeekly current = japanStockWeeklyService.findOne(japanStockWeekly.getJapanStockKey());
				// Adapt all prices data.
				current.setOpeningPrice(japanStockWeekly.getOpeningPrice());
				current.setHighPrice(japanStockWeekly.getHighPrice());
				current.setLowPrice(japanStockWeekly.getLowPrice());
				current.setFinishPrice(japanStockWeekly.getFinishPrice());
				current.setTurnover(japanStockWeekly.getTurnover());
				current.setTradingValue(japanStockWeekly.getTradingValue());
				// Copy all data.
				BeanUtils.copyProperties(current, japanStockWeekly);
				logger.debug("The updated data is {}", japanStockWeekly);
				// Delete the current weekly data.
				japanStockWeeklyService.operation(current, OperationMode.DELETE);
				// Insert a new weekly data.
				japanStockWeeklyService.operation(japanStockWeekly, OperationMode.NEW);
				// Count up the updated rows counter.
				updatedCnt++;
			} else {
				logger.debug("The inserted data is {}", japanStockWeekly);
				// Insert a new weekly data.
				japanStockWeeklyService.operation(japanStockWeekly, OperationMode.NEW);
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
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.INSERTED_ROWS + "Week",
				stepExecution.getJobExecution().getExecutionContext().getInt(Constants.INSERTED_ROWS + "Week", 0)
						+ cnt);
	}

	private void countUpdatedRows(int cnt) {
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.UPDATED_ROWS + "Week",
				stepExecution.getJobExecution().getExecutionContext().getInt(Constants.UPDATED_ROWS + "Week", 0) + cnt);
	}
}
