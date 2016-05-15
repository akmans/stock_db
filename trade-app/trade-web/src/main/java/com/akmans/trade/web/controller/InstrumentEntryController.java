package com.akmans.trade.web.controller;

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
import com.akmans.trade.core.service.InstrumentService;
import com.akmans.trade.core.service.MarketService;
import com.akmans.trade.core.service.ScaleService;
import com.akmans.trade.core.service.Sector17Service;
import com.akmans.trade.core.service.Sector33Service;
import com.akmans.trade.core.springdata.jpa.entities.MstCalendar;
import com.akmans.trade.core.springdata.jpa.entities.MstInstrument;
import com.akmans.trade.core.springdata.jpa.entities.MstMarket;
import com.akmans.trade.core.springdata.jpa.entities.MstScale;
import com.akmans.trade.core.springdata.jpa.entities.MstSector17;
import com.akmans.trade.core.springdata.jpa.entities.MstSector33;
import com.akmans.trade.web.form.CalendarEntryForm;
import com.akmans.trade.web.form.InstrumentEntryForm;
import com.akmans.trade.web.utils.PathConstants;
import com.akmans.trade.web.utils.ViewConstants;

@Controller
@RequestMapping(PathConstants.PATH_INSTRUMENTS)
@SessionAttributes("instrumentEntryForm")
public class InstrumentEntryController extends AbstractEntryController<InstrumentEntryForm, MstInstrument> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(InstrumentEntryController.class);

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

	public InstrumentEntryController() {
		super(ViewConstants.VIEW_INSTRUMENT_FORM_FRAGEMENT);
	}

	public void initCommandForm(ModelMap model, Long code, InstrumentEntryForm instrumentEntryForm)
			throws TradeException {
		logger.debug("instrumentEntryForm = {}", instrumentEntryForm);
		MstInstrument entity = instrumentService.findOneEager(code);
		BeanUtils.copyProperties(entity, instrumentEntryForm);
		if (entity.getScale() != null) {
			instrumentEntryForm.setScale(entity.getScale().getCode());
			instrumentEntryForm.setScaleName(entity.getScale().getName());
		}
		if (entity.getMarket() != null) {
			instrumentEntryForm.setMarket(entity.getMarket().getCode());
			instrumentEntryForm.setMarketName(entity.getMarket().getName());
		}
		if (entity.getSector17() != null) {
			instrumentEntryForm.setSector17(entity.getSector17().getCode());
			instrumentEntryForm.setSector17Name(entity.getSector17().getName());
		}
		if (entity.getSector33() != null) {
			instrumentEntryForm.setSector33(entity.getSector33().getCode());
			instrumentEntryForm.setSector33Name(entity.getSector33().getName());
		}
	}

	public void initComponent(ModelMap model) throws TradeException {
		List<MstScale> scales = scaleService.findAll();
		List<MstMarket> markets = marketService.findAll();
		List<MstSector17> sector17s = sector17Service.findAll();
		List<MstSector33> sector33s = sector33Service.findAll();
		model.addAttribute("scales", scales);
		model.addAttribute("markets", markets);
		model.addAttribute("sector17s", sector17s);
		model.addAttribute("sector33s", sector33s);
	}

	public void doConfirm(ModelMap model, InstrumentEntryForm instrumentEntryForm) throws TradeException {
		MstInstrument instrument = new MstInstrument();
		BeanUtils.copyProperties(instrumentEntryForm, instrument);
		if (instrumentEntryForm.getScale() != null) {
			MstScale scale = scaleService.findOne(instrumentEntryForm.getScale());
			instrument.setScale(scale);
			instrumentEntryForm.setScaleName(scale.getName());
		}
		if (instrumentEntryForm.getMarket() != null) {
			MstMarket market = marketService.findOne(instrumentEntryForm.getMarket());
			instrument.setMarket(market);
			instrumentEntryForm.setMarketName(market.getName());
		}
		if (instrumentEntryForm.getSector17() != null) {
			MstSector17 sector17 = sector17Service.findOne(instrumentEntryForm.getSector17());
			instrument.setSector17(sector17);
			instrumentEntryForm.setSector17Name(sector17.getName());
		}
		if (instrumentEntryForm.getSector33() != null) {
			MstSector33 sector33 = sector33Service.findOne(instrumentEntryForm.getSector33());
			instrument.setSector33(sector33);
			instrumentEntryForm.setSector33Name(sector33.getName());
		}
		instrumentService.operation(instrument, instrumentEntryForm.getOperationMode());
	}

/*	public Page<MstInstrument> doSearch(Pageable pageable) {
		logger.debug("The pageable is {}", pageable);
		return instrumentService.findPage(pageable);
	}

	public MstInstrument findOne(Integer code) throws TradeException {
		logger.debug("The code is {}", code);
		return instrumentService.findOne(code);
	}

	public void confirmOperation(InstrumentForm instrumentForm) throws TradeException {
		logger.debug("The marketForm is {}", instrumentForm);
		MstInstrument instrument = new MstInstrument();
		BeanUtils.copyProperties(instrumentForm, instrument);
		instrumentService.operation(instrument, instrumentForm.getOperationMode());
	}*/
}