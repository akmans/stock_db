package com.akmans.trade.standalone.springbatch.processors;

import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.InstrumentService;
import com.akmans.trade.standalone.dto.CsvJapanStockDto;

public class JapanStockValidateProcessor implements ItemProcessor<CsvJapanStockDto, CsvJapanStockDto> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(JapanStockValidateProcessor.class);

	@Autowired
	private InstrumentService instrumentService;

	public CsvJapanStockDto process(CsvJapanStockDto item) throws Exception {
		// Skip processing when empty.
		if (item == null || item.getCode() == null) {
			logger.warn("The item is empty! item = {}", item);
			return null;
		}

		try {
			// Process stock code.
			String codes[] = item.getCode().split("-");
			instrumentService.findOne(Long.valueOf(codes[0]));
		} catch(TradeException te) {
			logger.error(te.getMessage());
		}
		// No subsequent process needed.
		return null;
	}
}
