package com.akmans.trade.fx.springbatch.writer;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.akmans.trade.core.Constants;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.fx.model.FXTick;
import com.akmans.trade.fx.service.FXTickService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXTick;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;

@Component
@StepScope
public class FXHistoryTickWriter implements ItemWriter<FXTick>, StepExecutionListener {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXHistoryTickWriter.class);

	@Autowired
	private FXTickService fxTickService;

	private StepExecution stepExecution;

	@Override
	public void write(List<? extends FXTick> ticks) throws Exception {
		for(FXTick tick : ticks) {
			logger.debug("FXTick = {}", tick);
			FXTickKey key = new FXTickKey();
			key.setCurrencyPair(tick.getCurrencyPair());
			key.setRegistDate(tick.getRegistDate());
			if (fxTickService.exist(key)) {
				TrnFXTick origin = fxTickService.findOne(key);
				origin.setBidPrice(tick.getBidPrice());
				origin.setAskPrice(tick.getAskPrice());
				origin.setMidPrice(tick.getMidPrice());
				fxTickService.operation(origin, OperationMode.EDIT);
				countUpdatedRows();
			} else {
				TrnFXTick newTick = new TrnFXTick();
				newTick.setTickKey(key);
				newTick.setBidPrice(tick.getBidPrice());
				newTick.setAskPrice(tick.getAskPrice());
				newTick.setMidPrice(tick.getMidPrice());
				fxTickService.operation(newTick, OperationMode.NEW);
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
