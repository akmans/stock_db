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

import com.akmans.trade.core.dto.JapanStockLogQueryDto;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.JapanStockLogService;
import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStockLog;
import com.akmans.trade.web.form.JapanStockLogQueryForm;
import com.akmans.trade.web.utils.PathConstants;
import com.akmans.trade.web.utils.ViewConstants;

@Controller
@RequestMapping(PathConstants.PATH_JAPAN_STOCK_LOGS)
@SessionAttributes("japanStockLogQueryForm")
public class JapanStockLogQueryController extends AbstractQueryController<JapanStockLogQueryForm, TrnJapanStockLog> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(JapanStockLogQueryController.class);

	@Autowired
	private JapanStockLogService japanStockLogService;

	JapanStockLogQueryController() {
		super(PathConstants.PATH_JAPAN_STOCK_LOGS, ViewConstants.VIEW_JAPAN_STOCK_LOG_LIST);
	}

	@Override
	public void initComponent(ModelMap model) {
		// Nothing.
	}

	@Override
	public Page<TrnJapanStockLog> doSearch(ModelMap model, JapanStockLogQueryForm calendarQueryForm,
			Pageable pageable) throws TradeException {
		// Do searching.
		JapanStockLogQueryDto criteria = new JapanStockLogQueryDto();
		BeanUtils.copyProperties(calendarQueryForm, criteria);
		criteria.setPageable(pageable);
		logger.debug("The pageable is {}", pageable);
		return japanStockLogService.findPage(criteria);
	}
}