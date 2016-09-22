package com.akmans.trade.fx.springbatch.execution;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.akmans.trade.core.Constants;
import com.akmans.trade.core.enums.FXType;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.fx.service.FXDayService;
import com.akmans.trade.fx.service.FXMonthService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXMonth;

@Component
@StepScope
public class FXMonthGenerateExecution extends StepExecutionListenerSupport implements Tasklet {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXMonthGenerateExecution.class);

	private FXDayService fxDayService;

	private FXMonthService fxMonthService;

	private StepExecution stepExecution;

	@Autowired
	FXMonthGenerateExecution(FXDayService fxDayService, FXMonthService fxMonthService) {
		this.fxDayService = fxDayService;
		this.fxMonthService = fxMonthService;
	}

	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
		// Initialize inserted rows as 0.
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.INSERTED_ROWS + "Month", 0);
		// Initialize updated rows as 0.
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.UPDATED_ROWS + "Month", 0);
	}

	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		JobParameters jobParameters = stepExecution.getJobParameters();
		// Get currency pair from job parameters.
		String currencyPair = jobParameters.getString("currencyPair");
		// Get processed month from job parameters.
		String processedMonth = jobParameters.getString("processedMonth");
		logger.debug("The currencyPair is {}", currencyPair);
		logger.debug("The processedMonth is {}", processedMonth);
		// Get first hour.
		ZonedDateTime currentDatetime = getFirstDayOfMonth(processedMonth);
		// Get end day.
		ZonedDateTime endDay = currentDatetime.plusMonths(1);
		// Inserted rows counter.
		int insertedCnt = 0;
		// updated rows counter.
		int updatedCnt = 0;
		// Loop all instruments.
		while (currentDatetime.compareTo(endDay) < 0) {
			// Retrieve weekly data.
			TrnFXMonth fxMonth = (TrnFXMonth)fxDayService
					.generateFXPeriodData(FXType.MONTH, currencyPair, currentDatetime);
			// Continue to next instrument if no weekly data found.
			if (fxMonth != null) {
				// Retrieve previous FX Month data from DB by current key.
				Optional<TrnFXMonth> prevOption = fxMonthService.findPrevious(fxMonth.getTickKey());
				// If exists.
				if (prevOption.isPresent()) {
					TrnFXMonth previous = prevOption.get();
					fxMonth.setAvOpeningPrice((previous.getAvOpeningPrice() + previous.getAvFinishPrice()) / 2);
					fxMonth.setAvFinishPrice((fxMonth.getOpeningPrice() + fxMonth.getHighPrice() + fxMonth.getLowPrice()
							+ fxMonth.getFinishPrice()) / 4);
				} else {
					fxMonth.setAvOpeningPrice(fxMonth.getOpeningPrice());
					fxMonth.setAvFinishPrice(fxMonth.getFinishPrice());
				}
				// Retrieve FX Month data from DB by key.
				Optional<TrnFXMonth> option = fxMonthService.findOne(fxMonth.getTickKey());
				// Do delete & insert if exist, or else do insert.
				if (option.isPresent()) {
					// Get the current data.
					TrnFXMonth current = option.get();
					// Adapt all prices data.
					current.setOpeningPrice(fxMonth.getOpeningPrice());
					current.setHighPrice(fxMonth.getHighPrice());
					current.setLowPrice(fxMonth.getLowPrice());
					current.setFinishPrice(fxMonth.getFinishPrice());
					current.setAvOpeningPrice(fxMonth.getAvOpeningPrice());
					current.setAvFinishPrice(fxMonth.getAvFinishPrice());
					// Copy all data.
					BeanUtils.copyProperties(current, fxMonth);
					logger.debug("The updated data is {}", fxMonth);
					// Delete the current weekly data.
					fxMonthService.operation(current, OperationMode.DELETE);
					// Insert a new weekly data.
					fxMonthService.operation(fxMonth, OperationMode.NEW);
					// Count up the updated rows counter.
					updatedCnt++;
				} else {
					logger.debug("The inserted data is {}", fxMonth);
					// Insert a new weekly data.
					fxMonthService.operation(fxMonth, OperationMode.NEW);
					// Count up the inserted rows counter.
					insertedCnt++;
				}
			} else {
				logger.info("No FX Month generated at {}", currentDatetime);
			}
			// Increment with 1 day.
			currentDatetime = currentDatetime.plusMonths(1);
		}
		// Save updated rows counter into job execution context.
		countUpdatedRows(updatedCnt);
		// Save inserted rows counter into job execution context.
		countInsertedRows(insertedCnt);
		return RepeatStatus.FINISHED;
	}

	private void countInsertedRows(int cnt) {
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.INSERTED_ROWS + "Month",
				stepExecution.getJobExecution().getExecutionContext().getInt(Constants.INSERTED_ROWS + "Month", 0) + cnt);
	}

	private void countUpdatedRows(int cnt) {
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.UPDATED_ROWS + "Month",
				stepExecution.getJobExecution().getExecutionContext().getInt(Constants.UPDATED_ROWS + "Month", 0) + cnt);
	}

	private ZonedDateTime getFirstDayOfMonth(String processedMonth) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse(processedMonth + "01 00:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		return result;
	}
}
