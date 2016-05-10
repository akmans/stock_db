package com.akmans.trade.web.controller;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import com.akmans.trade.core.springdata.jpa.entities.AbstractEntity;
import com.akmans.trade.core.springdata.jpa.entities.TrnSpecialDetail;
import com.akmans.trade.core.springdata.jpa.entities.TrnSpecialItem;
import com.akmans.trade.web.form.AbstractQueryForm;
import com.akmans.trade.web.form.SpecialDetailQueryForm;
import com.akmans.trade.web.form.SpecialItemForm;
import com.akmans.trade.web.utils.PageWrapper;
import com.akmans.trade.web.utils.PathConstants;
import com.akmans.trade.web.utils.ViewConstants;

public abstract class AbstractQueryController<T extends AbstractQueryForm, E extends AbstractEntity> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractQueryController.class);

	@Autowired
	private MessageSource messageSource;

	private String pathList;

	private String viewList;

	public AbstractQueryController(String path, String view) {
		this.pathList = path;
		this.viewList = view;
	}

	@RequestMapping(method = {RequestMethod.POST, RequestMethod.GET})
	public String search(ModelMap model, T commandForm, Pageable pageable) {
		logger.debug("pageable = {}", pageable);
//		logger.debug("specialDetailQueryForm = {}", specialDetailQueryForm);
//		specialDetailQueryForm.setName("あああ");
//		Page<TrnSpecialDetail> page = doSearch(specialDetailQueryForm, pageable);
		try {
			Page<E> page = doSearch(model, commandForm, pageable);
			PageWrapper<E> wrapper = new PageWrapper<E>(page, pathList);
			model.addAttribute("page", wrapper);
		} catch (TradeException te) {
			// errors
			model.addAttribute("cssStyle", "alert-danger");
			model.addAttribute("message", te.getMessage());
		}
//		List<TrnSpecialItem> itemList = specialItemService.findAll();
//		model.addAttribute("itemList", itemList);

		// render path
		return viewList;
	}

	public abstract Page<E> doSearch(ModelMap model, T commandForm, Pageable pageable) throws TradeException;
/*	private Page<TrnSpecialDetail> doSearch(SpecialDetailQueryForm specialDetailQueryForm, Pageable pageable) {
		SpecialDetailQueryDto criteria = new SpecialDetailQueryDto();
		BeanUtils.copyProperties(specialDetailQueryForm, criteria);
		criteria.setPageable(pageable);
		logger.debug("The pageable is {}", pageable);
		return specialDetailService.findPage(criteria);
	}*/
}