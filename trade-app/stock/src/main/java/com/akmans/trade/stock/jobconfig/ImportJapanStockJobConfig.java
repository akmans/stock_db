package com.akmans.trade.stock.jobconfig;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.akmans.trade.core.enums.JapanStockJob;
import com.akmans.trade.stock.dto.CsvJapanStockDto;
import com.akmans.trade.stock.springdata.jpa.entities.TrnJapanStock;
import com.akmans.trade.stock.springbatch.execution.JapanStockDownloadExecution;
import com.akmans.trade.stock.springbatch.execution.JapanStockMonthlyGenerateExecution;
import com.akmans.trade.stock.springbatch.execution.JapanStockWeeklyGenerateExecution;
import com.akmans.trade.stock.springbatch.listener.JapanStockJobExecutionListener;
import com.akmans.trade.stock.springbatch.processor.JapanStockConvertProcessor;
import com.akmans.trade.stock.springbatch.writer.JapanStockWriter;

@Configuration
public class ImportJapanStockJobConfig {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ImportJapanStockJobConfig.class);

	@Bean
	@StepScope
	public FlatFileItemReader<CsvJapanStockDto> japanStockReader(@Value("#{jobExecutionContext['targetFile']}") String csv) {
		logger.info("csv =" + csv);
		// flat file item reader (using an csv extractor)
		FlatFileItemReader<CsvJapanStockDto> reader = new FlatFileItemReader<CsvJapanStockDto>();
		reader.setLinesToSkip(1);
		reader.setEncoding("Shift-JIS");
		reader.setResource(new FileSystemResource(csv));
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
	public Job importJapanStockJob(JobBuilderFactory jobs, Step step1, Step step2, Step step3, Step step4,
			JapanStockJobExecutionListener listener) {
		return jobs.get(JapanStockJob.IMPORT_JAPAN_STOCK_JOB.getValue()).start(step1).next(step2).next(step3)
				.next(step4).listener(listener).build();
	}

	@Bean
	public Step step4(StepBuilderFactory stepBuilderFactory, JapanStockMonthlyGenerateExecution generateExecution) {
		return stepBuilderFactory.get("step4").tasklet(generateExecution).build();
	}

	@Bean
	public Step step3(StepBuilderFactory stepBuilderFactory, JapanStockWeeklyGenerateExecution generateExecution) {
		return stepBuilderFactory.get("step3").tasklet(generateExecution).build();
	}

	@Bean
	public Step step2(StepBuilderFactory stepBuilderFactory, FlatFileItemReader<CsvJapanStockDto> japanStockReader,
			JapanStockWriter writer, JapanStockConvertProcessor processor) {
		return stepBuilderFactory.get("step2").<CsvJapanStockDto, TrnJapanStock> chunk(500).reader(japanStockReader)
				.processor(processor).writer(writer).listener(processor).listener(writer).build();
	}

	@Bean
	Step step1(StepBuilderFactory stepBuilderFactory, JapanStockDownloadExecution downloadExecution) {
		return stepBuilderFactory.get("step1").tasklet(downloadExecution).build();
	}
}
