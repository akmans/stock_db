package com.akmans.trade.standalone.springbatch.processors;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStock;
import com.akmans.trade.core.springdata.jpa.keys.JapanStockKey;
import com.akmans.trade.standalone.dto.CsvJapanStockDto;

public class JapanStockConvertProcessor implements ItemProcessor<CsvJapanStockDto, TrnJapanStock> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(JapanStockConvertProcessor.class);

	private String applicationDate;

	public JapanStockConvertProcessor(String applicationDate) {
		this.applicationDate = applicationDate;
	}

	public TrnJapanStock process(CsvJapanStockDto item) throws Exception {
		// logger.warn("CsvJapanStockDto = {}", item);

		if (item == null || item.getCode() == null) {
			logger.warn("The item is empty! item = {}", item);
			return null;
		}

		TrnJapanStock stock = new TrnJapanStock();
		// Process stock code.
		String codes[] = item.getCode().split("-");
		// Primary key.
		JapanStockKey japanStockKey = new JapanStockKey();
		japanStockKey.setCode(Integer.valueOf(codes[0]));
		japanStockKey.setRegistDate(convertDate(applicationDate));
		stock.setJapanStockKey(japanStockKey);
		// Omit data that price is empty or market is not TOKYO.
		if (item.getOpeningPrice().isEmpty() || !"T".equals(codes[1])) {
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
}