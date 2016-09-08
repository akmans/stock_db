package com.akmans.trade.fx.jobconfig;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;

import com.akmans.trade.core.enums.FXJob;
import com.akmans.trade.fx.dto.CsvHistoryTickDto;
import com.akmans.trade.fx.model.FXTick;
import com.akmans.trade.fx.springbatch.listener.FXJobExecutionListener;
import com.akmans.trade.fx.springbatch.processor.HistoryTickConvertProcessor;
import com.akmans.trade.fx.springbatch.writer.FXHistoryTickWriter;

@Configuration
public class ImportHistoryTickJobConfig {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ImportHistoryTickJobConfig.class);

	@Autowired
	private Environment env;

	@Bean
	@StepScope
	public FlatFileItemReader<CsvHistoryTickDto> reader(@Value("#{jobParameters['targetFile']}") String csv) {
		String fullPath = env.getRequiredProperty("truefx.folder") + "/" + csv;
		logger.info("the processed csv file =" + fullPath);
		// flat file item reader (using an csv extractor)
		FlatFileItemReader<CsvHistoryTickDto> reader = new FlatFileItemReader<CsvHistoryTickDto>();
		reader.setLinesToSkip(0);
		reader.setEncoding("Shift-JIS");
		reader.setResource(new FileSystemResource(fullPath));
		reader.setLineMapper(new DefaultLineMapper<CsvHistoryTickDto>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						// USD/JPY,20090501 00:00:00.296,98.89,98.902
						setNames(new String[] { "currencyPair", "registDate", "bidPrice", "askPrice" });
					}
				});
				setFieldSetMapper(new BeanWrapperFieldSetMapper<CsvHistoryTickDto>() {
					{
						setTargetType(CsvHistoryTickDto.class);
					}
				});
			}
		});
		return reader;
	}

	@Bean
	public Job importHistoryTickJob(JobBuilderFactory jobs, Step step1,
			FXJobExecutionListener listener) {
		return jobs.get(FXJob.IMPORT_HISTORY_TICK_JOB.getValue()).start(step1).listener(listener)
				.build();
	}

	@Bean
	public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<CsvHistoryTickDto> reader,
			FXHistoryTickWriter writer, HistoryTickConvertProcessor processor) {
		return stepBuilderFactory.get("step1").<CsvHistoryTickDto, FXTick> chunk(1000).reader(reader)
				.processor(processor).writer(writer).listener(processor).listener(writer).build();
	}
}
