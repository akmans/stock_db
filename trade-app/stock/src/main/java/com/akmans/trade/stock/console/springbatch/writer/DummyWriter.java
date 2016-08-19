package com.akmans.trade.stock.console.springbatch.writer;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import com.akmans.trade.stock.springdata.jpa.entities.TrnJapanStock;

public class DummyWriter implements ItemWriter<TrnJapanStock> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(DummyWriter.class);

	@Override
	public void write(List<? extends TrnJapanStock> stock) throws Exception {
//		logger.info("TrnJapanStock = {}", stock);
	}

}
