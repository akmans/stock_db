package com.akmans.trade.fx.springbatch.execution;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

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
import com.akmans.trade.core.enums.FXType;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.fx.service.FXHourService;
import com.akmans.trade.fx.service.FXTickService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXHour;

@Component
public class FXHourGenerateExecution extends StepExecutionListenerSupport implements Tasklet {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXHourGenerateExecution.class);

	private String currencyPair;

	private String processedMonth;

	@Autowired
	private FXTickService fxTickService;

	@Autowired
	private FXHourService fxHourService;

	private StepExecution stepExecution;

	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
		// Initialize inserted rows as 0.
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.INSERTED_ROWS, 0);
		// Initialize updated rows as 0.
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.UPDATED_ROWS, 0);
		JobParameters jobParameters = stepExecution.getJobParameters();
		// Get currency pair from job parameters.
		currencyPair = jobParameters.getString("currencyPair");
		// Get processed month from job parameters.
		processedMonth = jobParameters.getString("processedMonth");
		logger.debug("The currencyPair is {}", currencyPair);
		logger.debug("The processedMonth is {}", processedMonth);
	}

	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		// Get first hour.
		ZonedDateTime currentDatetime = getFirstHourOfMonth(processedMonth);
		// Get end day.
		ZonedDateTime endDay = currentDatetime.plusMonths(1);
		// Inserted rows counter.
		int insertedCnt = 0;
		// updated rows counter.
		int updatedCnt = 0;
		// Loop all instruments.
		while (currentDatetime.compareTo(endDay) < 0) {
			// Retrieve weekly data.
			TrnFXHour fxHour = (TrnFXHour)fxTickService
					.generateFXPeriodData(FXType.HOUR, currencyPair, currentDatetime);
			logger.debug("TrnFXHour is {}.", fxHour);
			// Continue to next instrument if no weekly data found.
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
			// Mark processed.
//			int markedCnt = fxTickService.markProcessed(currencyPair, currentDatetime);
//			logger.debug("{} records were processed.", markedCnt);
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
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.INSERTED_ROWS,
				stepExecution.getJobExecution().getExecutionContext().getInt(Constants.INSERTED_ROWS, 0) + cnt);
	}

	private void countUpdatedRows(int cnt) {
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.UPDATED_ROWS,
				stepExecution.getJobExecution().getExecutionContext().getInt(Constants.UPDATED_ROWS, 0) + cnt);
	}

	private ZonedDateTime getFirstHourOfMonth(String processedMonth) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse(processedMonth + "01 00:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		logger.debug("The first DateTime is {}.", result.truncatedTo(ChronoUnit.HOURS));
		return result.truncatedTo(ChronoUnit.HOURS);
	}
}
