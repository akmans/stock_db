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
import com.akmans.trade.core.service.Sector17Service;
import com.akmans.trade.core.springdata.jpa.entities.MstSector17;
import com.akmans.trade.web.form.Sector17Form;
import com.akmans.trade.web.utils.PathConstants;
import com.akmans.trade.web.utils.ViewConstants;

@Controller
@RequestMapping(PathConstants.PATH_SECTOR17S)
@SessionAttributes("sector17Form")
public class Sector17Controller extends AbstractSimpleController<Sector17Form, MstSector17> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ScaleController.class);

	@Autowired
	private Sector17Service sector17Service;

	public Sector17Controller() {
		super(PathConstants.PATH_SECTOR17S, ViewConstants.VIEW_SECTOR17_LIST,
				ViewConstants.VIEW_SECTOR17_FORM_FRAGEMENT, ViewConstants.VIEW_SECTOR17_LIST_CONTENT_FRAGEMENT);
	}

	public Page<MstSector17> doSearch(Pageable pageable) {
		logger.debug("The pageable is {}", pageable);
		return sector17Service.findAll(pageable);
	}

	public MstSector17 findOne(Integer code) throws TradeException {
		logger.debug("The code is {}", code);
		return sector17Service.findOne(code);
	}

	public void confirmOperation(Sector17Form sector17Form) throws TradeException {
		logger.debug("The sector17Form is {}", sector17Form);
		MstSector17 sector17 = new MstSector17();
		BeanUtils.copyProperties(sector17Form, sector17);
		sector17Service.operation(sector17, sector17Form.getOperationMode());
	}
}