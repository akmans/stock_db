package com.akmans.trade.cashing.web.controller;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.akmans.trade.cashing.service.SpecialDetailService;
import com.akmans.trade.cashing.service.SpecialItemService;
import com.akmans.trade.cashing.springdata.jpa.entities.TrnSpecialDetail;
import com.akmans.trade.cashing.springdata.jpa.entities.TrnSpecialItem;
import com.akmans.trade.cashing.web.form.SpecialDetailEntryForm;
import com.akmans.trade.cashing.web.utils.PathConstants;
import com.akmans.trade.cashing.web.utils.ViewConstants;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.web.controller.AbstractEntryController;

@Controller
@RequestMapping(PathConstants.PATH_SPECIAL_DETAILS)
@SessionAttributes("specialDetailEntryForm")
public class SpecialDetailEntryController extends AbstractEntryController<SpecialDetailEntryForm, TrnSpecialDetail> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(SpecialDetailQueryController.class);

	@Autowired
	private SpecialDetailService specialDetailService;

	@Autowired
	private SpecialItemService specialItemService;

	public SpecialDetailEntryController() {
		super(ViewConstants.VIEW_SPECIAL_DETAIL_ENTRY_FORM_FRAGEMENT);
	}

	public void initCommandForm(ModelMap model, Long code, SpecialDetailEntryForm specialDetailEntryForm)
			throws TradeException {
		logger.debug("specialDetailEntryForm = {}", specialDetailEntryForm);
		TrnSpecialDetail entity = specialDetailService.findOneEager(code);
		BeanUtils.copyProperties(entity, specialDetailEntryForm);
		specialDetailEntryForm.setItemCode(entity.getSpecialItem().getCode());
		specialDetailEntryForm.setItemName(entity.getSpecialItem().getName());
	}

	public void initComponent(ModelMap model) throws TradeException {
		List<TrnSpecialItem> itemList = specialItemService.findAll();
		model.addAttribute("itemList", itemList);
	}

	public void doConfirm(ModelMap model, SpecialDetailEntryForm specialDetailEntryForm) throws TradeException {
		TrnSpecialDetail specialDetail = new TrnSpecialDetail();
		BeanUtils.copyProperties(specialDetailEntryForm, specialDetail);
		TrnSpecialItem item = specialItemService.findOne(specialDetailEntryForm.getItemCode());
		specialDetail.setSpecialItem(item);
		specialDetailService.operation(specialDetail, specialDetailEntryForm.getOperationMode());
		// Set item name.
		specialDetailEntryForm.setItemName(item.getName());
	}
}