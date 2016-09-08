package com.akmans.trade.fx.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class TrueFX {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(TrueFX.class);

	@Autowired
	private Environment env;

	private String makeSession() {
		String sessionId = null;

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
			HttpURLConnection connection = null;
			try {
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
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		} catch (IOException e) {
			logger.error("An IOException occurred.", e);
		}
		// return sessionId.
		return sessionId;
	}

	public void ticker() {
		String sessionId = makeSession();
		
		if (sessionId != null && !sessionId.equals("not authorized")) {
			try {
				// Make request URL.
				StringBuffer sb = new StringBuffer(env.getRequiredProperty("truefx.url"));
				sb.append("?id=" + sessionId);
				URL obj = new URL(sb.toString());
				// Do request.
				HttpURLConnection connection = null;
				try {
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

						// print result.
						// ex. USD/JPY,1472849100026,103.,992,104.,000,103.837,104.091,103.945
						logger.debug(response.toString());
					} else {
						// Some error occurred.
						logger.error("GET request not worked, Response Code =[" + responseCode + "]");
					}
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			} catch (IOException e) {
				logger.error("An IOException occurred.", e);
			}
		}
	}
}