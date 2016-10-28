package com.akmans.trade.fx.console;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.akmans.trade.core.enums.CurrencyPair;
import com.pretty_tools.dde.DDEException;
import com.pretty_tools.dde.DDEMLException;
import com.pretty_tools.dde.client.DDEClientConversation;

public class MonitorDDEApp {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(GenerateFXTickApp.class);

	public static void main(String[] args)
    {
		logger.info("Start !");
        try
        {
            // DDE client
            final DDEClientConversation conversation = new DDEClientConversation();
            // We can use UNICODE format if server prefers it
            //conversation.setTextFormat(ClipboardFormat.CF_UNICODETEXT);

            conversation.setTimeout(3000);
            conversation.connect("Excel", "Sheet1");
            
    		Map<String, String> previousMap = new HashMap<String, String>();
    		String stopFlag = null;

            try
            {
            	String oldValue = null;
            	while("RUNNING".equals(stopFlag)) {
                    // Requesting A1 value
            		String currentUSDJPY = conversation.request("R3C3");
            		String currentEURJPY = conversation.request("R4C3");
            		String currentAUDJPY = conversation.request("R5C3");
            		String currentGBPJPY = conversation.request("R6C3");
            		String currentCHFJPY = conversation.request("R7C3");
            		String currentEURUSD = conversation.request("R8C3");
            		String currentGBPUSD = conversation.request("R9C3");
            		String currentAUDUSD = conversation.request("R10C3");
            		String currentUSDCHF = conversation.request("R11C3");
            		stopFlag = conversation.request("R13C3");
/*            		if (currentUSDJPY != null && !currentUSDJPY.trim().equals(previousMap.get(CurrencyPair.USDJPY.getValue()))) {
            			logger.debug(currentUSDJPY);
            			process(CurrencyPair.USDJPY, currentUSDJPY);
            		} else if (!currentValue.equals(oldValue)) {
            			System.out.println("USDJPY value: " + currentValue);
            			oldValue = currentValue;
            		}*/
            	}
                // Requesting A1 value
//                System.out.println("A1 value: " + conversation.request("R1C1"));
                // Changing cell A1 value to "We did it!"
//                conversation.poke("R1C1", "We did it!");
//                conversation.poke("R2C2", "We did it again!".getBytes(), ClipboardFormat.CF_TEXT);
                // Sending "close()" command
//                conversation.execute("[close()]");
                // or we can use byte array to send command
                //conversation.execute("[close()]\0".getBytes());
            }
            finally
            {
                conversation.disconnect();
            }
        }
        catch (DDEMLException e)
        {
            System.out.println("DDEMLException: 0x" + Integer.toHexString(e.getErrorCode()) + " " + e.getMessage());
        }
        catch (DDEException e)
        {
            System.out.println("DDEClientException: " + e.getMessage());
        }
        catch (Exception e)
        {
            System.out.println("Exception: " + e);
        }
		logger.info("End !");
    }
}
