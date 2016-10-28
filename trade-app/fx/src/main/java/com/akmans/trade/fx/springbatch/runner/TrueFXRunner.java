package com.akmans.trade.fx.springbatch.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.akmans.trade.core.enums.CurrencyPair;
import com.akmans.trade.core.enums.OperationMode;
import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.MessageService;
import com.akmans.trade.fx.model.FXTick;
import com.akmans.trade.fx.service.FXTickService;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXTick;

@Component
public class TrueFXRunner {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(TrueFXRunner.class);

	private Environment env;

	private FXTickService fxTickService;

	private MessageService messageService;

	@Autowired
	TrueFXRunner(FXTickService fxTickService, MessageService messageService, Environment env) {
		this.fxTickService = fxTickService;
		this.messageService = messageService;
		this.env = env;
	}

	private String makeSession() throws TradeException {
		String sessionId = null;
		HttpURLConnection connection = null;

		try {
			// Make request URL.
			StringBuffer sb = new StringBuffer(env.getRequiredProperty("truefx.url"));
			sb.append("?u=" + env.getRequiredProperty("truefx.username"));
			sb.append("&p=" + env.getRequiredProperty("truefx.password"));
			sb.append("&q=forexRates");
			sb.append("&c=" + env.getRequiredProperty("truefx.currencypairs"));
			sb.append("&f=" + env.getRequiredProperty("truefx.format"));
			sb.append("&n=" + env.getRequiredProperty("truefx.snapshot"));
			URL obj = new URL(sb.toString());
			// Do request.
			connection = (HttpURLConnection) obj.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			int responseCode = connection.getResponseCode();
			logger.debug("GET Response Code :: " + responseCode);
			// Check request result.
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				// Get response.
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				// Get sessionId(the response body is sessionId).
				sessionId = response.toString();
				// print result
				logger.debug(response.toString());
			} else {
				// Some error occurred.
				logger.error("GET request not worked, Response Code =[" + responseCode + "]");
			}
		} catch (IOException e) {
			logger.error("An IOException occurred.", e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		// Check sessionId.
		if (sessionId == null || sessionId.equals("not authorized")) {
			throw new TradeException(
					messageService.getMessage("fx.springbatch.truefx.created.session.failed", sessionId));
		}

		// return sessionId.
		return sessionId;
	}

	private List<String> parseResponse(String line) throws TradeException {
		// QuoteFormat:"EUR/USD,1326226727808,1.27,445,1.27,453,1.27283,1.27284,1.27285"
		// QuoteFormat:"USD/JPY,1253890249628,89.,897,89.,907,89.763,90.619,90.526"
		if (line == null)
			throw new TradeException(messageService.getMessage("fx.springbatch.truefx.rate.invalid", line));

		int indexUSDJPY = line.indexOf(CurrencyPair.USDJPY.getLabel());
		int indexEURJPY = line.indexOf(CurrencyPair.EURJPY.getLabel());
		int indexAUDJPY = line.indexOf(CurrencyPair.AUDJPY.getLabel());
		int indexGBPJPY = line.indexOf(CurrencyPair.GBPJPY.getLabel());
		int indexCHFJPY = line.indexOf(CurrencyPair.CHFJPY.getLabel());
		int indexEURUSD = line.indexOf(CurrencyPair.EURUSD.getLabel());
		int indexGBPUSD = line.indexOf(CurrencyPair.GBPUSD.getLabel());
		int indexAUDUSD = line.indexOf(CurrencyPair.AUDUSD.getLabel());
		int indexUSDCHF = line.indexOf(CurrencyPair.USDCHF.getLabel());

		if (indexUSDJPY < 0 && indexEURJPY < 0 && indexAUDJPY < 0 && indexGBPJPY < 0 && indexCHFJPY < 0
				&& indexEURUSD < 0 && indexGBPUSD < 0 && indexAUDUSD < 0 && indexUSDCHF < 0) {
			throw new TradeException(messageService.getMessage("fx.springbatch.truefx.rate.invalid", line));
		}

		ArrayList<Integer> indexes = new ArrayList<Integer>();
		if (indexUSDJPY >= 0)
			indexes.add(indexUSDJPY);
		if (indexEURJPY >= 0)
			indexes.add(indexEURJPY);
		if (indexAUDJPY >= 0)
			indexes.add(indexAUDJPY);
		if (indexGBPJPY >= 0)
			indexes.add(indexGBPJPY);
		if (indexCHFJPY >= 0)
			indexes.add(indexCHFJPY);
		if (indexEURUSD >= 0)
			indexes.add(indexEURUSD);
		if (indexGBPUSD >= 0)
			indexes.add(indexGBPUSD);
		if (indexAUDUSD >= 0)
			indexes.add(indexAUDUSD);
		if (indexUSDCHF >= 0)
			indexes.add(indexUSDCHF);
		// Sort the indexes.
		Collections.sort(indexes);

		List<String> data = new ArrayList<String>();
		// Split currency data.
		for (int i = 0; i < indexes.size(); i++) {
			if (i == indexes.size() - 1) {
				data.add(line.substring(indexes.get(i)).trim());
			} else {
				data.add(line.substring(indexes.get(i), indexes.get(i + 1)).trim());
			}
		}
		return data;
	}

	private FXTick parseQuote(String currencyData) throws TradeException {
		if (currencyData == null || currencyData.length() == 0) {
			throw new TradeException(messageService.getMessage("fx.springbatch.truefx.tick.invalid", currencyData));
		}

		String[] fields = currencyData.split(",");
		if (fields.length < 9)
			throw new TradeException(messageService.getMessage("fx.springbatch.truefx.tick.invalid", currencyData));

		FXTick tick = new FXTick();
		// Check currency pair.
		if (CurrencyPair.get(fields[0].replace("/", "").toLowerCase()) == null) {
			// throw exception
			throw new TradeException(messageService.getMessage("fx.springbatch.truefx.tick.invalid", currencyData));
		} else {
			tick.setCurrencyPair(fields[0].replace("/", "").toLowerCase());
		}

		// Check regist date.
		long millisecond = Long.parseLong(fields[1]);
		LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(millisecond), ZoneId.of("GMT"));
		tick.setRegistDate(date);

		// Check big price.
		tick.setBidPrice(Double.parseDouble(fields[2] + fields[3]));

		// Check ask price.
		tick.setAskPrice(Double.parseDouble(fields[4] + fields[5]));

		// Check mid price.
		tick.setMidPrice(0.5 * (tick.getBidPrice() + tick.getAskPrice()));

		return tick;
	}

	private boolean keepRunning() throws TradeException {
		String stopFilePath = env.getRequiredProperty("truefx.stopfile");
		if (stopFilePath == null || stopFilePath.length() == 0) {
			throw new TradeException(messageService.getMessage("fx.springbatch.truefx.stopfile.invalid", stopFilePath));
		}

		return !new File(stopFilePath).exists();
	}

	private void closeSession(String sessionId) {
		logger.debug("Close session.");
		HttpURLConnection connection = null;
		try {
			// Make request URL.
			StringBuffer sb = new StringBuffer(env.getRequiredProperty("truefx.url"));
			sb.append("?di=" + sessionId);
			URL obj = new URL(sb.toString());
			// Do request.
			connection = (HttpURLConnection) obj.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			int responseCode = connection.getResponseCode();
			logger.debug("GET Response Code :: " + responseCode);
			// Check request result.
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				// Get response.
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				// print result
				logger.info(response.toString());
			} else {
				// Some error occurred.
				logger.error("GET request not worked, Response Code =[" + responseCode + "]");
			}
		} catch (IOException e) {
			logger.error("An IOException occurred.", e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public void execute() throws TradeException {
		// Get session id.
		String sessionId = makeSession();
		HttpURLConnection connection = null;
		Map<String, FXTick> previousMap = new HashMap<String, FXTick>();
		int retryCnt = 0;

		System.out.println("Here!");
		// Keep running.
		while (retryCnt < 100 && keepRunning()) {
			try {
				// Make request URL.
				StringBuffer sb = new StringBuffer(env.getRequiredProperty("truefx.url"));
				sb.append("?id=" + sessionId);
				URL obj = new URL(sb.toString());
				// Do request.
				connection = (HttpURLConnection) obj.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("User-Agent", "Mozilla/5.0");
				int responseCode = connection.getResponseCode();
				logger.debug("GET Response Code :: " + responseCode);
				// Check request result.
				if (responseCode == HttpURLConnection.HTTP_OK) {
					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();
					// Get response.
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();

					// None content, skip to next loop.
					if (response.toString().length() == 0) {
						retryCnt++;
						logger.info("No data received!" + retryCnt);
						Thread.sleep(retryCnt * 1000l);
						continue;
					} else {
						// Reset count.
						retryCnt = 0;
					}
					List<String> ticks = parseResponse(response.toString());
					// result.Ex.USD/JPY,1472849100026,103.,992,104.,000,103.837,104.091,103.945
					for (String strTick : ticks) {
						logger.debug("{}", parseQuote(strTick));
						FXTick tick = parseQuote(strTick);
						// Get previous tick.
						FXTick previous = previousMap.get(tick.getCurrencyPair());
						if (previous == null || previous.getMidPrice() != tick.getMidPrice()) {
							// do insert db
							TrnFXTick fxTick = new TrnFXTick();
							BeanUtils.copyProperties(tick, fxTick);
							fxTickService.operation(fxTick, OperationMode.NEW);
						}
						// Put tick into map.
						previousMap.put(tick.getCurrencyPair(), tick);
					}
				} else {
					// Some error occurred.
					logger.error("GET request not worked, Response Code =[" + responseCode + "]");
				}
			} catch (IOException | InterruptedException e) {
				logger.error("An Exception occurred.", e);
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		}

		// Terminate session.
		closeSession(sessionId);
	}

	// @Scheduled(fixedDelay = 3600000)
	// @Scheduled(cron = "0 0 8 * * TUE")
	public void run() {
		logger.info("TrueFXRunner start!!!");
		try {
			SecurityContext ctx = SecurityContextHolder.createEmptyContext();
			ctx.setAuthentication(authenticate());
			SecurityContextHolder.setContext(ctx);
			execute();
		} catch (Exception e) {
			logger.error("Some errors occurred!", e);
		} finally {
			SecurityContextHolder.clearContext();
		}
		logger.info("TrueFXRunner end!!!");
	}

	private Authentication authenticate() {
		final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("USER"));
		return new UsernamePasswordAuthenticationToken("scheduler", "scheduler", authorities);
	}
}