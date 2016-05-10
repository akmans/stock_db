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
import com.akmans.trade.core.service.SpecialItemService;
import com.akmans.trade.core.springdata.jpa.entities.TrnSpecialItem;
import com.akmans.trade.web.form.SpecialItemForm;
import com.akmans.trade.web.utils.PathConstants;
import com.akmans.trade.web.utils.ViewConstants;

@Controller
@RequestMapping(PathConstants.PATH_SPECIAL_ITEMS)
@SessionAttributes("specialItemForm")
public class SpecialItemController extends AbstractSimpleController<SpecialItemForm, TrnSpecialItem> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(SpecialItemController.class);

	@Autowired
	private SpecialItemService specialItemService;

	public SpecialItemController() {
		super(PathConstants.PATH_SPECIAL_ITEMS, ViewConstants.VIEW_SPECIAL_ITEM_LIST,
				ViewConstants.VIEW_SPECIAL_ITEM_FORM_FRAGEMENT, ViewConstants.VIEW_SPECIAL_ITEM_LIST_CONTENT_FRAGEMENT);
	}

	public Page<TrnSpecialItem> doSearch(Pageable pageable) {
		logger.debug("The pageable is {}", pageable);
		return specialItemService.findAll(pageable);
	}

	public TrnSpecialItem findOne(Integer code) throws TradeException {
		logger.debug("The code is {}", code);
		return specialItemService.findOne(code);
	}

	public void confirmOperation(SpecialItemForm specialItemForm) throws TradeException {
		logger.debug("The specialItemForm is {}", specialItemForm);
		TrnSpecialItem specialItem = new TrnSpecialItem();
		BeanUtils.copyProperties(specialItemForm, specialItem);
		specialItemService.operation(specialItem, specialItemForm.getOperationMode());
	}
}