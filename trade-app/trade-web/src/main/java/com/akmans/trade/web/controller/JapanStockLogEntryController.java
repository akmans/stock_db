package com.akmans.trade.web.controller;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.enums.OperationStatus;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.JapanStockLogService;
import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStockLog;
import com.akmans.trade.core.springdata.jpa.keys.JapanStockLogKey;
import com.akmans.trade.web.form.JapanStockLogEntryForm;
import com.akmans.trade.web.utils.PathConstants;
import com.akmans.trade.web.utils.ViewConstants;

@Controller
@RequestMapping(PathConstants.PATH_JAPAN_STOCK_LOGS)
@SessionAttributes("japanStockLogEntryForm")
public class JapanStockLogEntryController extends AbstractController {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(JapanStockLogEntryController.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private JapanStockLogService japanStockLogService;

	@Autowired
	private ApplicationContext appContext;

	private static String viewForm = ViewConstants.VIEW_JAPAN_STOCK_LOG_ENTRY_FORM_FRAGEMENT;

	@RequestMapping(value = "/new", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	String initNew(ModelMap model, JapanStockLogEntryForm japanStockLogEntryForm) {
		// Set operation mode.
		japanStockLogEntryForm.setOperationMode(OperationMode.NEW);
		// Set operation status.
		japanStockLogEntryForm.setOperationStatus(OperationStatus.ENTRY);
		// render path
		return viewForm;
	}

	@RequestMapping(value = "/post", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	String confirm(Locale locale, ModelMap model, @Valid final JapanStockLogEntryForm japanStockLogEntryForm, BindingResult bindingResult) {
		logger.debug("The commandForm = {}", japanStockLogEntryForm);
		String message = null;
		try {
			if (bindingResult.hasErrors()) {
				List<ObjectError> errors = bindingResult.getAllErrors();
				for (ObjectError error : errors) {
					logger.debug("ERRORS" + error.getDefaultMessage());
				}
				// Errors
				model.addAttribute("cssStyle", "alert-danger");
			} else {
				JapanStockLogKey japanStockLogKey = new JapanStockLogKey();
				japanStockLogKey.setJobId(japanStockLogEntryForm.getJobId());
				japanStockLogKey.setProcessDate(japanStockLogEntryForm.getProcessDate());
				// do confirm operation.
				TrnJapanStockLog japanStockLog = new TrnJapanStockLog();
				japanStockLog.setJapanStockLogKey(japanStockLogKey);
				japanStockLogService.operation(japanStockLog, japanStockLogEntryForm.getOperationMode());
				// launch the job.
				launch(japanStockLogKey.getJobId(), japanStockLogKey.getProcessDate());
				// get message.
				message = messageSource.getMessage("controller.japanstocklog.insert.finish", null, locale);
				// Set operation status.
				japanStockLogEntryForm.setOperationStatus(OperationStatus.COMPLETE);
				// Set message.
				model.addAttribute("cssStyle", "alert-success");
				model.addAttribute("message", message);
			}
		} catch (TradeException te) {
			// errors
			model.addAttribute("cssStyle", "alert-danger");
			model.addAttribute("message", te.getMessage());
		}

		// render path
		return viewForm;
	}

	private void launch(String jobId, Date processDate) {
		JobLauncher jobLauncher = (JobLauncher) appContext.getBean("jobLauncher");
		Job job = (Job) appContext.getBean(jobId);
		logger.info("Job Restartable ? : " + job.isRestartable());

		try {
			JobParameters params = new JobParametersBuilder().addString("jobId", jobId)
					.addDate("processDate", processDate).toJobParameters();
			JobExecution execution = jobLauncher.run(job, params);
			logger.info("Exit Status : " + execution.getStatus());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}