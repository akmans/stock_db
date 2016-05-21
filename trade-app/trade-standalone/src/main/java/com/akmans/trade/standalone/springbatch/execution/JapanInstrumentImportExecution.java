package com.akmans.trade.standalone.springbatch.execution;

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
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.akmans.trade.core.enums.Onboard;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.service.InstrumentService;
import com.akmans.trade.core.service.MarketService;
import com.akmans.trade.core.service.ScaleService;
import com.akmans.trade.core.service.Sector17Service;
import com.akmans.trade.core.service.Sector33Service;
import com.akmans.trade.core.springdata.jpa.entities.MstInstrument;
import com.akmans.trade.core.springdata.jpa.entities.MstMarket;
import com.akmans.trade.core.springdata.jpa.entities.MstScale;
import com.akmans.trade.core.springdata.jpa.entities.MstSector17;
import com.akmans.trade.core.springdata.jpa.entities.MstSector33;
import com.akmans.trade.standalone.dto.ExcelInstrumentDto;

@Component
public class JapanInstrumentImportExecution implements Tasklet {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(JapanInstrumentImportExecution.class);

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
		InputStream inputStream = new ClassPathResource("csv/data_j.xls").getInputStream();
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
	        	 market = new MstMarket();// TODO make Code serial
	        	 market.setName(str);
	        	 marketService.operation(market, OperationMode.NEW);
	         }
	      }
		for (ExcelInstrumentDto dto : entityList) {
			if (dto.getDate() != null && dto.getDate().length() > 0) {
//				logger.debug("dto = {}", dto);
				OperationMode om = OperationMode.EDIT;
				MstInstrument instrument = instrumentService.findOneEager(Long.valueOf(dto.getCode()));
				if (instrument == null || instrument.getCode() == null) {
					instrument = new MstInstrument();
					om = OperationMode.NEW;
				}
				instrument.setCode(Long.valueOf(dto.getCode()));
				instrument.setName(dto.getName());
				if (!dto.getMarketName().equals("-")) {
					MstMarket market = marketService.findByName(dto.getMarketName().trim());
					instrument.setMarket(market);
				} else {
					instrument.setMarket(null);
				}
				if (!dto.getScale().equals("-")) {
					MstScale scale = scaleService.findOne(Integer.valueOf(dto.getScale()));
					instrument.setScale(scale);
				} else {
					instrument.setScale(null);
				}
				if (!dto.getSector17().equals("-")) {
					MstSector17 sector17 = sector17Service.findOne(Integer.valueOf(dto.getSector17()));
					instrument.setSector17(sector17);
				} else {
					instrument.setSector17(null);
				}
				if (!dto.getSector33().equals("-")) {
					MstSector33 sector33 = sector33Service.findOne(Integer.valueOf(dto.getSector33()));
					instrument.setSector33(sector33);
				} else {
					instrument.setSector33(null);
				}
				instrument.setOnboard(Onboard.ON.getValue());
//				logger.debug("instrument = {}", instrument);
				instrumentService.operation(instrument, om);
			}
		}
		return RepeatStatus.FINISHED;
	}

}
