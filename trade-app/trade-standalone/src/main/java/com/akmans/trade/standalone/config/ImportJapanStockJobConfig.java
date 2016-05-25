package com.akmans.trade.standalone.config;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStock;
import com.akmans.trade.standalone.dto.CsvJapanStockDto;
import com.akmans.trade.standalone.springbatch.listener.JapanStockJobExecutionListener;
import com.akmans.trade.standalone.springbatch.processors.JapanStockConvertProcessor;
import com.akmans.trade.standalone.springbatch.writers.JapanStockWriter;

public class ImportJapanStockJobConfig {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ImportJapanStockJobConfig.class);

	@Bean
	@StepScope
	public FlatFileItemReader<CsvJapanStockDto> reader(
			@Value("#{jobParameters['applicationDate']}") String applicationDate) {
//		logger.info("applicationDate =" + applicationDate);
		// flat file item reader (using an csv extractor)
		FlatFileItemReader<CsvJapanStockDto> reader = new FlatFileItemReader<CsvJapanStockDto>();
		reader.setLinesToSkip(1);
		reader.setEncoding("Shift-JIS");
		reader.setResource(new ClassPathResource("csv/stocks_" + applicationDate + ".csv"));
		reader.setLineMapper(new DefaultLineMapper<CsvJapanStockDto>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames(new String[] { "code", "name", "market", "openingPrice", "highPrice", "lowPrice",
								"finishPrice", "turnover", "tradingValue" });
					}
				});
				setFieldSetMapper(new BeanWrapperFieldSetMapper<CsvJapanStockDto>() {
					{
						setTargetType(CsvJapanStockDto.class);
					}
				});
			}
		});
		return reader;
	}

/*	@Bean
	@StepScope
	public ItemProcessor<CsvJapanStockDto, CsvJapanStockDto> processor1() {
		return new JapanStockValidateProcessor();
	}
*/
	@Bean
	@StepScope
	public ItemProcessor<CsvJapanStockDto, TrnJapanStock> processor(
			@Value("#{jobParameters['applicationDate']}") String applicationDate) {
		return new JapanStockConvertProcessor(applicationDate);
	}

	@Bean
	public ItemWriter<TrnJapanStock> writer() {
		return new JapanStockWriter();
	}

	@Bean
	public Job importJapanStockJob(JobBuilderFactory jobs, Step step, JapanStockJobExecutionListener listener) {
//		return jobs.get("importJapanStockJob").start(step).listener(listener).build();
		return jobs.get("importJapanStockJob").start(step).build();
	}

	@Bean
	public Step step(StepBuilderFactory stepBuilderFactory, ItemReader<CsvJapanStockDto> reader,
			ItemWriter<TrnJapanStock> writer, ItemProcessor<CsvJapanStockDto, TrnJapanStock> processor) {
		// logger.info("applicationDate =" + applicationDate);
		return stepBuilderFactory.get("step").<CsvJapanStockDto, TrnJapanStock> chunk(500).reader(reader)
				.processor(processor).writer(writer).build();
	}

/*	@Bean
	public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<CsvJapanStockDto> reader,
			ItemProcessor<CsvJapanStockDto, CsvJapanStockDto> processor1) {
		// logger.info("applicationDate =" + applicationDate);
		return stepBuilderFactory.get("step1").<CsvJapanStockDto, CsvJapanStockDto> chunk(500).reader(reader)
				.processor(processor1).build();
	}*/
}
