package com.akmans.trade.fx.springbatch.execution;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
import com.akmans.trade.fx.service.FXHourService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXDay;

@Component
@StepScope
public class FXDayGenerateExecution extends StepExecutionListenerSupport implements Tasklet {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXDayGenerateExecution.class);

	private FXHourService fxHourService;

	private FXDayService fxDayService;

	private StepExecution stepExecution;

	@Autowired
	FXDayGenerateExecution(FXHourService fxHourService, FXDayService fxDayService) {
		this.fxHourService = fxHourService;
		this.fxDayService = fxDayService;
	}

	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
		// Initialize inserted rows as 0.
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.INSERTED_ROWS + "Day", 0);
		// Initialize updated rows as 0.
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.UPDATED_ROWS + "Day", 0);
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
		LocalDateTime currentDatetime = getFirstDayOfMonth(processedMonth);
		// Get end day.
		LocalDateTime endDay = currentDatetime.plusMonths(1);
		// Inserted rows counter.
		int insertedCnt = 0;
		// updated rows counter.
		int updatedCnt = 0;
		// Loop all instruments.
		while (currentDatetime.compareTo(endDay) < 0) {
			// Retrieve daily data.
			TrnFXDay fxDay = (TrnFXDay)fxHourService
					.generateFXPeriodData(FXType.DAY, currencyPair, currentDatetime);
			// Continue to next instrument if no weekly data found.
			if (fxDay != null) {
				// Retrieve previous FX Day data from DB by current key.
				Optional<TrnFXDay> prevOption = fxDayService.findPrevious(fxDay.getTickKey());
				// If exists.
				if (prevOption.isPresent()) {
					TrnFXDay previous = prevOption.get();
					fxDay.setAvOpeningPrice((previous.getAvOpeningPrice() + previous.getAvFinishPrice()) / 2);
					fxDay.setAvFinishPrice((fxDay.getOpeningPrice() + fxDay.getHighPrice() + fxDay.getLowPrice()
							+ fxDay.getFinishPrice()) / 4);
				} else {
					fxDay.setAvOpeningPrice(fxDay.getOpeningPrice());
					fxDay.setAvFinishPrice(fxDay.getFinishPrice());
				}
				// Retrieve FX Day data from DB by key.
				Optional<TrnFXDay> option = fxDayService.findOne(fxDay.getTickKey());
				// Do delete & insert if exist, or else do insert.
				if (option.isPresent()) {
					// Get the current data.
					TrnFXDay current = option.get();
					// Adapt all prices data.
					current.setOpeningPrice(fxDay.getOpeningPrice());
					current.setHighPrice(fxDay.getHighPrice());
					current.setLowPrice(fxDay.getLowPrice());
					current.setFinishPrice(fxDay.getFinishPrice());
					current.setAvOpeningPrice(fxDay.getAvOpeningPrice());
					current.setAvFinishPrice(fxDay.getAvFinishPrice());
					// Copy all data.
					BeanUtils.copyProperties(current, fxDay);
					logger.debug("The updated data is {}", fxDay);
					// Delete the current weekly data.
					fxDayService.operation(current, OperationMode.DELETE);
					// Insert a new weekly data.
					fxDayService.operation(fxDay, OperationMode.NEW);
					// Count up the updated rows counter.
					updatedCnt++;
				} else {
					logger.debug("The inserted data is {}", fxDay);
					// Insert a new weekly data.
					fxDayService.operation(fxDay, OperationMode.NEW);
					// Count up the inserted rows counter.
					insertedCnt++;
				}
			} else {
				logger.info("No FX Day generated at {}", currentDatetime);
			}
			// Increment with 1 day.
			currentDatetime = currentDatetime.plusDays(1);
		}
		// Save updated rows counter into job execution context.
		countUpdatedRows(updatedCnt);
		// Save inserted rows counter into job execution context.
		countInsertedRows(insertedCnt);
		return RepeatStatus.FINISHED;
	}

	private void countInsertedRows(int cnt) {
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.INSERTED_ROWS + "Day",
				stepExecution.getJobExecution().getExecutionContext().getInt(Constants.INSERTED_ROWS + "Day", 0) + cnt);
	}

	private void countUpdatedRows(int cnt) {
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.UPDATED_ROWS + "Day",
				stepExecution.getJobExecution().getExecutionContext().getInt(Constants.UPDATED_ROWS + "Day", 0) + cnt);
	}

	private LocalDateTime getFirstDayOfMonth(String processedMonth) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse(processedMonth + "01 00:00:00.000", formatter);
		return dateTime.truncatedTo(ChronoUnit.DAYS);
	}
}
