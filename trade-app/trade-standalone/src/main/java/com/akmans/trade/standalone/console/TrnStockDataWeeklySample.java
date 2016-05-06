package com.akmans.trade.standalone.console;

import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.akmans.trade.core.springdata.jpa.repositories.TrnStockDataWeeklyDao;
import com.akmans.trade.core.springdata.jpa.entities.TrnStockDataWeekly;
import com.akmans.trade.standalone.config.StandaloneConfig;

public class TrnStockDataWeeklySample {
	public static void main(String[] args) {
		// Initialize application context
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(StandaloneConfig.class);
		context.refresh();
		// Get dao object
		TrnStockDataWeeklyDao dao = context.getBean(TrnStockDataWeeklyDao.class);
 
        // Print all records
        List<TrnStockDataWeekly> data = (List<TrnStockDataWeekly>) dao.findAll();
        for (TrnStockDataWeekly item : data) {
            System.out.println(item);
        }
  
        // And finally count records
        System.out.println("Count stock weekly data records: " + dao.findAll().size());
 
        context.close();
	}
}