package com.akmans.trade.web.controller;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.akmans.trade.core.springdata.jpa.dao.MstSector33Dao;
import com.akmans.trade.core.springdata.jpa.entities.MstSector33;

@Controller
public class BaseController {

	private static int counter = 0;
	private static final String VIEW_INDEX = "index";
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(BaseController.class);

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

}