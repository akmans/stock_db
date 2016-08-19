package com.akmans.trade.stock.web.controller;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.web.controller.AbstractSimpleController;
import com.akmans.trade.stock.service.ScaleService;
import com.akmans.trade.stock.springdata.jpa.entities.MstScale;
import com.akmans.trade.stock.web.form.ScaleForm;
import com.akmans.trade.stock.web.utils.PathConstants;
import com.akmans.trade.stock.web.utils.ViewConstants;

@Controller
@RequestMapping(PathConstants.PATH_SCALES)
@SessionAttributes("scaleForm")
public class ScaleController extends AbstractSimpleController<ScaleForm, MstScale> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ScaleController.class);

	@Autowired
	private ScaleService scaleService;

	public ScaleController() {
		super(PathConstants.PATH_SCALES, ViewConstants.VIEW_SCALE_LIST, ViewConstants.VIEW_SCALE_FORM_FRAGEMENT,
				ViewConstants.VIEW_SCALE_LIST_CONTENT_FRAGEMENT);
	}

	public Page<MstScale> doSearch(Pageable pageable) {
		logger.debug("The pageable is {}", pageable);
		return scaleService.findAll(pageable);
	}

	public MstScale findOne(Integer code) throws TradeException {
		logger.debug("The code is {}", code);
		return scaleService.findOne(code);
	}

	public void confirmOperation(ScaleForm scaleForm) throws TradeException {
		logger.debug("The scaleForm is {}", scaleForm);
		MstScale scale = new MstScale();
		BeanUtils.copyProperties(scaleForm, scale);
		scaleService.operation(scale, scaleForm.getOperationMode());
	}
}