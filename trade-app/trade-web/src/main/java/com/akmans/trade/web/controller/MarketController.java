package com.akmans.trade.web.controller;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.MarketService;
import com.akmans.trade.core.springdata.jpa.entities.MstMarket;
import com.akmans.trade.web.form.MarketForm;
import com.akmans.trade.web.utils.PathConstants;
import com.akmans.trade.web.utils.ViewConstants;

@Controller
@RequestMapping(PathConstants.PATH_MARKETS)
@SessionAttributes("marketForm")
public class MarketController extends AbstractSimpleController<MarketForm, MstMarket> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(MarketController.class);

	@Autowired
	private MarketService marketService;

	public MarketController() {
		super(PathConstants.PATH_MARKETS, ViewConstants.VIEW_MARKET_LIST, ViewConstants.VIEW_MARKET_FORM_FRAGEMENT,
				ViewConstants.VIEW_MARKET_LIST_CONTENT_FRAGEMENT);
	}

	public Page<MstMarket> doSearch(Pageable pageable) {
		logger.debug("The pageable is {}", pageable);
		return marketService.findAll(pageable);
	}

	public MstMarket findOne(Integer code) throws TradeException {
		logger.debug("The code is {}", code);
		return marketService.findOne(code);
	}

	public void confirmOperation(MarketForm marketForm) throws TradeException {
		logger.debug("The marketForm is {}", marketForm);
		MstMarket market = new MstMarket();
		BeanUtils.copyProperties(marketForm, market);
		marketService.operation(market, marketForm.getOperationMode());
	}
}