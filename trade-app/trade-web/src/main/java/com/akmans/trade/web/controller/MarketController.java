package com.akmans.trade.web.controller;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
import com.akmans.trade.core.service.MarketService;
import com.akmans.trade.core.springdata.jpa.entities.MstMarket;
import com.akmans.trade.web.form.MarketForm;
import com.akmans.trade.web.utils.PageWrapper;
import com.akmans.trade.web.utils.ViewConstants;

@Controller
@RequestMapping("/markets")
@SessionAttributes("marketForm")
public class MarketController {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(MarketController.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private MarketService marketService;

	@RequestMapping(method = RequestMethod.GET)
	public String init(ModelMap model, Pageable pageable) {
		logger.debug("pageable = {}" , pageable);
		Locale locale = LocaleContextHolder.getLocale();
		logger.debug("Locale = {}" , locale);
		Page<MstMarket> page = marketService.findAll(pageable);
		PageWrapper<MstMarket> markets = new PageWrapper<MstMarket>(page, "/markets");

		model.addAttribute("page", markets);

		// render path
		return ViewConstants.VIEW_MARKET_LIST;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	public String initAdd(ModelMap model, MarketForm marketForm) {
		// Set operation mode.
		marketForm.setOperationMode(OperationMode.NEW);

		// render path
		return ViewConstants.VIEW_ENTRY_FORM_FORM_FRAGEMENT;
	}

	@RequestMapping(value = "/{code}/edit", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	public String initEdit(ModelMap model, @PathVariable Integer code, MarketForm marketForm) throws TradeException {
		// Set operation mode.
		marketForm.setOperationMode(OperationMode.EDIT);
		// Get records
		MstMarket market = marketService.findOne(code);
		BeanUtils.copyProperties(market, marketForm);

		// render path
		return ViewConstants.VIEW_ENTRY_FORM_FORM_FRAGEMENT;
	}

	@RequestMapping(value = "/post", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	public String confirm(Locale locale, ModelMap model, @Valid final MarketForm marketForm, BindingResult bindingResult, Pageable pageable) throws TradeException {
		logger.debug("The marketForm = {}", marketForm);
		String message = null;
		if (bindingResult.hasErrors()) {
			List<ObjectError> errors = bindingResult.getAllErrors();
			for(ObjectError error : errors) {
				logger.debug("ERRORS" + error.getDefaultMessage());
			}
			// errors
			model.addAttribute("cssStyle", "alert-danger");

			// render path
			return ViewConstants.VIEW_ENTRY_FORM_FORM_FRAGEMENT;
		} else {
			MstMarket market = null;
			if (marketForm.getOperationMode() == OperationMode.EDIT) {
				market = marketService.findOne(marketForm.getCode());
				logger.debug("Updated.");
			} else {
				market = new MstMarket();
				logger.debug("Inserted.");
			}

			BeanUtils.copyProperties(marketForm, market);
			marketService.save(market, marketForm.getOperationMode());

			if (marketForm.getOperationMode() == OperationMode.EDIT) {
				message = messageSource.getMessage("controller.market.update.finish", null, locale);
			} else {
				message = messageSource.getMessage("controller.market.insert.finish", null, locale);
			}
			model.addAttribute("cssStyle", "alert-success");
		}
		// Get all records
		Page<MstMarket> page = marketService.findAll(pageable);
		PageWrapper<MstMarket> markets = new PageWrapper<MstMarket>(page, "/markets");
		model.addAttribute("page", markets);
		model.addAttribute("message", message);

		// render path
		return ViewConstants.VIEW_MARKET_LIST_CONTENT_FRAGEMENT;
	}

	@RequestMapping(value = "/{code}/delete", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	public String delete(Locale locale, ModelMap model, @PathVariable Integer code, Pageable pageable) throws TradeException {
		marketService.delete(code);
		// Get all records
		Page<MstMarket> page = marketService.findAll(pageable);
		PageWrapper<MstMarket> markets = new PageWrapper<MstMarket>(page, "/markets");
		model.addAttribute("page", markets);

		model.addAttribute("message", messageSource.getMessage("controller.market.delete.finish", null, locale));
		model.addAttribute("cssStyle", "alert-success");

		// render path
		return ViewConstants.VIEW_MARKET_LIST_CONTENT_FRAGEMENT;
	}
}