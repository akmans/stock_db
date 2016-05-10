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
import com.akmans.trade.core.service.Sector33Service;
import com.akmans.trade.core.springdata.jpa.entities.MstSector33;
import com.akmans.trade.web.form.Sector33Form;
import com.akmans.trade.web.utils.PathConstants;
import com.akmans.trade.web.utils.ViewConstants;

@Controller
@RequestMapping(PathConstants.PATH_SECTOR33S)
@SessionAttributes("sector33Form")
public class Sector33Controller extends AbstractSimpleController<Sector33Form, MstSector33> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ScaleController.class);

	@Autowired
	private Sector33Service sector33Service;

	public Sector33Controller() {
		super(PathConstants.PATH_SECTOR33S, ViewConstants.VIEW_SECTOR33_LIST,
				ViewConstants.VIEW_SECTOR33_FORM_FRAGEMENT, ViewConstants.VIEW_SECTOR33_LIST_CONTENT_FRAGEMENT);
	}

	public Page<MstSector33> doSearch(Pageable pageable) {
		logger.debug("The pageable is {}", pageable);
		return sector33Service.findAll(pageable);
	}

	public MstSector33 findOne(Integer code) throws TradeException {
		logger.debug("The code is {}", code);
		return sector33Service.findOne(code);
	}

	public void confirmOperation(Sector33Form sector33Form) throws TradeException {
		logger.debug("The sector33Form is {}", sector33Form);
		MstSector33 sector33 = new MstSector33();
		BeanUtils.copyProperties(sector33Form, sector33);
		sector33Service.operation(sector33, sector33Form.getOperationMode());
	}
}