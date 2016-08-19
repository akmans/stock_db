package com.akmans.trade.cashing.web.controller;

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

import com.akmans.trade.cashing.dto.SpecialDetailQueryDto;
import com.akmans.trade.cashing.service.SpecialDetailService;
import com.akmans.trade.cashing.service.SpecialItemService;
import com.akmans.trade.cashing.springdata.jpa.entities.TrnSpecialDetail;
import com.akmans.trade.cashing.springdata.jpa.entities.TrnSpecialItem;
import com.akmans.trade.cashing.web.form.SpecialDetailQueryForm;
import com.akmans.trade.cashing.web.utils.PathConstants;
import com.akmans.trade.cashing.web.utils.ViewConstants;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.web.controller.AbstractQueryController;

@Controller
@RequestMapping(PathConstants.PATH_SPECIAL_DETAILS)
@SessionAttributes("specialDetailQueryForm")
public class SpecialDetailQueryController extends AbstractQueryController<SpecialDetailQueryForm, TrnSpecialDetail> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(SpecialDetailQueryController.class);

	@Autowired
	private SpecialDetailService specialDetailService;

	@Autowired
	private SpecialItemService specialItemService;

	SpecialDetailQueryController() {
		super(PathConstants.PATH_SPECIAL_DETAILS, ViewConstants.VIEW_SPECIAL_DETAIL_LIST);
	}

	@Override
	public void initComponent(ModelMap model) {
		// Get item list.
		List<TrnSpecialItem> itemList = specialItemService.findAll();
		model.addAttribute("itemList", itemList);
	}

	@Override
	public Page<TrnSpecialDetail> doSearch(ModelMap model, SpecialDetailQueryForm specialDetailQueryForm,
			Pageable pageable) throws TradeException {
		// Do searching.
		SpecialDetailQueryDto criteria = new SpecialDetailQueryDto();
		BeanUtils.copyProperties(specialDetailQueryForm, criteria);
		criteria.setPageable(pageable);
		logger.debug("The pageable is {}", pageable);
		return specialDetailService.findPage(criteria);
	}
}