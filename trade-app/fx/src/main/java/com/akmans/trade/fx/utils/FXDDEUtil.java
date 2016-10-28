package com.akmans.trade.fx.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.akmans.trade.core.enums.CurrencyPair;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.MessageService;
import com.akmans.trade.fx.model.FXTick;
import com.akmans.trade.fx.service.FXTickService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXTick;
import com.pretty_tools.dde.DDEException;
import com.pretty_tools.dde.DDEMLException;
import com.pretty_tools.dde.client.DDEClientConversation;

@Component
public class FXDDEUtil {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXDDEUtil.class);

	private FXTickService fxTickService;

	private MessageService messageService;

	@Autowired
	FXDDEUtil(FXTickService fxTickService, MessageService messageService) {
		this.fxTickService = fxTickService;
		this.messageService = messageService;
	}

	public void execute() throws Exception {
		logger.info("Start !");
		int counter = 0;
		// Stop flag to end the endless loop.
		String stopFlag = "RUNNING";

		// Map to save previous rate data.
		Map<String, String> previousMap = new HashMap<String, String>(9);

		while (counter < 100 && "RUNNING".equals(stopFlag)) {
			try {
				// DDE client
				final DDEClientConversation conversation = new DDEClientConversation();
				conversation.setTimeout(3000);
				conversation.connect("Excel", "Sheet1");

				try {
					// Requesting A1 value
					String currentUSDJPY = conversation.request("R3C3").trim();
					String currentEURJPY = conversation.request("R4C3").trim();
					String currentAUDJPY = conversation.request("R5C3").trim();
					String currentGBPJPY = conversation.request("R6C3").trim();
					String currentCHFJPY = conversation.request("R7C3").trim();
					String currentEURUSD = conversation.request("R8C3").trim();
					String currentGBPUSD = conversation.request("R9C3").trim();
					String currentAUDUSD = conversation.request("R10C3").trim();
					String currentUSDCHF = conversation.request("R11C3").trim();
					stopFlag = conversation.request("R13C3").trim();
					// Previous Value.
					String previousUSDJPY = previousMap.put(CurrencyPair.USDJPY.getValue(), currentUSDJPY);
					String previousEURJPY = previousMap.put(CurrencyPair.EURJPY.getValue(), currentEURJPY);
					String previousAUDJPY = previousMap.put(CurrencyPair.AUDJPY.getValue(), currentAUDJPY);
					String previousGBPJPY = previousMap.put(CurrencyPair.GBPJPY.getValue(), currentGBPJPY);
					String previousCHFJPY = previousMap.put(CurrencyPair.CHFJPY.getValue(), currentCHFJPY);
					String previousEURUSD = previousMap.put(CurrencyPair.EURUSD.getValue(), currentEURUSD);
					String previousGBPUSD = previousMap.put(CurrencyPair.GBPUSD.getValue(), currentGBPUSD);
					String previousAUDUSD = previousMap.put(CurrencyPair.AUDUSD.getValue(), currentAUDUSD);
					String previousUSDCHF = previousMap.put(CurrencyPair.USDCHF.getValue(), currentUSDCHF);
					// Reset counter.
					counter = 0;
					logger.debug("STOP FLAG:" + stopFlag);
					if (currentUSDJPY != null && !currentUSDJPY.equals(previousUSDJPY)) {
						logger.debug(CurrencyPair.USDJPY.getLabel() + " : " + currentUSDJPY);
						previousMap.put(CurrencyPair.USDJPY.getValue(), currentUSDJPY);
						process(CurrencyPair.USDJPY, currentUSDJPY);
					}
					if (currentEURJPY != null && !currentEURJPY.equals(previousEURJPY)) {
						logger.debug(CurrencyPair.EURJPY.getLabel() + " : " + currentEURJPY);
						previousMap.put(CurrencyPair.EURJPY.getValue(), currentEURJPY);
						process(CurrencyPair.EURJPY, currentEURJPY);
					}
					if (currentAUDJPY != null && !currentAUDJPY.equals(previousAUDJPY)) {
						logger.debug(CurrencyPair.AUDJPY.getLabel() + " : " + currentAUDJPY);
						previousMap.put(CurrencyPair.AUDJPY.getValue(), currentAUDJPY);
						process(CurrencyPair.AUDJPY, currentAUDJPY);
					}
					if (currentGBPJPY != null && !currentGBPJPY.equals(previousGBPJPY)) {
						logger.debug(CurrencyPair.GBPJPY.getLabel() + " : " + currentGBPJPY);
						previousMap.put(CurrencyPair.GBPJPY.getValue(), currentGBPJPY);
						process(CurrencyPair.GBPJPY, currentGBPJPY);
					}
					if (currentCHFJPY != null && !currentCHFJPY.equals(previousCHFJPY)) {
						logger.debug(CurrencyPair.CHFJPY.getLabel() + " : " + currentCHFJPY);
						previousMap.put(CurrencyPair.CHFJPY.getValue(), currentCHFJPY);
						process(CurrencyPair.CHFJPY, currentCHFJPY);
					}
					if (currentEURUSD != null && !currentEURUSD.equals(previousEURUSD)) {
						logger.debug(CurrencyPair.EURUSD.getLabel() + " : " + currentEURUSD);
						previousMap.put(CurrencyPair.EURUSD.getValue(), currentEURUSD);
						process(CurrencyPair.EURUSD, currentEURUSD);
					}
					if (currentGBPUSD != null && !currentGBPUSD.equals(previousGBPUSD)) {
						logger.debug(CurrencyPair.GBPUSD.getLabel() + " : " + currentGBPUSD);
						previousMap.put(CurrencyPair.GBPUSD.getValue(), currentGBPUSD);
						process(CurrencyPair.GBPUSD, currentGBPUSD);
					}
					if (currentAUDUSD != null && !currentAUDUSD.equals(previousAUDUSD)) {
						logger.debug(CurrencyPair.AUDUSD.getLabel() + " : " + currentAUDUSD);
						previousMap.put(CurrencyPair.AUDUSD.getValue(), currentAUDUSD);
						process(CurrencyPair.AUDUSD, currentAUDUSD);
					}
					if (currentUSDCHF != null && !currentUSDCHF.equals(previousUSDCHF)) {
						logger.debug(CurrencyPair.USDCHF.getLabel() + " : " + currentUSDCHF);
						previousMap.put(CurrencyPair.USDCHF.getValue(), currentUSDCHF);
						process(CurrencyPair.USDCHF, currentUSDCHF);
					}
				} finally {
					conversation.disconnect();
				}
			} catch (DDEMLException e) {
				if (counter < 100) {
					logger.error(counter + " DDEMLException: 0x" + Integer.toHexString(e.getErrorCode()) + " "
							+ e.getMessage(), e);
					counter++;
				} else {
					throw new Exception(
							"DDEMLException: 0x" + Integer.toHexString(e.getErrorCode()) + " " + e.getMessage(), e);
				}
			} catch (DDEException e) {
				if (counter < 100) {
					logger.error(counter + " DDEClientException: " + e.getMessage(), e);
					counter++;
				} else {
					throw new Exception("DDEClientException: " + e.getMessage(), e);
				}
			}
		}
		logger.info("End !");
	}

	private void process(CurrencyPair currencyPair, String rateData) throws TradeException {
		if (rateData == null || rateData.length() == 0) {
			throw new TradeException(messageService.getMessage("fx.springbatch.truefx.tick.invalid", rateData));
		}

		String[] fields = rateData.split(" ");
		if (fields.length < 4)
			throw new TradeException(messageService.getMessage("fx.springbatch.truefx.tick.invalid", rateData));

		FXTick tick = new FXTick();
		// Check currency pair.
		tick.setCurrencyPair(currencyPair.getValue());

		// Check regist date.
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime dateTime = LocalDateTime.parse(fields[0] + " " + fields[1], formatter);
		tick.setRegistDate(dateTime);

		// Set bid price.
		tick.setBidPrice(Double.parseDouble(fields[2]));
		// Set ask price.
		tick.setAskPrice(Double.parseDouble(fields[3]));
		// Set mid price.
		tick.setMidPrice(0.5 * (tick.getBidPrice() + tick.getAskPrice()));

		// Do insert operation.
		TrnFXTick fxTick = new TrnFXTick();
		BeanUtils.copyProperties(tick, fxTick);
		fxTickService.operation(fxTick, OperationMode.NEW);
	}
}
