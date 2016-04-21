package com.akmans.trade.web.controller;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.akmans.trade.core.springdata.jpa.dao.MstMarketDao;
import com.akmans.trade.core.springdata.jpa.entities.MstMarket;
import com.akmans.trade.web.form.MarketForm;

@Controller
@RequestMapping("/markets")
public class MarketController {

	private static final String VIEW_LIST_PATH = "market/list";
	private static final String VIEW_NEW_PATH = "market/new";
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(MarketController.class);

	@Autowired
	private MstMarketDao mstMarketDao;

	@RequestMapping(method = RequestMethod.GET)
	public String init(ModelMap model) {
		// Print all records
		List<MstMarket> markets = (List<MstMarket>) mstMarketDao.findAll();
		logger.debug("[markets] size : {}", markets.size());

		model.addAttribute("market", markets);

		// render path
		return VIEW_LIST_PATH;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String confirmAdd(ModelMap model) {
		// Print all records
		MarketForm form = new MarketForm();

		model.addAttribute("commandForm", form);

		// render path
		return VIEW_NEW_PATH;
	}
}