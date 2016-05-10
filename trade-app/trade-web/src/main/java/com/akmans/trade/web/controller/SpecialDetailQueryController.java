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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.akmans.trade.core.dto.SpecialDetailQueryDto;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.SpecialDetailService;
import com.akmans.trade.core.service.SpecialItemService;
import com.akmans.trade.core.springdata.jpa.entities.TrnSpecialDetail;
import com.akmans.trade.core.springdata.jpa.entities.TrnSpecialItem;
import com.akmans.trade.web.form.SpecialDetailQueryForm;
import com.akmans.trade.web.form.SpecialItemForm;
import com.akmans.trade.web.utils.PageWrapper;
import com.akmans.trade.web.utils.PathConstants;
import com.akmans.trade.web.utils.ViewConstants;

@Controller
@RequestMapping(PathConstants.PATH_SPECIAL_DETAILS)
@SessionAttributes("specialDetailQueryForm")
public class SpecialDetailQueryController extends AbstractQueryController<SpecialDetailQueryForm, TrnSpecialDetail> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(SpecialDetailQueryController.class);

	@Autowired
	private SpecialDetailService specialDetailService;

	@Autowired
	private SpecialItemService specialItemService;
/*
	@RequestMapping(value = PathConstants.PATH_SPECIAL_DETAILS, method = {RequestMethod.POST, RequestMethod.GET})
	public String search(ModelMap model, SpecialDetailQueryForm specialDetailQueryForm, Pageable pageable) {
		logger.debug("pageable = {}", pageable);
		logger.debug("specialDetailQueryForm = {}", specialDetailQueryForm);
//		specialDetailQueryForm.setName("あああ");
		Page<TrnSpecialDetail> page = doSearch(specialDetailQueryForm, pageable);
		PageWrapper<TrnSpecialDetail> wrapper = new PageWrapper<TrnSpecialDetail>(page, PathConstants.PATH_SPECIAL_DETAILS);
		model.addAttribute("page", wrapper);
		List<TrnSpecialItem> itemList = specialItemService.findAll();
		model.addAttribute("itemList", itemList);

		// render path
		return ViewConstants.VIEW_SPECIAL_DETAIL_LIST;
	}
	
	private Page<TrnSpecialDetail> doSearch(SpecialDetailQueryForm specialDetailQueryForm, Pageable pageable) {
		SpecialDetailQueryDto criteria = new SpecialDetailQueryDto();
		BeanUtils.copyProperties(specialDetailQueryForm, criteria);
		criteria.setPageable(pageable);
		logger.debug("The pageable is {}", pageable);
		return specialDetailService.findPage(criteria);
	}*/
	SpecialDetailQueryController() {
		super(PathConstants.PATH_SPECIAL_DETAILS, ViewConstants.VIEW_SPECIAL_DETAIL_LIST);
	}

	@Override
	public Page<TrnSpecialDetail> doSearch(ModelMap model, SpecialDetailQueryForm specialDetailQueryForm, Pageable pageable) throws TradeException {
		List<TrnSpecialItem> itemList = specialItemService.findAll();
		model.addAttribute("itemList", itemList);
		SpecialDetailQueryDto criteria = new SpecialDetailQueryDto();
		BeanUtils.copyProperties(specialDetailQueryForm, criteria);
		criteria.setPageable(pageable);
		logger.debug("The pageable is {}", pageable);
		return specialDetailService.findPage(criteria);
	}
}