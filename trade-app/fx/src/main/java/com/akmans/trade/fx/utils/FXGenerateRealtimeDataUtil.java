package com.akmans.trade.fx.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.akmans.trade.core.enums.CurrencyPair;
import com.akmans.trade.core.service.MessageService;
import com.akmans.trade.fx.service.FX6HourService;
import com.akmans.trade.fx.service.FXDayService;
import com.akmans.trade.fx.service.FXHourService;
import com.akmans.trade.fx.service.FXMonthService;
import com.akmans.trade.fx.service.FXWeekService;

@Component
public class FXGenerateRealtimeDataUtil {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXGenerateRealtimeDataUtil.class);

	private FXHourService fxHourService;

	private FX6HourService fx6HourService;

	private FXDayService fxDayService;

	private FXWeekService fxWeekService;

	private FXMonthService fxMonthService;

	private MessageService messageService;

	@Autowired
	FXGenerateRealtimeDataUtil(FXHourService fxHourService, FX6HourService fx6HourService, FXDayService fxDayService,
			FXWeekService fxWeekService, FXMonthService fxMonthService, MessageService messageService) {
		this.fxHourService = fxHourService;
		this.fx6HourService = fx6HourService;
		this.fxDayService = fxDayService;
		this.fxWeekService = fxWeekService;
		this.fxMonthService = fxMonthService;
		this.messageService = messageService;
	}

	public void execute() throws Exception {
		logger.info("Start !");
		int counter = 0;
		// Stop flag to end the endless loop.
		String stopFlag = "RUNNING";

		while (counter < 100 && "RUNNING".equals(stopFlag)) {
			LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
			// loop currency pair types.
			for (CurrencyPair currencyPair : CurrencyPair.values()) {
				// Save hour data.
				fxHourService.refresh(currencyPair.getValue(), now, false);
				// Save hour data.
				fx6HourService.refresh(currencyPair.getValue(), now);
				// Save day data.
				fxDayService.refresh(currencyPair.getValue(), now);
				// Save hour data.
				fxWeekService.refresh(currencyPair.getValue(), now);
				// Save day data.
				fxMonthService.refresh(currencyPair.getValue(), now);
			}
		}
		logger.info("End !");
	}
}
