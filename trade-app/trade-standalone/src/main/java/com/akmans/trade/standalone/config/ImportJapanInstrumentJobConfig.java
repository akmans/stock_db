package com.akmans.trade.standalone.config;

import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.javafunk.excelparser.SheetParser;
import org.javafunk.excelparser.exception.ExcelParsingException;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import com.akmans.trade.core.utils.FileUtil;
import com.akmans.trade.standalone.dto.ExcelInstrumentDto;

public class ImportJapanInstrumentJobConfig {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ImportJapanInstrumentJobConfig.class);

	@Bean
	public Job importJapanInstrumentJob(JobBuilderFactory jobs, Step step1, Step step2) {
		return jobs.get("importJapanInstrumentJob").start(step1).next(step2).build();
	}

	@Bean
	public Step step1(StepBuilderFactory stepBuilderFactory) {
		// logger.info("applicationDate =" + applicationDate);
		return stepBuilderFactory.get("step1").tasklet(new Step1Execution()).build();
	}

	@Bean
	public Step step2(StepBuilderFactory stepBuilderFactory) {
		// logger.info("applicationDate =" + applicationDate);
		return stepBuilderFactory.get("step2").tasklet(new Step2Execution()).build();
	}

	private class Step1Execution implements Tasklet {
		public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
			String sourceUrl = "http://www.jpx.co.jp/markets/statistics-equities/misc/tvdivq0000001vg2-att/data_j.xls";
			String targetDirectory = "/Users/owner99/Desktop/FX";
			FileUtil.download(sourceUrl, targetDirectory);
			return RepeatStatus.FINISHED;
		}
	}

	private class Step2Execution implements Tasklet {
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
			for (ExcelInstrumentDto dto : entityList) {
				if (dto.getDate() != null && dto.getDate().length() > 0) {
					logger.info("data = {}", dto);
				}
			}
			return RepeatStatus.FINISHED;
		}
	}
}
