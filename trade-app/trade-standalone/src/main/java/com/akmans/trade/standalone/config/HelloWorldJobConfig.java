package com.akmans.trade.standalone.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.akmans.trade.standalone.mapper.ReportFieldSetMapper;
import com.akmans.trade.standalone.model.Report;
import com.akmans.trade.standalone.processor.CustomItemProcessor;

public class HelloWorldJobConfig {

	@Bean
	private Report report() {
		return new Report();
	}

	@Bean
	private FlatFileItemReader<Report> reader() {
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setNames(new String[]{"id", "sales", "qty", "staffName", "date"});

		DefaultLineMapper<Report> mapper = new DefaultLineMapper<Report>();
		mapper.setFieldSetMapper(new ReportFieldSetMapper());
		mapper.setLineTokenizer(lineTokenizer);

		Resource resource = new ClassPathResource("csv/report.csv");
		FlatFileItemReader<Report> reader = new FlatFileItemReader<Report>();
		reader.setResource(resource);
		reader.setLineMapper(mapper);
		return reader;
	}

	@Bean
	private ItemProcessor<Report, Report> processor() {
		return new CustomItemProcessor();
	}

	@Bean
	private ItemWriter<Report> writer() {
		Jaxb2Marshaller reportMarshaller = new Jaxb2Marshaller();
		reportMarshaller.setClassesToBeBound(Report.class);

		Resource resource = new FileSystemResource("target/xml/outputs/report.xml");
		StaxEventItemWriter<Report> xmlItemWriter = new StaxEventItemWriter<Report>();
		xmlItemWriter.setRootTagName("report");
		xmlItemWriter.setMarshaller(reportMarshaller);
		xmlItemWriter.setResource(resource);
		return xmlItemWriter;
	}

	@Bean
	public Job helloWorldJob(JobBuilderFactory jobs, Step step){
		return jobs.get("helloWorldJob")
				.start(step)
				.build();
	}

	@Bean
	private Step step(StepBuilderFactory stepBuilderFactory, ItemReader<Report> reader,
            ItemWriter<Report> writer, ItemProcessor<Report, Report> processor){
		return stepBuilderFactory.get("step1")
				.<Report, Report>chunk(50)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}
}
