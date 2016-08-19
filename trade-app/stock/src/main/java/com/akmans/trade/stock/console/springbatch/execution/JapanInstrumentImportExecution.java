package com.akmans.trade.stock.console.springbatch.execution;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.javafunk.excelparser.SheetParser;
import org.javafunk.excelparser.exception.ExcelParsingException;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import com.akmans.trade.core.enums.Onboard;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.stock.service.InstrumentService;
import com.akmans.trade.stock.service.MarketService;
import com.akmans.trade.core.service.MessageService;
import com.akmans.trade.stock.service.ScaleService;
import com.akmans.trade.stock.service.Sector17Service;
import com.akmans.trade.stock.service.Sector33Service;
import com.akmans.trade.stock.springdata.jpa.entities.MstInstrument;
import com.akmans.trade.stock.springdata.jpa.entities.MstMarket;
import com.akmans.trade.stock.springdata.jpa.entities.MstScale;
import com.akmans.trade.stock.springdata.jpa.entities.MstSector17;
import com.akmans.trade.stock.springdata.jpa.entities.MstSector33;
import com.akmans.trade.stock.dto.ExcelInstrumentDto;

@Component
public class JapanInstrumentImportExecution implements Tasklet {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(JapanInstrumentImportExecution.class);

	@Autowired
	private MessageService messageService;

	@Autowired
	private InstrumentService instrumentService;

	@Autowired
	private ScaleService scaleService;

	@Autowired
	private MarketService marketService;

	@Autowired
	private Sector17Service sector17Service;

	@Autowired
	private Sector33Service sector33Service;

	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		// Get the sheet using POI API.
		String sheetName = "Sheet1";
		SheetParser parser = new SheetParser();
		String fullFilePath = (String) chunkContext.getStepContext().getStepExecution().getJobExecution()
				.getExecutionContext().get("targetFile");
		logger.debug("fullFilePath = {}", fullFilePath);
		InputStream inputStream = new FileSystemResource(fullFilePath).getInputStream();
		Sheet sheet = new HSSFWorkbook(inputStream).getSheet(sheetName);

		// Error handler
		Consumer<ExcelParsingException> errorHandler = (error) -> {
			throw error;
		};

		// Invoke the Sheet parser.
		List<ExcelInstrumentDto> entityList = parser.createEntity(sheet, ExcelInstrumentDto.class, errorHandler);
		Set<String> hashSet = new HashSet<String>();
		for (ExcelInstrumentDto dto : entityList) {
			if (dto.getDate() != null && dto.getDate().length() > 0 && !dto.getMarketName().equals("-")) {
				hashSet.add(dto.getMarketName());
			}
		}
		Iterator<String> itr = hashSet.iterator();
		while (itr.hasNext()) {
			String str = (String) itr.next();
			MstMarket market = marketService.findByName(str.trim());
			if (market == null) {
				// market = new MstMarket();
				// market.setName(str);
				// marketService.operation(market, OperationMode.NEW);
				throw new TradeException(
						messageService.getMessage("core.service.market.record.notfound.by.name", str.trim()));
			}
		}
		for (ExcelInstrumentDto dto : entityList) {
			if (dto.getDate() != null && dto.getDate().length() > 0) {
				// logger.debug("dto = {}", dto);
				OperationMode om = OperationMode.EDIT;
				// Get current value from database.
				MstInstrument instrument = instrumentService.findOneEager(Long.valueOf(dto.getCode()));
				// Mark new if not found.
				if (instrument == null || instrument.getCode() == null) {
					instrument = new MstInstrument();
					om = OperationMode.NEW;
				}

				// Set instrument code.
				instrument.setCode(Long.valueOf(dto.getCode()));

				// Set instrument name.
				instrument.setName(dto.getName());

				// Set market if not equals to "-".
				if (!dto.getMarketName().equals("-")) {
					// Current market is null or not equals to value of server.
					if (instrument.getMarket() == null
							|| !instrument.getMarket().getName().equals(dto.getMarketName())) {
						// Find the market.
						MstMarket market = marketService.findByName(dto.getMarketName().trim());
						// Set market.
						instrument.setMarket(market);
					}
				} else {
					// Market is null.
					instrument.setMarket(null);
				}

				// Set scale if not equals to "-".
				if (!dto.getScale().equals("-")) {
					// Current scale is null or not equals to value of server.
					if (instrument.getScale() == null
							|| instrument.getScale().getCode() != Integer.valueOf(dto.getScale())) {
						// Find the scale.
						MstScale scale = scaleService.findOne(Integer.valueOf(dto.getScale()));
						// Set market.
						instrument.setScale(scale);
					}
				} else {
					// Scale is null.
					instrument.setScale(null);
				}

				// Set sector17 if not equals to "-".
				if (!dto.getSector17().equals("-")) {
					// Current sector17 is null or not equals to value of
					// server.
					if (instrument.getSector17() == null
							|| instrument.getSector17().getCode() != Integer.valueOf(dto.getSector17())) {
						// Find the sector17.
						MstSector17 sector17 = sector17Service.findOne(Integer.valueOf(dto.getSector17()));
						// Set sector17.
						instrument.setSector17(sector17);
					}
				} else {
					// Sector17 is null.
					instrument.setSector17(null);
				}

				// Set sector33 if not equals to "-".
				if (!dto.getSector33().equals("-")) {
					// Current sector33 is null or not equals to value of
					// server.
					if (instrument.getSector33() == null
							|| instrument.getSector33().getCode() != Integer.valueOf(dto.getSector33())) {
						// Find the sector33.
						MstSector33 sector33 = sector33Service.findOne(Integer.valueOf(dto.getSector33()));
						// Set sector33.
						instrument.setSector33(sector33);
					}
				} else {
					// Sector33 is null.
					instrument.setSector33(null);
				}

				// Set on board false to ON.
				instrument.setOnboard(Onboard.ON.getValue());

				// Do database operation.
				instrumentService.operation(instrument, om);
			}
		}
		return RepeatStatus.FINISHED;
	}
}
