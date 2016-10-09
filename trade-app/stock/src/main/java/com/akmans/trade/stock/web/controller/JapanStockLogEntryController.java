package com.akmans.trade.stock.web.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
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

import com.akmans.trade.core.enums.JapanStockJob;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.enums.OperationStatus;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.CalendarService;
import com.akmans.trade.core.web.controller.AbstractController;
import com.akmans.trade.stock.service.JapanStockLogService;
import com.akmans.trade.stock.springdata.jpa.entities.TrnJapanStockLog;
import com.akmans.trade.stock.springdata.jpa.keys.JapanStockLogKey;
import com.akmans.trade.stock.web.form.JapanStockLogEntryForm;
import com.akmans.trade.stock.web.utils.PathConstants;
import com.akmans.trade.stock.web.utils.ViewConstants;

@Controller
@RequestMapping(PathConstants.PATH_JAPAN_STOCK_LOGS)
@SessionAttributes("japanStockLogEntryForm")
public class JapanStockLogEntryController extends AbstractController {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(JapanStockLogEntryController.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private CalendarService calendarService;

	@Autowired
	private JapanStockLogService japanStockLogService;

	@Autowired
	private ApplicationContext appContext;

	private static String viewForm = ViewConstants.VIEW_JAPAN_STOCK_LOG_ENTRY_FORM_FRAGEMENT;

	@RequestMapping(value = "/new", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	String initNew(ModelMap model, JapanStockLogEntryForm japanStockLogEntryForm) {
		// Set job id.
		String jobId = JapanStockJob.IMPORT_JAPAN_STOCK_JOB.getValue();
		japanStockLogEntryForm.setJobId(jobId);
		// Set process date.
		Calendar cal1 = Calendar.getInstance();
		cal1.set(2099, 11, 31); // 20991231
		TrnJapanStockLog log = japanStockLogService.findMaxRegistDate(jobId, cal1.getTime());
		logger.debug("JapanStockLogEntryController log is !!!" + log);
		Date currentDate = log.getJapanStockLogKey().getProcessDate();
		logger.debug("JapanStockLogEntryController !!!" + currentDate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		if (DateUtils.truncate(cal.getTime(), Calendar.DAY_OF_MONTH)
				.compareTo(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)) < 0) {
			do {
				cal.add(Calendar.DAY_OF_MONTH, 1);
				logger.debug("JapanStockLogEntryController cal is !!!" + cal.getTime());
			} while (!calendarService.isJapanBusinessDay(cal.getTime()));
		}

		Date processDate = cal.getTime();
		japanStockLogEntryForm.setProcessDate(processDate);
		// Set operation mode.
		japanStockLogEntryForm.setOperationMode(OperationMode.NEW);
		// Set operation status.
		japanStockLogEntryForm.setOperationStatus(OperationStatus.ENTRY);
		// render path
		return viewForm;
	}

	@RequestMapping(value = "/post", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	String confirm(Locale locale, ModelMap model, @Valid final JapanStockLogEntryForm japanStockLogEntryForm,
			BindingResult bindingResult) {
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
				TrnJapanStockLog japanStockLog = null;
				JapanStockLogKey japanStockLogKey = new JapanStockLogKey();
				japanStockLogKey.setJobId(japanStockLogEntryForm.getJobId());
				japanStockLogKey.setProcessDate(japanStockLogEntryForm.getProcessDate());
				if (!calendarService.isJapanBusinessDay(japanStockLogEntryForm.getProcessDate())) {
					// get message.
					message = messageSource.getMessage("controller.japanstocklog.not.businessday",
							new Object[] { japanStockLogKey }, locale);
					throw new TradeException(message);
				}
				Optional<TrnJapanStockLog> optional = japanStockLogService.findOne(japanStockLogKey);
				if (optional.isPresent()) {
//					japanStockLog = japanStockLogService.findOne(japanStockLogKey);
					if (ExitStatus.COMPLETED.getExitCode().equals(optional.get().getStatus())) {
						// get message.
						message = messageSource.getMessage("controller.japanstocklog.job.already.completed",
								new Object[] { japanStockLogKey }, locale);
						throw new TradeException(message);
					}
					// do confirm operation.
					japanStockLogService.operation(japanStockLog, OperationMode.EDIT);
				} else {
					japanStockLog = new TrnJapanStockLog();
					japanStockLog.setJapanStockLogKey(japanStockLogKey);
					// do confirm operation.
					japanStockLogService.operation(japanStockLog, japanStockLogEntryForm.getOperationMode());
				}
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
		} catch (Exception te) {
			// errors
			model.addAttribute("cssStyle", "alert-danger");
			model.addAttribute("message", te.getMessage());
			te.printStackTrace();
		}

		// render path
		return viewForm;
	}

	private void launch(String jobId, Date processDate) throws Exception {
		// Get job launcher.
		JobLauncher jobLauncher = (JobLauncher) appContext.getBean("jobLauncher");
		// Get job.
		Job job = (Job) appContext.getBean(jobId);
		logger.info("Job Restartable ? : " + job.isRestartable());

		// Build job parameters.
		JobParameters params = new JobParametersBuilder().addString("jobId", jobId)
				.addDate("processDate", processDate).toJobParameters();
		// Run the job.
		JobExecution execution = jobLauncher.run(job, params);
		logger.info("Exit Status : " + execution.getStatus());
	}
}