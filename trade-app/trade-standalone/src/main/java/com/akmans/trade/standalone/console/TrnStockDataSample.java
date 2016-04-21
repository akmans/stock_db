package com.akmans.trade.standalone.console;

import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.akmans.trade.core.springdata.jpa.dao.TrnStockDataDao;
import com.akmans.trade.core.springdata.jpa.entities.TrnStockData;
import com.akmans.trade.standalone.config.StandaloneConfig;

public class TrnStockDataSample {
	public static void main(String[] args) {
		// Initialize application context
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(StandaloneConfig.class);
		context.refresh();
		// Get dao object
		TrnStockDataDao dao = context.getBean(TrnStockDataDao.class);
 
        // Print all records
        List<TrnStockData> data = (List<TrnStockData>) dao.findAll();
        for (TrnStockData item : data) {
            System.out.println(item);
        }
  
        // And finally count records
        System.out.println("Count stock data records: " + dao.findAll().size());
 
        context.close();
	}
}