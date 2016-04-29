package com.akmans.trade.web.controller;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.akmans.trade.core.springdata.jpa.dao.MstMarketDao;
import com.akmans.trade.core.springdata.jpa.entities.MstMarket;
import com.akmans.trade.web.form.MarketForm;

@Controller
@RequestMapping("/markets")
@SessionAttributes("marketForm")
public class MarketController {

	private static final String VIEW_LIST_PATH = "market/list";
	private static final String VIEW_LIST_CONTENT_FRAGEMENT = "market/list :: content";
	private static final String VIEW_ENTRY_FORM_FRAGEMENT = "market/entry-form :: form";
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(MarketController.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private MstMarketDao mstMarketDao;

	@RequestMapping(method = RequestMethod.GET)
	public String init(ModelMap model/*, HttpServletResponse response*/) /*throws Exception*/ {
		// Get all records
		List<MstMarket> markets = (List<MstMarket>) mstMarketDao.findAllByOrderByCode();

		model.addAttribute("market", markets);
//		response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bad or missing CSRF value");

		// render path
		return VIEW_LIST_PATH;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	public String initAdd(ModelMap model, MarketForm marketForm) {
		// Initialize market form.
		marketForm = new MarketForm();

		// render path
		return VIEW_ENTRY_FORM_FRAGEMENT;
	}

	@RequestMapping(value = "/{code}/edit", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	public String initEdit(ModelMap model, @PathVariable Integer code, MarketForm marketForm) {
		// Get records
		Optional<MstMarket> option = mstMarketDao.findOne(code);
		if (option.isPresent()) {
			MstMarket market = option.get();
			marketForm.setCode(market.getCode());
			marketForm.setName(market.getName());
		}

		// render path
		return VIEW_ENTRY_FORM_FRAGEMENT;
	}

	@RequestMapping(value = "/post", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	public String confirm(Locale locale, ModelMap model, @Valid final MarketForm marketForm, BindingResult bindingResult) {
		logger.debug("code=" + marketForm.getCode() + ";;name=" + marketForm.getName());
		String message = null;
		if (bindingResult.hasErrors()) {
			List<ObjectError> errors = bindingResult.getAllErrors();
			for(ObjectError error : errors) {
				logger.debug("ERRORS" + error.getDefaultMessage());
			}
			// errors
			model.addAttribute("cssStyle", "alert-danger");

			// render path
			return VIEW_ENTRY_FORM_FRAGEMENT;
		} else {
			Optional<MstMarket> option = mstMarketDao.findOne(marketForm.getCode());
			MstMarket market = null;
			if (option.isPresent()) {
				market = option.get();
				logger.debug("Updated.");
			} else {
				market = new MstMarket();
				logger.debug("Inserted.");
			}
			market.setCode(marketForm.getCode());
			market.setName(marketForm.getName());
			mstMarketDao.save(market);

			if (option.isPresent()) {
				message = messageSource.getMessage("controller.market.update.finish", null, locale);
			} else {
				message = messageSource.getMessage("controller.market.insert.finish", null, locale);
			}
			model.addAttribute("cssStyle", "alert-success");
		}
		// Get all records
		List<MstMarket> markets = (List<MstMarket>) mstMarketDao.findAllByOrderByCode();
		model.addAttribute("market", markets);
		model.addAttribute("message", message);

		// render path
		return VIEW_LIST_CONTENT_FRAGEMENT;
	}

	@RequestMapping(value = "/{code}/delete", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	public String delete(Locale locale, ModelMap model, @PathVariable Integer code) {
		Optional<MstMarket> option = mstMarketDao.findOne(code);
		String message = null;
		if (option.isPresent()) {
			mstMarketDao.delete(option.get());
			logger.debug("Deleted successfully!");
			message = messageSource.getMessage("controller.market.delete.finish", null, locale);
		} else {
			logger.debug("Data not exist!");
		}

		// Get all records
		List<MstMarket> markets = (List<MstMarket>) mstMarketDao.findAllByOrderByCode();
		model.addAttribute("market", markets);

		model.addAttribute("message", message);
		model.addAttribute("cssStyle", "alert-success");

		// render path
		return VIEW_LIST_CONTENT_FRAGEMENT;
	}
}