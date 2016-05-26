package com.akmans.trade.standalone.springbatch.processors;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.InstrumentService;
import com.akmans.trade.core.service.JapanStockService;
import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStock;
import com.akmans.trade.core.springdata.jpa.keys.JapanStockKey;
import com.akmans.trade.standalone.dto.CsvJapanStockDto;

@Component
public class JapanStockConvertProcessor implements ItemProcessor<CsvJapanStockDto, TrnJapanStock>, StepExecutionListener {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(JapanStockConvertProcessor.class);

//	private String applicationDate;
	private Date applicationDate;

	@Autowired
	private InstrumentService instrumentService;

	@Autowired
	private JapanStockService japanStockService;

	private StepExecution stepExecution;

/*	@BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

	public JapanStockConvertProcessor(Date applicationDate) {
		this.applicationDate = applicationDate;
	}
*/
	public TrnJapanStock process(CsvJapanStockDto item) throws Exception {
		// logger.warn("CsvJapanStockDto = {}", item);

		applicationDate = stepExecution.getJobExecution().getJobParameters().getDate("processDate");
		// Skip null or empty record.
		if (item == null || item.getCode() == null) {
			logger.warn("The item is empty! item = {}", item);
			return null;
		}

		// Skip record not match those in MstInstrument.
		try {
			// Process stock code.
			String codes[] = item.getCode().split("-");
			instrumentService.findOne(Long.valueOf(codes[0]));
		} catch(TradeException te) {
			logger.warn("The item is {}", item);
			logger.warn(te.getMessage());
			return null;
		}

		TrnJapanStock stock = new TrnJapanStock();
		// Process stock code.
		String codes[] = item.getCode().split("-");
		// Primary key.
		JapanStockKey japanStockKey = new JapanStockKey();
		japanStockKey.setCode(Integer.valueOf(codes[0]));
//		japanStockKey.setRegistDate(convertDate(applicationDate));
		japanStockKey.setRegistDate(applicationDate);
		stock.setJapanStockKey(japanStockKey);
		// Skip data that price is empty.
		if (item.getOpeningPrice().isEmpty()) {
			return null;
		}
		// Skip data that already loaded if market is not TOKYO.
		if (!"T".equals(codes[1]) && japanStockService.exist(japanStockKey)) {
			return null;
		}
//		logger.debug("CsvJapanStockDto = {}", item);
		stock.setOpeningPrice(
				Integer.valueOf(item.getOpeningPrice().substring(0, item.getOpeningPrice().lastIndexOf("."))));
		stock.setHighPrice(Integer.valueOf(item.getHighPrice().substring(0, item.getHighPrice().lastIndexOf("."))));
		stock.setLowPrice(Integer.valueOf(item.getLowPrice().substring(0, item.getLowPrice().lastIndexOf("."))));
		stock.setFinishPrice(
				Integer.valueOf(item.getFinishPrice().substring(0, item.getFinishPrice().lastIndexOf("."))));
		stock.setTurnover(Long.valueOf(item.getTurnover()));
		stock.setTradingValue(Long.valueOf(item.getTradingValue()));
//		logger.info("stock = {}", stock);
		// System.out.println("Processing..." + item);
		return stock;
	}

	private Date convertDate(String date) throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date rt = null;
		rt = df.parse(date);
		return rt;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
		
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub
		return null;
	}
}
