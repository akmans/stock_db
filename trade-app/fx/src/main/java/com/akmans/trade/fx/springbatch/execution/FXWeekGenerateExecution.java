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
import com.akmans.trade.fx.service.FXDayService;
import com.akmans.trade.fx.service.FXHourService;
import com.akmans.trade.fx.service.FXWeekService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXDay;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXWeek;

@Component
public class FXWeekGenerateExecution extends StepExecutionListenerSupport implements Tasklet {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXWeekGenerateExecution.class);

	private String currencyPair;

	private String processedMonth;

	@Autowired
	private FXDayService fxDayService;

	@Autowired
	private FXWeekService fxWeekService;

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
			TrnFXWeek fxWeek = (TrnFXWeek)fxDayService
					.generateFXPeriodData(FXType.WEEK, currencyPair, currentDatetime);
			// Continue to next instrument if no weekly data found.
			if (fxWeek == null) {
				continue;
			}
			Optional<TrnFXWeek> option = fxWeekService.findOne(fxWeek.getTickKey());
			// Do delete & insert if exist, or else do insert.
			if (option.isPresent()) {
				// Get the current data.
				TrnFXWeek current = option.get();
				// Adapt all prices data.
				current.setOpeningPrice(fxWeek.getOpeningPrice());
				current.setHighPrice(fxWeek.getHighPrice());
				current.setLowPrice(fxWeek.getLowPrice());
				current.setFinishPrice(fxWeek.getFinishPrice());
				// Copy all data.
				BeanUtils.copyProperties(current, fxWeek);
				logger.debug("The updated data is {}", fxWeek);
				// Delete the current weekly data.
				fxWeekService.operation(current, OperationMode.DELETE);
				// Insert a new weekly data.
				fxWeekService.operation(fxWeek, OperationMode.NEW);
				// Count up the updated rows counter.
				updatedCnt++;
			} else {
				logger.debug("The inserted data is {}", fxWeek);
				// Insert a new weekly data.
				fxWeekService.operation(fxWeek, OperationMode.NEW);
				// Count up the inserted rows counter.
				insertedCnt++;
			}
			// Increment with 1 day.
			currentDatetime.plusWeeks(1);
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

	private ZonedDateTime getFirstDayOfMonth(String processedMonth) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse(processedMonth + "01 00:00:00.000", formatter);
		ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
		return result.truncatedTo(ChronoUnit.WEEKS);
	}
}
