package com.akmans.trade.web.controller;

import java.util.List;
import java.util.Locale;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.akmans.trade.core.springdata.jpa.dao.MstSector33Dao;
import com.akmans.trade.core.springdata.jpa.entities.MstSector33;

@Controller
public class BaseController {

	private static int counter = 0;
	private static final String VIEW_INDEX = "index";
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(BaseController.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private MstSector33Dao mstSector33Dao;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String welcome(ModelMap model) {
		// Print all records
		List<MstSector33> sector33s = (List<MstSector33>) mstSector33Dao.findAll();
		logger.debug("[sector33s] size : {}", sector33s.size());

		model.addAttribute("sector33", sector33s);
		model.addAttribute("message", "Welcome");
		model.addAttribute("counter", ++counter);
		logger.debug("[welcome] counter : {}", counter);

		// Spring uses InternalResourceViewResolver and return back index.jsp
		return VIEW_INDEX;
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Locale locale, ModelMap model, @RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout) {
		String message = null;
		if (error != null) {
			message = messageSource.getMessage("controller.base.authenticate.error", null, locale);
			model.addAttribute("cssStyle", "alert-danger");
			model.addAttribute("message", message);
		}

		if (logout != null) {
			message = messageSource.getMessage("controller.base.logout.success", null, locale);
			model.addAttribute("cssStyle", "alert-success");
			model.addAttribute("message", message);
		}

		return "user/login";

	}

}