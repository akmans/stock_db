package com.akmans.trade.standalone.springbatch.writers;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.service.JapanStockService;
import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStock;

public class JapanStockWriter implements ItemWriter<TrnJapanStock> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(JapanStockWriter.class);

	@Autowired
	private JapanStockService japanStockService;

	@Override
	public void write(List<? extends TrnJapanStock> stocks) throws Exception {
		for(TrnJapanStock stock : stocks) {
			logger.debug("TrnJapanStock = {}", stock);
			if (japanStockService.exist(stock.getJapanStockKey())) {
				TrnJapanStock origin = japanStockService.findOne(stock.getJapanStockKey());
				origin.setOpeningPrice(stock.getOpeningPrice());
				origin.setHighPrice(stock.getHighPrice());
				origin.setLowPrice(stock.getLowPrice());
				origin.setFinishPrice(stock.getFinishPrice());
				origin.setTurnover(stock.getTurnover());
				origin.setTradingValue(stock.getTradingValue());
				japanStockService.operation(origin, OperationMode.EDIT);
			} else {
				japanStockService.operation(stock, OperationMode.NEW);
			}
		}
	}

}
