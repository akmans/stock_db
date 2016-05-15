package com.akmans.trade.web.controller;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.akmans.trade.core.dto.CalendarQueryDto;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.CalendarService;
import com.akmans.trade.core.springdata.jpa.entities.MstCalendar;
import com.akmans.trade.web.form.CalendarQueryForm;
import com.akmans.trade.web.utils.PathConstants;
import com.akmans.trade.web.utils.ViewConstants;

@Controller
@RequestMapping(PathConstants.PATH_CALENDARS)
@SessionAttributes("calendarQueryForm")
public class CalendarQueryController extends AbstractQueryController<CalendarQueryForm, MstCalendar> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(CalendarQueryController.class);

	@Autowired
	private CalendarService calendarService;

	CalendarQueryController() {
		super(PathConstants.PATH_CALENDARS, ViewConstants.VIEW_CALENDAR_LIST);
	}

	@Override
	public void initComponent(ModelMap model) {
		// Nothing.
	}

	@Override
	public Page<MstCalendar> doSearch(ModelMap model, CalendarQueryForm calendarQueryForm,
			Pageable pageable) throws TradeException {
		// Do searching.
		CalendarQueryDto criteria = new CalendarQueryDto();
		BeanUtils.copyProperties(calendarQueryForm, criteria);
		criteria.setPageable(pageable);
		logger.debug("The pageable is {}", pageable);
		return calendarService.findPage(criteria);
	}
}