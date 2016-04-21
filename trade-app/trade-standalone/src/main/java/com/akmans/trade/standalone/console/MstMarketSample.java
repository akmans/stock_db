package com.akmans.trade.standalone.console;

import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.akmans.trade.core.springdata.jpa.dao.MstMarketDao;
import com.akmans.trade.core.springdata.jpa.entities.MstMarket;
import com.akmans.trade.standalone.config.StandaloneConfig;

public class MstMarketSample {
	public static void main(String[] args) {
		// Initialize application context
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(StandaloneConfig.class);
		context.refresh();
		// Get dao object
		MstMarketDao dao = context.getBean(MstMarketDao.class);
 
        // Print all records
        List<MstMarket> markets = (List<MstMarket>) dao.findAll();
        for (MstMarket market : markets) {
            System.out.println(market);
        }
  
        // And finally count records
        System.out.println("Count market records: " + dao.findAll().size());
 
        context.close();
	}
}