package com.akmans.trade.standalone.config;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStock;
import com.akmans.trade.standalone.dto.CsvJapanStockDto;
import com.akmans.trade.standalone.springbatch.execution.JapanInstrumentDownloadExecution;
import com.akmans.trade.standalone.springbatch.execution.JapanStockDownloadExecution;
import com.akmans.trade.standalone.springbatch.listeners.JapanStockJobExecutionListener;
import com.akmans.trade.standalone.springbatch.processors.JapanStockConvertProcessor;
import com.akmans.trade.standalone.springbatch.writers.JapanStockWriter;

public class ImportJapanStockJobConfig {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ImportJapanStockJobConfig.class);

/*	private StepExecution stepExecution;

	@BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }
*/
	@Bean
	@StepScope
	public FlatFileItemReader<CsvJapanStockDto> reader(@Value("#{jobExecutionContext['targetFile']}") String csv){
//			@Value("#{jobParameters['applicationDate']}") String applicationDate) {
//		String csv = (String)stepExecution.getJobExecution().getExecutionContext().get("targetFile");
		logger.info("csv =" + csv);
		// flat file item reader (using an csv extractor)
		FlatFileItemReader<CsvJapanStockDto> reader = new FlatFileItemReader<CsvJapanStockDto>();
		reader.setLinesToSkip(1);
		reader.setEncoding("Shift-JIS");
		reader.setResource(new FileSystemResource(csv));
///		reader.setResource(new ClassPathResource("csv/stocks_2007-07-24.csv"));
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
/*	@Bean
	@StepScope
	public ItemProcessor<CsvJapanStockDto, TrnJapanStock> processor() {
//			@Value("#{jobParameters['applicationDate']}") String applicationDate) {
//		return new JapanStockConvertProcessor(stepExecution.getJobExecution().getJobParameters().getDate("processDate"));
		return new JapanStockConvertProcessor();
	}

	@Bean
	public ItemWriter<TrnJapanStock> writer() {
		return new JapanStockWriter();
	}
*/
	@Bean
	public Job importJapanStockJob(JobBuilderFactory jobs, Step step1, Step step2, JapanStockJobExecutionListener listener) {
		return jobs.get("importJapanStockJob").start(step1).next(step2).listener(listener).build();
//		return jobs.get("importJapanStockJob").start(step1).next(step2).build();
	}

	@Bean
	public Step step2(StepBuilderFactory stepBuilderFactory, ItemReader<CsvJapanStockDto> reader,
			JapanStockWriter writer, JapanStockConvertProcessor processor) {
		// logger.info("applicationDate =" + applicationDate);
		return stepBuilderFactory.get("step2").<CsvJapanStockDto, TrnJapanStock> chunk(500).reader(reader)
				.processor(processor).writer(writer).listener(processor).build();
	}

	@Bean Step step1(StepBuilderFactory stepBuilderFactory, JapanStockDownloadExecution downloadExecution) {
		return stepBuilderFactory.get("step1").tasklet(downloadExecution).build();
	}
/*	@Bean
	public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<CsvJapanStockDto> reader,
			ItemProcessor<CsvJapanStockDto, CsvJapanStockDto> processor1) {
		// logger.info("applicationDate =" + applicationDate);
		return stepBuilderFactory.get("step1").<CsvJapanStockDto, CsvJapanStockDto> chunk(500).reader(reader)
				.processor(processor1).build();
	}*/
}
