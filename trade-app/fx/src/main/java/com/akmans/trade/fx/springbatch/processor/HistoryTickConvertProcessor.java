package com.akmans.trade.fx.springbatch.processor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.akmans.trade.core.Constants;
import com.akmans.trade.core.enums.CurrencyPair;
import com.akmans.trade.fx.dto.CsvHistoryTickDto;
import com.akmans.trade.fx.model.FXTick;

@Component
public class HistoryTickConvertProcessor implements ItemProcessor<CsvHistoryTickDto, FXTick>, StepExecutionListener {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(HistoryTickConvertProcessor.class);

	private StepExecution stepExecution;

	public FXTick process(CsvHistoryTickDto item) {
		// process flag.
		boolean failed = false;
		// logger.warn("CsvJapanStockDto = {}", item);
		countProcessedRows();
		// new tick.
		FXTick tick = new FXTick();
		// Skip null or empty record.
		if (item == null) {
			logger.warn("The item is empty! item = {}", item);
			failed = true;
		} else {
			// process currency pair.
			if (!StringUtils.isBlank(item.getCurrencyPair())) {
				// Set currency pair.
				tick.setCurrencyPair(item.getCurrencyPair().replace("/", "").toLowerCase());
				if (CurrencyPair.get(tick.getCurrencyPair()) == null) {
					logger.warn("The currency pair is invalid! item = {}", item);
					failed = true;
				}
			} else {
				logger.warn("The currency pair is empty! item = {}", item);
				failed = true;
			}

			// process regist date.
			if (!StringUtils.isBlank(item.getRegistDate())) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");
				LocalDateTime dateTime = LocalDateTime.parse(item.getRegistDate(), formatter);
				ZonedDateTime result = dateTime.atZone(ZoneId.of("GMT"));
				tick.setRegistDate(result);
			} else {
				logger.warn("The regist date is empty! item = {}", item);
				failed = true;
			}

			// process bid price.
			if (!StringUtils.isBlank(item.getBidPrice())) {
				// Set bid price.
				tick.setBidPrice(Double.parseDouble(item.getBidPrice()));
			} else {
				logger.warn("The bid price is empty! item = {}", item);
				failed = true;
			}

			// process ask price.
			if (!StringUtils.isBlank(item.getAskPrice())) {
				// Set ask price.
				tick.setAskPrice(Double.parseDouble(item.getAskPrice()));
			} else {
				logger.warn("The ask price is empty! item = {}", item);
				failed = true;
			}

			// process mid price.
			if (!failed) {
				// Set mid price.
				tick.setMidPrice((tick.getBidPrice() + tick.getAskPrice()) / 2);
			}
		}

		// process when failed.
		if (failed) {
			// count up skipped rows.
			countSkippedRows();
			return null;
		}
		return tick;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.SKIPPED_ROWS, 0);
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.PROCESSED_ROWS, 0);
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub
		return null;
	}

	private void countSkippedRows() {
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.SKIPPED_ROWS,
				stepExecution.getJobExecution().getExecutionContext().getInt(Constants.SKIPPED_ROWS, 0) + 1);
	}

	private void countProcessedRows() {
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.PROCESSED_ROWS,
				stepExecution.getJobExecution().getExecutionContext().getInt(Constants.PROCESSED_ROWS, 0) + 1);
	}
}
