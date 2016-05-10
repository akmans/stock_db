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
import com.akmans.trade.core.service.InstrumentService;
import com.akmans.trade.core.springdata.jpa.entities.MstInstrument;
import com.akmans.trade.web.form.InstrumentForm;
import com.akmans.trade.web.utils.PathConstants;
import com.akmans.trade.web.utils.ViewConstants;

@Controller
@RequestMapping(PathConstants.PATH_INSTRUMENTS)
@SessionAttributes("instrumentForm")
public class InstrumentController extends AbstractSimpleController<InstrumentForm, MstInstrument> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(MarketController.class);

	@Autowired
	private InstrumentService instrumentService;

	public InstrumentController() {
		super(PathConstants.PATH_INSTRUMENTS, ViewConstants.VIEW_INSTRUMENT_LIST,
				ViewConstants.VIEW_INSTRUMENT_FORM_FRAGEMENT, ViewConstants.VIEW_INSTRUMENT_LIST_CONTENT_FRAGEMENT);
	}

	public Page<MstInstrument> doSearch(Pageable pageable) {
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
	}
}