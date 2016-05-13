package com.akmans.trade.web.controller;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.CalendarService;
import com.akmans.trade.core.springdata.jpa.entities.MstCalendar;
import com.akmans.trade.web.form.CalendarEntryForm;
import com.akmans.trade.web.utils.PathConstants;
import com.akmans.trade.web.utils.ViewConstants;

@Controller
@RequestMapping(PathConstants.PATH_CALENDARS)
@SessionAttributes("calendarEntryForm")
public class CalendarEntryController extends AbstractEntryController<CalendarEntryForm, MstCalendar> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(CalendarEntryController.class);

	@Autowired
	private CalendarService calendarService;

	public CalendarEntryController() {
		super(ViewConstants.VIEW_CALENDAR_ENTRY_FORM_FRAGEMENT);
	}

	public void initCommandForm(ModelMap model, Long code, CalendarEntryForm calendarEntryForm)
			throws TradeException {
		logger.debug("calendarEntryForm = {}", calendarEntryForm);
		MstCalendar entity = calendarService.findOne(code);
		BeanUtils.copyProperties(entity, calendarEntryForm);
	}

	public void initComponent(ModelMap model) throws TradeException {
		// Nothing!
	}

	public void doConfirm(ModelMap model, CalendarEntryForm calendarEntryForm) throws TradeException {
		MstCalendar calendar = new MstCalendar();
		BeanUtils.copyProperties(calendarEntryForm, calendar);
		calendarService.operation(calendar, calendarEntryForm.getOperationMode());
	}
}