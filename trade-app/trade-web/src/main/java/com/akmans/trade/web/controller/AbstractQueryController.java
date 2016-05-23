package com.akmans.trade.web.controller;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.springdata.jpa.entities.AbstractEntity;
import com.akmans.trade.web.form.AbstractQueryForm;
import com.akmans.trade.web.utils.PageWrapper;

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

	@InitBinder
	public void binder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
			public void setAsText(String value) {
				try {
					setValue(new SimpleDateFormat("yyyy-MM-dd").parse(value));
				} catch (ParseException e) {
					setValue(null);
				}
			}

			public String getAsText() {
				Date input = (Date) getValue();
				if (input == null) {
					return null;
				}
				return new SimpleDateFormat("yyyy-MM-dd").format(input);
			}

		});
	}

	@RequestMapping(method = { RequestMethod.POST, RequestMethod.GET })
	String search(Locale locale, ModelMap model, T commandForm, Pageable pageable) {
		logger.debug("pageable = {}", pageable);
		logger.debug("commandForm = {}", commandForm);
		try {
			initComponent(model);
			Page<E> page = doSearch(model, commandForm, pageable);
			PageWrapper<E> wrapper = new PageWrapper<E>(page, pathList);
			model.addAttribute("page", wrapper);
			// Set no record message.
			if (page.getTotalElements() == 0) {
				// errors
				model.addAttribute("cssStyle", "alert-danger");
				model.addAttribute("message",
						messageSource.getMessage("controller.query.search.notrecord", null, locale));
			}
		} catch (TradeException te) {
			// errors
			model.addAttribute("cssStyle", "alert-danger");
			model.addAttribute("message", te.getMessage());
		}

		// render path
		return viewList;
	}

	public abstract void initComponent(ModelMap model) throws TradeException;

	public abstract Page<E> doSearch(ModelMap model, T commandForm, Pageable pageable) throws TradeException;
}