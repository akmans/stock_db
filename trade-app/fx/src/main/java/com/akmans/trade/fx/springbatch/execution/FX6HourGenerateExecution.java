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
import com.akmans.trade.fx.service.FX6HourService;
import com.akmans.trade.fx.service.FXHourService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFX6Hour;

@Component
@StepScope
public class FX6HourGenerateExecution extends StepExecutionListenerSupport implements Tasklet {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FX6HourGenerateExecution.class);

	private FXHourService fxHourService;

	private FX6HourService fx6HourService;

	private StepExecution stepExecution;

	@Autowired
	FX6HourGenerateExecution(FXHourService fxHourService, FX6HourService fx6HourService) {
		this.fxHourService = fxHourService;
		this.fx6HourService = fx6HourService;
	}

	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
		// Initialize inserted rows as 0.
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.INSERTED_ROWS + "6Hour", 0);
		// Initialize updated rows as 0.
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.UPDATED_ROWS + "6Hour", 0);
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
		LocalDateTime currentDatetime = getFirst6HourOfMonth(processedMonth);
		// Get end day.
		LocalDateTime endDay = currentDatetime.plusMonths(1);
		// Inserted rows counter.
		int insertedCnt = 0;
		// updated rows counter.
		int updatedCnt = 0;
		// Loop all instruments.
		while (currentDatetime.compareTo(endDay) < 0) {
			// Retrieve weekly data.
			TrnFX6Hour fx6Hour = (TrnFX6Hour)fxHourService
					.generateFXPeriodData(FXType.SIXHOUR, currencyPair, currentDatetime);
			// Continue to next instrument if no weekly data found.
			if (fx6Hour != null) {
				// Retrieve previous FX Hour data from DB by current key.
				Optional<TrnFX6Hour> prevOption = fx6HourService.findPrevious(fx6Hour.getTickKey());
				// If exists.
				if (prevOption.isPresent()) {
					TrnFX6Hour previous = prevOption.get();
					fx6Hour.setAvOpeningPrice((previous.getAvOpeningPrice() + previous.getAvFinishPrice()) / 2);
					fx6Hour.setAvFinishPrice((fx6Hour.getOpeningPrice() + fx6Hour.getHighPrice() + fx6Hour.getLowPrice()
							+ fx6Hour.getFinishPrice()) / 4);
				} else {
					fx6Hour.setAvOpeningPrice(fx6Hour.getOpeningPrice());
					fx6Hour.setAvFinishPrice(fx6Hour.getFinishPrice());
				}
				// Retrieve FX 6Hour data from DB by key.
				Optional<TrnFX6Hour> option = fx6HourService.findOne(fx6Hour.getTickKey());
				// Do delete & insert if exist, or else do insert.
				if (option.isPresent()) {
					// Get the current data.
					TrnFX6Hour current = option.get();
					// Adapt all prices data.
					current.setOpeningPrice(fx6Hour.getOpeningPrice());
					current.setHighPrice(fx6Hour.getHighPrice());
					current.setLowPrice(fx6Hour.getLowPrice());
					current.setFinishPrice(fx6Hour.getFinishPrice());
					current.setAvOpeningPrice(fx6Hour.getAvOpeningPrice());
					current.setAvFinishPrice(fx6Hour.getAvFinishPrice());
					// Copy all data.
					BeanUtils.copyProperties(current, fx6Hour);
					logger.debug("The updated data is {}", fx6Hour);
					// Delete the current weekly data.
					fx6HourService.operation(current, OperationMode.DELETE);
					// Insert a new weekly data.
					fx6HourService.operation(fx6Hour, OperationMode.NEW);
					// Count up the updated rows counter.
					updatedCnt++;
				} else {
					logger.debug("The inserted data is {}", fx6Hour);
					// Insert a new weekly data.
					fx6HourService.operation(fx6Hour, OperationMode.NEW);
					// Count up the inserted rows counter.
					insertedCnt++;
				}
			} else {
				logger.info("No FX 6Hour generated at {}", currentDatetime);
			}
			// Increment with 6 hour.
			currentDatetime = currentDatetime.plusHours(6);
		}
		// Save updated rows counter into job execution context.
		countUpdatedRows(updatedCnt);
		// Save inserted rows counter into job execution context.
		countInsertedRows(insertedCnt);
		return RepeatStatus.FINISHED;
	}

	private void countInsertedRows(int cnt) {
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.INSERTED_ROWS + "6Hour",
				stepExecution.getJobExecution().getExecutionContext().getInt(Constants.INSERTED_ROWS + "6Hour", 0) + cnt);
	}

	private void countUpdatedRows(int cnt) {
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.UPDATED_ROWS + "6Hour",
				stepExecution.getJobExecution().getExecutionContext().getInt(Constants.UPDATED_ROWS + "6Hour", 0) + cnt);
	}

	private LocalDateTime getFirst6HourOfMonth(String processedMonth) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse(processedMonth + "01 00:00:00.000", formatter);
		return dateTime.truncatedTo(ChronoUnit.HOURS);
	}
}
