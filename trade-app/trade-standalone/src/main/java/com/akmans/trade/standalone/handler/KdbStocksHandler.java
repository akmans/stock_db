package com.akmans.trade.standalone.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.akmans.trade.core.Constants;
import com.akmans.trade.standalone.model.Stock;

/**
 * Class to parse Japan Stock Page.
 */

public class KdbStocksHandler extends HTMLEditorKit.ParserCallback {
	
	/** logger */
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(KdbStocksHandler.class);

    /** stock */
	private Stock stock = null;

    /** processing flag (parsing stock data) */
    private boolean processing = false;

    /** parse stock flag */
    private boolean stockFlag = false;

    /** parse item flag */
    private int itemFlag = 0;

    /** parse business day flag */
    private boolean businessDayFlag = false;

    /** business day */
    private Date businessDay;
    
	/** Parser result */
	private ArrayList<Stock> result = new ArrayList<Stock>();
	
	/**
	 * result setter
	 * @param result
	 */
	public void setResult(ArrayList<Stock> result) {
		this.result = result;
	}
	
	/**
	 * result getter
	 * @return
	 */
	public ArrayList<Stock> getResult() {
		return this.result;
	}
	
	/**
	 * タグ開始の処理メソッド
	 */
	public void handleStartTag(HTML.Tag tag, MutableAttributeSet attr, int pos) {
		logger.debug("start");
		// Open business day flag
		if (tag.equals(HTML.Tag.TITLE)) {
			businessDayFlag = true;
		}
		// Open stockFlag
		if (processing && tag.equals(HTML.Tag.TR)) {
			stockFlag = true;
			stock = new Stock();
		}
		// Handle TD Tag
		if (stockFlag && tag.equals(HTML.Tag.TD)) {
			itemFlag++;
		}
		// Handle A Tag (extract stock code from A tag)
		if (itemFlag == 1 && tag.equals(HTML.Tag.A)) {
			String href = (String)attr.getAttribute(HTML.Attribute.HREF);
			if (href.substring(href.length() - 1).equalsIgnoreCase(Constants.STOCK_TOKYO_SUFFIX)) {
				String code = href.substring(href.lastIndexOf("/") + 1);
				code = code.substring(0, code.lastIndexOf("-"));
				stock.setCode(code);
			} else {
				stockFlag = false;
				itemFlag = 0;
			}
			
		}
		// Handle Table Tag
		if (tag.equals(HTML.Tag.TABLE)) {
			String cls = (String)attr.getAttribute(HTML.Attribute.ID);
			// ID is "maintable"
			if ("maintable".equalsIgnoreCase(cls)) {
				processing = true;
				logger.debug("Parsing new stock data.");
			}
		}
		logger.debug("end");
	}

	
	/**
	 * 文書内容処理メソッド
	 */
	public void handleText(char[] data, int pos) {
		logger.debug("start");
		
		// parsing business day
		if (businessDayFlag) {
			Pattern pattern = Pattern.compile(Constants.DATE_PATTERN);
			Matcher matcher = pattern.matcher(new String(data));
			while (matcher.find()) {
				String day = matcher.group();
				SimpleDateFormat format = new SimpleDateFormat(Constants.DATE_FORMAT);
				try {
					businessDay = format.parse(day);
				} catch (ParseException pe) {
					logger.error(pe.getMessage(), pe);
				}
			}
			businessDayFlag = false;
		}

		if (stockFlag) {
			// Parsing stock code
			if (itemFlag == 1) {
//				stock.setCode(0);
			} else if (itemFlag == 2) {
				// Set registed date (No op!)
				stock.setRegistDate(businessDay);
			} else if (itemFlag == 3) {
				stock.setOpeningPrice(String.valueOf(data));
			} else if (itemFlag == 4) {
				stock.setHighPrice(String.valueOf(data));
			} else if (itemFlag == 5) {
				stock.setLowPrice(String.valueOf(data));
			} else if (itemFlag == 6) {
				stock.setFinishPrice(String.valueOf(data));
			} else if (itemFlag == 7) {
				stock.setTurnover(String.valueOf(data));
			} else if (itemFlag == 8) {
				stock.setTradingValue(String.valueOf(data));
			}
		}
		logger.debug("end");
	}

	/**
	 * タグ終了の処理メソッド
	 */
	public void handleEndTag(HTML.Tag tag, int pos) {
		logger.debug("start");
		// Close stockFlag
		if (stockFlag && tag.equals(HTML.Tag.TR)) {
			stockFlag = false;
			itemFlag = 0;
			if (!Constants.ZERO.equals(stock.getTurnover())) {
				result.add(stock);
			}
		}
		logger.debug("end");
	}
	
	/**
	 * main method
	 * テスト用メソッド
	 * @param args
	 */
	public static void main(String[] args) {
		logger.info("start");
		try {
			Resource resource = new ClassPathResource("abc.html");
			InputStream is = resource.getInputStream();
			InputStreamReader br = new InputStreamReader(is, "UTF-8");

			ParserDelegator pd = new ParserDelegator();
			KdbStocksHandler handler = new KdbStocksHandler();
			pd.parse(br, handler, true);
			br.close();
			
			ArrayList<Stock> list = handler.getResult();
			Iterator<Stock> it = list.iterator();
			while (it.hasNext()) {
				Stock stock = it.next();
				logger.info(stock.toString());
			}
			if (list.size() == 0) {
				logger.info("データがありませんでした。");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("end");
	}
}


