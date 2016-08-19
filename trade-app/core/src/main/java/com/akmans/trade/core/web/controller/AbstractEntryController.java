package com.akmans.trade.core.web.controller;

import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.enums.OperationStatus;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.springdata.jpa.entities.AbstractEntity;
import com.akmans.trade.core.web.form.AbstractSimpleForm;

public abstract class AbstractEntryController<T extends AbstractSimpleForm, E extends AbstractEntity> extends AbstractController {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractEntryController.class);

	@Autowired
	private MessageSource messageSource;

	private String viewForm;

	public AbstractEntryController(String viewForm) {
		this.viewForm = viewForm;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	String initNew(ModelMap model, T commandForm) throws TradeException {
		try {
			// Set operation mode.
			commandForm.setOperationMode(OperationMode.NEW);
			// Set operation status.
			commandForm.setOperationStatus(OperationStatus.ENTRY);
			// initialize component.
			initComponent(model);
		} catch (TradeException te) {
			// errors
			model.addAttribute("cssStyle", "alert-danger");
			model.addAttribute("message", te.getMessage());
		}

		// render path
		return viewForm;
	}

	@RequestMapping(value = "/{code}/{action}", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	String initAction(Locale locale, ModelMap model, @PathVariable Long code, @PathVariable String action,
			T commandForm) throws TradeException {
		try {
			if (action != null && "edit".equals(action)) {
				// Set operation mode.
				commandForm.setOperationMode(OperationMode.EDIT);
			} else if (action != null && "delete".equals(action)) {
				// Set operation mode.
				commandForm.setOperationMode(OperationMode.DELETE);
			} else {
				throw new TradeException(messageSource.getMessage("controller.entry.action.notdefined", null, locale));
			}
			// Set operation status.
			commandForm.setOperationStatus(OperationStatus.ENTRY);
			// initialize command form.
			initCommandForm(model, code, commandForm);
			// initialize component.
			initComponent(model);
		} catch (TradeException te) {
			// errors
			model.addAttribute("cssStyle", "alert-danger");
			model.addAttribute("message", te.getMessage());
		}

		// render path
		return viewForm;
	}

	@RequestMapping(value = "/post", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	String confirm(Locale locale, ModelMap model, @Valid final T commandForm, BindingResult bindingResult) {
		logger.debug("The commandForm = {}", commandForm);
		String message = null;
		try {
			if (bindingResult.hasErrors()) {
				List<ObjectError> errors = bindingResult.getAllErrors();
				for (ObjectError error : errors) {
					logger.debug("ERRORS" + error.getDefaultMessage());
				}
				// Errors
				model.addAttribute("cssStyle", "alert-danger");
				// Initialize component.
				initComponent(model);
			} else {
				// do confirm operation.
				doConfirm(model, commandForm);
				// get message.
				if (commandForm.getOperationMode() == OperationMode.EDIT) {
					message = messageSource.getMessage("controller.simple.update.finish", null, locale);
				} else if (commandForm.getOperationMode() == OperationMode.NEW) {
					message = messageSource.getMessage("controller.simple.insert.finish", null, locale);
				} else if (commandForm.getOperationMode() == OperationMode.DELETE) {
					message = messageSource.getMessage("controller.simple.delete.finish", null, locale);
				}
				// Set operation status.
				commandForm.setOperationStatus(OperationStatus.COMPLETE);
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

	public abstract void initCommandForm(ModelMap model, Long code, T commandForm) throws TradeException;

	public abstract void initComponent(ModelMap model) throws TradeException;

	public abstract void doConfirm(ModelMap model, T commandForm) throws TradeException;
}