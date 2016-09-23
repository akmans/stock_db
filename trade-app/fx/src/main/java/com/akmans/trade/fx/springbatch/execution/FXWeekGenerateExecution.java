package com.akmans.trade.fx.springbatch.execution;

import java.time.LocalDateTime;
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
import com.akmans.trade.fx.service.FXWeekService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXWeek;

@Component
@StepScope
public class FXWeekGenerateExecution extends StepExecutionListenerSupport implements Tasklet {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXWeekGenerateExecution.class);

	private FXDayService fxDayService;

	private FXWeekService fxWeekService;

	private StepExecution stepExecution;

	@Autowired
	FXWeekGenerateExecution(FXDayService fxDayService, FXWeekService fxWeekService) {
		this.fxDayService = fxDayService;
		this.fxWeekService = fxWeekService;
	}

	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
		// Initialize inserted rows as 0.
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.INSERTED_ROWS + "Week", 0);
		// Initialize updated rows as 0.
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.UPDATED_ROWS + "Week", 0);
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
		LocalDateTime firstDatetime = getFirstDayOfMonth(processedMonth);
		// Date of Sunday, the first day of week.
		LocalDateTime currentDatetime = firstDatetime.minusDays(firstDatetime.getDayOfWeek().getValue());
		// Get end day.
		LocalDateTime endDay = firstDatetime.plusMonths(1);
		logger.debug("The begin Day is {}", currentDatetime);
		logger.debug("The endDay is {}", endDay);
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
			if (fxWeek != null) {
				// Retrieve previous FX Week data from DB by current key.
				Optional<TrnFXWeek> prevOption = fxWeekService.findPrevious(fxWeek.getTickKey());
				// If exists.
				if (prevOption.isPresent()) {
					TrnFXWeek previous = prevOption.get();
					fxWeek.setAvOpeningPrice((previous.getAvOpeningPrice() + previous.getAvFinishPrice()) / 2);
					fxWeek.setAvFinishPrice((fxWeek.getOpeningPrice() + fxWeek.getHighPrice() + fxWeek.getLowPrice()
							+ fxWeek.getFinishPrice()) / 4);
				} else {
					fxWeek.setAvOpeningPrice(fxWeek.getOpeningPrice());
					fxWeek.setAvFinishPrice(fxWeek.getFinishPrice());
				}
				// Retrieve FX Week data from DB by key.
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
					current.setAvOpeningPrice(fxWeek.getAvOpeningPrice());
					current.setAvFinishPrice(fxWeek.getAvFinishPrice());
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
			} else {
				logger.info("No FX Week generated at {}", currentDatetime);
			}
			// Increment with 1 day.
			currentDatetime = currentDatetime.plusWeeks(1);
		}
		// Save updated rows counter into job execution context.
		countUpdatedRows(updatedCnt);
		// Save inserted rows counter into job execution context.
		countInsertedRows(insertedCnt);
		return RepeatStatus.FINISHED;
	}

	private void countInsertedRows(int cnt) {
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.INSERTED_ROWS + "Week",
				stepExecution.getJobExecution().getExecutionContext().getInt(Constants.INSERTED_ROWS + "Week", 0) + cnt);
	}

	private void countUpdatedRows(int cnt) {
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.UPDATED_ROWS + "Week",
				stepExecution.getJobExecution().getExecutionContext().getInt(Constants.UPDATED_ROWS + "Week", 0) + cnt);
	}

	private LocalDateTime getFirstDayOfMonth(String processedMonth) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
		LocalDateTime dateTime = LocalDateTime.parse(processedMonth + "01 00:00:00.000", formatter);
		return dateTime;
	}
}
