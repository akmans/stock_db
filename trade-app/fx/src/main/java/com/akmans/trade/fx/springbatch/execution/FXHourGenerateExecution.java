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
import com.akmans.trade.fx.service.FXHourService;
import com.akmans.trade.fx.service.FXTickService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXHour;

@Component
@StepScope
public class FXHourGenerateExecution extends StepExecutionListenerSupport implements Tasklet {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXHourGenerateExecution.class);

	private FXTickService fxTickService;

	private FXHourService fxHourService;

	private StepExecution stepExecution;

	@Autowired
	FXHourGenerateExecution(FXTickService fxTickService, FXHourService fxHourService) {
		this.fxTickService = fxTickService;
		this.fxHourService = fxHourService;
	}

	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
		// Initialize inserted rows as 0.
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.INSERTED_ROWS + "Hour", 0);
		// Initialize updated rows as 0.
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.UPDATED_ROWS + "Hour", 0);
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
		LocalDateTime currentDatetime = getFirstHourOfMonth(processedMonth);
		// Get end day.
		LocalDateTime endDay = currentDatetime.plusMonths(1);
		// Inserted rows counter.
		int insertedCnt = 0;
		// updated rows counter.
		int updatedCnt = 0;
		// Loop all instruments.
		while (currentDatetime.compareTo(endDay) < 0) {
			logger.debug("The current datetime is {}", currentDatetime);
			// Retrieve hourly data.
			TrnFXHour fxHour = (TrnFXHour) fxTickService.generateFXPeriodData(FXType.HOUR, currencyPair,
					currentDatetime);
			logger.debug("TrnFXHour is {}.", fxHour);
			if (fxHour != null) {
				// Retrieve previous FX Hour data from DB by current key.
				Optional<TrnFXHour> prevOption = fxHourService.findPrevious(fxHour.getTickKey());
				// If exists.
				if (prevOption.isPresent()) {
					TrnFXHour previous = prevOption.get();
					fxHour.setAvOpeningPrice((previous.getAvOpeningPrice() + previous.getAvFinishPrice()) / 2);
					fxHour.setAvFinishPrice((fxHour.getOpeningPrice() + fxHour.getHighPrice() + fxHour.getLowPrice()
							+ fxHour.getFinishPrice()) / 4);
				} else {
					fxHour.setAvOpeningPrice(fxHour.getOpeningPrice());
					fxHour.setAvFinishPrice(fxHour.getFinishPrice());
				}
				// Retrieve FX Hour data from DB by key.
				Optional<TrnFXHour> option = fxHourService.findOne(fxHour.getTickKey());
				// Do delete & insert if exist, or else do insert.
				if (option.isPresent()) {
					// Get the current data.
					TrnFXHour current = option.get();
					// Adapt all prices data.
					current.setOpeningPrice(fxHour.getOpeningPrice());
					current.setHighPrice(fxHour.getHighPrice());
					current.setLowPrice(fxHour.getLowPrice());
					current.setFinishPrice(fxHour.getFinishPrice());
					current.setAvOpeningPrice(fxHour.getAvOpeningPrice());
					current.setAvFinishPrice(fxHour.getAvFinishPrice());
					// Copy all data.
					BeanUtils.copyProperties(current, fxHour);
					logger.debug("The updated data is {}", fxHour);
					// Delete the current weekly data.
					fxHourService.operation(current, OperationMode.DELETE);
					// Insert a new weekly data.
					fxHourService.operation(fxHour, OperationMode.NEW);
					// Count up the updated rows counter.
					updatedCnt++;
				} else {
					logger.debug("The inserted data is {}", fxHour);
					// Insert a new weekly data.
					fxHourService.operation(fxHour, OperationMode.NEW);
					// Count up the inserted rows counter.
					insertedCnt++;
				}
			} else {
				logger.info("No FX Hour generated at {}", currentDatetime);
			}
			// Increment with 1 hour.
			currentDatetime = currentDatetime.plusHours(1);
		}
		// Save updated rows counter into job execution context.
		countUpdatedRows(updatedCnt);
		// Save inserted rows counter into job execution context.
		countInsertedRows(insertedCnt);
		return RepeatStatus.FINISHED;
	}

	private void countInsertedRows(int cnt) {
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.INSERTED_ROWS + "Hour",
				stepExecution.getJobExecution().getExecutionContext().getInt(Constants.INSERTED_ROWS + "Hour", 0) + cnt);
	}

	private void countUpdatedRows(int cnt) {
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.UPDATED_ROWS + "Hour",
				stepExecution.getJobExecution().getExecutionContext().getInt(Constants.UPDATED_ROWS + "Hour", 0) + cnt);
	}

	private LocalDateTime getFirstHourOfMonth(String processedMonth) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse(processedMonth + "01 00:00:00.000", formatter);
		return dateTime.truncatedTo(ChronoUnit.HOURS);
	}
}
