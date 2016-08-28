package com.akmans.trade.stock.console.springbatch.execution;

import java.util.Date;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.akmans.trade.core.Constants;
import com.akmans.trade.core.utils.DateUtil;
import com.akmans.trade.core.utils.FileUtil;

@Component
//@PropertySource(Constants.ENVIRONMENT_PROPERITES_FILE_PATH)
public class JapanStockDownloadExecution extends StepExecutionListenerSupport implements Tasklet {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(JapanStockDownloadExecution.class);

	private Date applicationDate;

	@Autowired
	private Environment env;

	@Autowired
	private FileUtil fileUtil;

	public void beforeStep(StepExecution stepExecution) {
		JobParameters jobParameters = stepExecution.getJobParameters();
		applicationDate = jobParameters.getDate("processDate");
		logger.debug("The applicationDate is {}", applicationDate);
	}

	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		String appDate = DateUtil.formatDate(applicationDate, "yyyy-MM-dd");
		String sourceUrl = env.getRequiredProperty("japan.stock.url") + appDate + "?download=csv";
		String targetDirectory = env.getRequiredProperty("standalone.work.dir");
		logger.debug("SourceUrl is {}", sourceUrl);
		String targetFile = targetDirectory + "/stocks_" + appDate + ".csv";
		fileUtil.download(sourceUrl, targetFile);
//		String targetFile = "/Users/owner99/Desktop/FX/work/stocks_2016-05-26.csv";
		logger.debug("Downloaded file is {}", targetFile);
		chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("targetFile",
				targetFile);
		return RepeatStatus.FINISHED;
	}
}
