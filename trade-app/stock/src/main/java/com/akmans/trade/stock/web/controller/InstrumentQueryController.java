package com.akmans.trade.stock.web.controller;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.web.controller.AbstractQueryController;
import com.akmans.trade.stock.dto.InstrumentQueryDto;
import com.akmans.trade.stock.service.InstrumentService;
import com.akmans.trade.stock.service.MarketService;
import com.akmans.trade.stock.service.ScaleService;
import com.akmans.trade.stock.service.Sector17Service;
import com.akmans.trade.stock.service.Sector33Service;
import com.akmans.trade.stock.springdata.jpa.entities.MstInstrument;
import com.akmans.trade.stock.springdata.jpa.entities.MstMarket;
import com.akmans.trade.stock.springdata.jpa.entities.MstScale;
import com.akmans.trade.stock.springdata.jpa.entities.MstSector17;
import com.akmans.trade.stock.springdata.jpa.entities.MstSector33;
import com.akmans.trade.stock.web.form.InstrumentQueryForm;
import com.akmans.trade.stock.web.utils.PathConstants;
import com.akmans.trade.stock.web.utils.ViewConstants;

@Controller
@RequestMapping(PathConstants.PATH_INSTRUMENTS)
@SessionAttributes("instrumentQueryForm")
public class InstrumentQueryController extends AbstractQueryController<InstrumentQueryForm, MstInstrument> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(InstrumentQueryController.class);

	@Autowired
	private InstrumentService instrumentService;

	@Autowired
	private ScaleService scaleService;

	@Autowired
	private MarketService marketService;

	@Autowired
	private Sector17Service sector17Service;

	@Autowired
	private Sector33Service sector33Service;

	InstrumentQueryController() {
		super(PathConstants.PATH_INSTRUMENTS, ViewConstants.VIEW_INSTRUMENT_LIST);
	}

	@Override
	public void initComponent(ModelMap model) {
		List<MstScale> scales = scaleService.findAll();
		List<MstMarket> markets = marketService.findAll();
		List<MstSector17> sector17s = sector17Service.findAll();
		List<MstSector33> sector33s = sector33Service.findAll();
		model.addAttribute("scales", scales);
		model.addAttribute("markets", markets);
		model.addAttribute("sector17s", sector17s);
		model.addAttribute("sector33s", sector33s);
	}

	@Override
	public Page<MstInstrument> doSearch(ModelMap model, InstrumentQueryForm instrumentQueryForm,
			Pageable pageable) throws TradeException {
		// Do searching.
		InstrumentQueryDto criteria = new InstrumentQueryDto();
		BeanUtils.copyProperties(instrumentQueryForm, criteria);
		criteria.setPageable(pageable);
		logger.debug("The pageable is {}", pageable);
		return instrumentService.findPage(criteria);
	}
}