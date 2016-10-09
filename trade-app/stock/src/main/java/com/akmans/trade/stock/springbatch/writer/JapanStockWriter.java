package com.akmans.trade.stock.springbatch.writer;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.akmans.trade.core.Constants;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.stock.service.JapanStockService;
import com.akmans.trade.stock.springdata.jpa.entities.TrnJapanStock;

@Component
public class JapanStockWriter implements ItemWriter<TrnJapanStock>, StepExecutionListener {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(JapanStockWriter.class);

	@Autowired
	private JapanStockService japanStockService;

	private StepExecution stepExecution;

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
				countUpdatedRows();
			} else {
				japanStockService.operation(stock, OperationMode.NEW);
				countInsertedRows();
			}
		}
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.INSERTED_ROWS, 0);
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.UPDATED_ROWS, 0);
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub
		return null;
	}

	private void countInsertedRows() {
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.INSERTED_ROWS,
				stepExecution.getJobExecution().getExecutionContext().getInt(Constants.INSERTED_ROWS, 0) + 1);
	}

	private void countUpdatedRows() {
		stepExecution.getJobExecution().getExecutionContext().putInt(Constants.UPDATED_ROWS,
				stepExecution.getJobExecution().getExecutionContext().getInt(Constants.UPDATED_ROWS, 0) + 1);
	}
}
