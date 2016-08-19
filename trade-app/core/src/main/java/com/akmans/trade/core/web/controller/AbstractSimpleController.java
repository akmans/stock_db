package com.akmans.trade.core.web.controller;

import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.akmans.trade.core.web.utils.PageWrapper;

public abstract class AbstractSimpleController<T extends AbstractSimpleForm, E extends AbstractEntity> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractSimpleController.class);

	@Autowired
	private MessageSource messageSource;

	private String pathList;

	private String viewList;

	private String viewEntryFormFragement;

	public AbstractSimpleController(String pathList, String viewList, String viewEntryFormFragement,
			String viewListContentFragement) {
		this.pathList = pathList;
		this.viewList = viewList;
		this.viewEntryFormFragement = viewEntryFormFragement;
	};

	@RequestMapping(method = RequestMethod.GET)
	String init(ModelMap model, Pageable pageable) {
		logger.debug("pageable = {}", pageable);
		Page<E> page = doSearch(pageable);
		PageWrapper<E> wrapper = new PageWrapper<E>(page, pathList);
		model.addAttribute("page", wrapper);

		// render path
		return viewList;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	String initAdd(ModelMap model, T commandForm) {
		// Set operation mode.
		commandForm.setOperationMode(OperationMode.NEW);
		// Set operation status.
		commandForm.setOperationStatus(OperationStatus.ENTRY);

		// render path
		return viewEntryFormFragement;
	}

	@RequestMapping(value = "/{code}/edit", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	String initEdit(ModelMap model, @PathVariable Integer code, T commandForm) {
		try {
			// Set operation mode.
			commandForm.setOperationMode(OperationMode.EDIT);
			// Set operation status.
			commandForm.setOperationStatus(OperationStatus.ENTRY);
			// Get records
			E entity = findOne(code);
			BeanUtils.copyProperties(entity, commandForm);
		} catch (TradeException te) {
			// errors
			model.addAttribute("cssStyle", "alert-danger");
			model.addAttribute("message", te.getMessage());
		}

		// render path
		return viewEntryFormFragement;
	}

	@RequestMapping(value = "/{code}/delete", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	String initDelete(Locale locale, ModelMap model, @PathVariable Integer code, T commandForm) {
		try {
			// Set operation mode.
			commandForm.setOperationMode(OperationMode.DELETE);
			// Set operation status.
			commandForm.setOperationStatus(OperationStatus.ENTRY);
			// Get records
			E entity = findOne(code);
			BeanUtils.copyProperties(entity, commandForm);
		} catch (TradeException te) {
			// errors
			model.addAttribute("cssStyle", "alert-danger");
			model.addAttribute("message", te.getMessage());
		}

		// render path
		return viewEntryFormFragement;
	}

	@RequestMapping(value = "/post", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	String confirm(Locale locale, ModelMap model, @Valid final T commandForm, BindingResult bindingResult) {
		logger.debug("The commandForm = {}", commandForm);
		String message = null;
		if (bindingResult.hasErrors()) {
			List<ObjectError> errors = bindingResult.getAllErrors();
			for (ObjectError error : errors) {
				logger.debug("ERRORS" + error.getDefaultMessage());
			}
			// errors
			model.addAttribute("cssStyle", "alert-danger");
		} else {
			try {
				// do confirm operation.
				confirmOperation(commandForm);
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
				model.addAttribute("cssStyle", "alert-success");
				model.addAttribute("message", message);
			} catch (TradeException te) {
				// errors
				model.addAttribute("cssStyle", "alert-danger");
				model.addAttribute("message", te.getMessage());
			}
		}

		// render path
		return viewEntryFormFragement;
	}

	public abstract void confirmOperation(T commandForm) throws TradeException;

	public abstract Page<E> doSearch(Pageable pageable);

	public abstract E findOne(Integer code) throws TradeException;
}