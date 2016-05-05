package com.akmans.trade.web.controller;

import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.SpecialDetailService;
import com.akmans.trade.core.service.SpecialItemService;
import com.akmans.trade.core.springdata.jpa.entities.MstMarket;
import com.akmans.trade.core.springdata.jpa.entities.TrnSpecialDetail;
import com.akmans.trade.core.springdata.jpa.entities.TrnSpecialItem;
import com.akmans.trade.web.form.SpecialDetailEntryForm;
import com.akmans.trade.web.form.SpecialDetailQueryForm;
import com.akmans.trade.web.form.SpecialItemForm;
import com.akmans.trade.web.utils.PageWrapper;
import com.akmans.trade.web.utils.PathConstants;
import com.akmans.trade.web.utils.ViewConstants;

@Controller
@SessionAttributes("specialDetailEntryForm")
public class SpecialDetailEntryController {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(SpecialDetailQueryController.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private SpecialDetailService specialDetailService;

	@Autowired
	private SpecialItemService specialItemService;

	@RequestMapping(value = PathConstants.PATH_SPECIAL_DETAILS + "/{code}/edit", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	public String initEdit(ModelMap model, @PathVariable Long code, SpecialDetailEntryForm specialDetailEntryForm) throws TradeException {
		try {
			// Set operation mode.
			specialDetailEntryForm.setOperationMode(OperationMode.EDIT);
			TrnSpecialDetail entity = specialDetailService.findOneEager(code);
			BeanUtils.copyProperties(entity, specialDetailEntryForm);
			specialDetailEntryForm.setItemCode(entity.getSpecialItem().getCode());
			specialDetailEntryForm.setItemName(entity.getSpecialItem().getName());
		} catch (TradeException te) {
			// errors
			model.addAttribute("cssStyle", "alert-danger");
			model.addAttribute("message", te.getMessage());
		}
		List<TrnSpecialItem> itemList = specialItemService.findAll();
		model.addAttribute("itemList", itemList);

		// render path
		return ViewConstants.VIEW_SPECIAL_DETAIL_FORM_FRAGEMENT;
	}

	@RequestMapping(value = PathConstants.PATH_SPECIAL_DETAILS + "/{code}/delete", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	public String initDelete(ModelMap model, @PathVariable Long code, SpecialDetailEntryForm specialDetailEntryForm) throws TradeException {
		try {
			// Set operation mode.
			specialDetailEntryForm.setOperationMode(OperationMode.DELETE);
			TrnSpecialDetail entity = specialDetailService.findOneEager(code);
			BeanUtils.copyProperties(entity, specialDetailEntryForm);
			specialDetailEntryForm.setItemCode(entity.getSpecialItem().getCode());
			specialDetailEntryForm.setItemName(entity.getSpecialItem().getName());
		} catch (TradeException te) {
			// errors
			model.addAttribute("cssStyle", "alert-danger");
			model.addAttribute("message", te.getMessage());
		}
//		List<TrnSpecialItem> itemList = specialItemService.findAll();
//		model.addAttribute("itemList", itemList);

		// render path
		return ViewConstants.VIEW_SPECIAL_DETAIL_FORM_FRAGEMENT;
	}


	@RequestMapping(value = PathConstants.PATH_SPECIAL_DETAILS + "/new", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	public String init(ModelMap model, SpecialDetailEntryForm specialDetailEntryForm) {
		// Set operation mode.
		specialDetailEntryForm.setOperationMode(OperationMode.NEW);
		List<TrnSpecialItem> itemList = specialItemService.findAll();
		model.addAttribute("itemList", itemList);

		// render path
		return ViewConstants.VIEW_SPECIAL_DETAIL_FORM_FRAGEMENT;
	}

	@RequestMapping(value = PathConstants.PATH_SPECIAL_DETAILS + "/post", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	String confirm(Locale locale, ModelMap model, @Valid final SpecialDetailEntryForm specialDetailEntryForm, BindingResult bindingResult,
			Pageable pageable) {
		logger.debug("The commandForm = {}", specialDetailEntryForm);
		String message = null;
		if (bindingResult.hasErrors()) {
			List<ObjectError> errors = bindingResult.getAllErrors();
			for (ObjectError error : errors) {
				logger.debug("ERRORS" + error.getDefaultMessage());
			}
			// errors
			model.addAttribute("cssStyle", "alert-danger");
			// render path
			return ViewConstants.VIEW_SPECIAL_DETAIL_FORM_FRAGEMENT;
		} else {
			try {
				// do confirm operation.
//				confirmOperation(specialDetailEntryForm);
				TrnSpecialDetail specialDetail = new TrnSpecialDetail();
				BeanUtils.copyProperties(specialDetailEntryForm, specialDetail);
				TrnSpecialItem item = specialItemService.findOne(specialDetailEntryForm.getItemCode());
				specialDetail.setSpecialItem(item);
				specialDetailService.operation(specialDetail, specialDetailEntryForm.getOperationMode());
				// get message.
				if (specialDetailEntryForm.getOperationMode() == OperationMode.EDIT) {
					message = messageSource.getMessage("controller.simple.update.finish", null, locale);
				} else if (specialDetailEntryForm.getOperationMode() == OperationMode.NEW) {
					message = messageSource.getMessage("controller.simple.insert.finish", null, locale);
				} else if (specialDetailEntryForm.getOperationMode() == OperationMode.DELETE) {
					message = messageSource.getMessage("controller.simple.delete.finish", null, locale);
				}
				model.addAttribute("cssStyle", "alert-success");
				model.addAttribute("message", message);
			} catch (TradeException te) {
				// errors
				model.addAttribute("cssStyle", "alert-danger");
				model.addAttribute("message", te.getMessage());
				// render path
				return ViewConstants.VIEW_SPECIAL_DETAIL_FORM_FRAGEMENT;
			}
		}
		// Get all records
		Page<TrnSpecialDetail> page = doSearch(pageable);
		PageWrapper<TrnSpecialDetail> wrapper = new PageWrapper<TrnSpecialDetail>(page, PathConstants.PATH_SPECIAL_DETAILS);
		model.addAttribute("page", wrapper);

		// render path
		return ViewConstants.VIEW_SPECIAL_DETAIL_LIST_CONTENT_FRAGEMENT;
	}
	
	private Page<TrnSpecialDetail> doSearch(Pageable pageable) {
		logger.debug("The pageable is {}", pageable);
		return specialDetailService.findPage(pageable);
	}
}