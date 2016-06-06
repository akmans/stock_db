package com.akmans.trade.standalone.springbatch.processors;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.InstrumentService;
import com.akmans.trade.core.service.JapanStockService;
import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStock;
import com.akmans.trade.core.springdata.jpa.keys.JapanStockKey;
import com.akmans.trade.core.utils.DateUtil;
import com.akmans.trade.standalone.dto.CsvJapanStockDto;

@Component
public class JapanStockConvertProcessor
		implements ItemProcessor<CsvJapanStockDto, TrnJapanStock>, StepExecutionListener {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(JapanStockConvertProcessor.class);

	private Date applicationDate;

	@Autowired
	private InstrumentService instrumentService;

	@Autowired
	private JapanStockService japanStockService;

	private StepExecution stepExecution;

	public TrnJapanStock process(CsvJapanStockDto item) throws Exception {
		// logger.warn("CsvJapanStockDto = {}", item);
		countProcessedRows();
		applicationDate = stepExecution.getJobExecution().getJobParameters().getDate("processDate");
		// Skip null or empty record.
		if (item == null || item.getCode() == null) {
			logger.warn("The item is empty! item = {}", item);
			countSkippedRows();
			return null;
		}

		// Skip record not match those in MstInstrument.
		try {
			// Process stock code.
			String codes[] = item.getCode().split("-");
			instrumentService.findOne(Long.valueOf(codes[0]));
		} catch (TradeException te) {
			logger.warn("The item is {}", item);
			logger.warn(te.getMessage());
			countSkippedRows();
			return null;
		}

		TrnJapanStock stock = new TrnJapanStock();
		// Process stock code.
		String codes[] = item.getCode().split("-");
		// Primary key.
		JapanStockKey japanStockKey = new JapanStockKey();
		japanStockKey.setCode(Integer.valueOf(codes[0]));
		// japanStockKey.setRegistDate(convertDate(applicationDate));
		japanStockKey.setRegistDate(applicationDate);
		stock.setJapanStockKey(japanStockKey);
		// Skip data that price is empty.
		if (item.getOpeningPrice().isEmpty()) {
			countSkippedRows();
			return null;
		}
		// Skip data that already loaded if market is not TOKYO.
		if (!"T".equals(codes[1]) && japanStockService.exist(japanStockKey)) {
			TrnJapanStock japanStock = japanStockService.findOne(japanStockKey);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
			String now = formatter.format(ZonedDateTime.now());
			String origin = formatter.format(japanStock.getUpdatedDate());
			// Skip processing if updated date is equal to current date.
			if (now.equals(origin)) {
				countSkippedRows();
				return null;
			}
		}
		// logger.debug("CsvJapanStockDto = {}", item);
		stock.setOpeningPrice(
				Integer.valueOf(item.getOpeningPrice().substring(0, item.getOpeningPrice().lastIndexOf("."))));
		stock.setHighPrice(Integer.valueOf(item.getHighPrice().substring(0, item.getHighPrice().lastIndexOf("."))));
		stock.setLowPrice(Integer.valueOf(item.getLowPrice().substring(0, item.getLowPrice().lastIndexOf("."))));
		stock.setFinishPrice(
				Integer.valueOf(item.getFinishPrice().substring(0, item.getFinishPrice().lastIndexOf("."))));
		stock.setTurnover(Long.valueOf(item.getTurnover()));
		stock.setTradingValue(Long.valueOf(item.getTradingValue()));
		// logger.info("stock = {}", stock);
		// System.out.println("Processing..." + item);
		return stock;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
		stepExecution.getJobExecution().getExecutionContext().putInt("skippedRows", 0);
		stepExecution.getJobExecution().getExecutionContext().putInt("processedRows", 0);
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub
		return null;
	}

	private void countSkippedRows() {
		stepExecution.getJobExecution().getExecutionContext().putInt("skippedRows",
				stepExecution.getJobExecution().getExecutionContext().getInt("skippedRows", 0) + 1);
	}

	private void countProcessedRows() {
		stepExecution.getJobExecution().getExecutionContext().putInt("processedRows",
				stepExecution.getJobExecution().getExecutionContext().getInt("processedRows", 0) + 1);
	}
}
