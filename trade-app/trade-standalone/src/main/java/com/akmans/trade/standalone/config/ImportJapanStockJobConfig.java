package com.akmans.trade.standalone.config;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStock;
import com.akmans.trade.standalone.console.ImportJapanStockApp;
import com.akmans.trade.standalone.dto.CsvJapanStockDto;
import com.akmans.trade.standalone.mapper.ReportFieldSetMapper;
import com.akmans.trade.standalone.model.Report;
import com.akmans.trade.standalone.processor.CustomItemProcessor;
import com.akmans.trade.standalone.springbatch.processors.JapanStockProcessor;
import com.akmans.trade.standalone.springbatch.writers.DummyWriter;
import com.akmans.trade.standalone.springbatch.writers.JapanStockWriter;

public class ImportJapanStockJobConfig {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ImportJapanStockJobConfig.class);

//	@JobScope
//	@Value("#{jobParameters['applicationDate']}")
//	private String applicationDate;

	@Bean
	@StepScope
	public FlatFileItemReader<CsvJapanStockDto> reader(@Value("#{jobParameters['applicationDate']}") String applicationDate) {
		logger.info("applicationDate =" + applicationDate);
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

	@Bean
	@StepScope
	public ItemProcessor<CsvJapanStockDto, TrnJapanStock> processor(@Value("#{jobParameters['applicationDate']}") String applicationDate) {
		return new JapanStockProcessor(applicationDate);
	}

	@Bean
	public ItemWriter<TrnJapanStock> writer() {
		return new JapanStockWriter();
/*		Jaxb2Marshaller reportMarshaller = new Jaxb2Marshaller();
		reportMarshaller.setClassesToBeBound(Report.class);

		Resource resource = new FileSystemResource("target/xml/outputs/report.xml");
		StaxEventItemWriter<Report> xmlItemWriter = new StaxEventItemWriter<Report>();
		xmlItemWriter.setRootTagName("report");
		xmlItemWriter.setMarshaller(reportMarshaller);
		xmlItemWriter.setResource(resource);
		return xmlItemWriter;*/
	}

	@Bean
	public Job importJapanStockJob(JobBuilderFactory jobs, Step step1) {
		return jobs.get("importJapanStockJob").start(step1).build();
	}

	@Bean
	public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<CsvJapanStockDto> reader, ItemWriter<TrnJapanStock> writer,
			ItemProcessor<CsvJapanStockDto, TrnJapanStock> processor) {
//		logger.info("applicationDate =" + applicationDate);
		return stepBuilderFactory.get("step1").<CsvJapanStockDto, TrnJapanStock> chunk(50).reader(reader).processor(processor)
				.writer(writer).build();
	}
}
