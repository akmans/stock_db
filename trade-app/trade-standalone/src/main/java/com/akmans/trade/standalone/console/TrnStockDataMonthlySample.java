package com.akmans.trade.standalone.console;

import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.akmans.trade.core.springdata.jpa.dao.TrnStockDataMonthlyDao;
import com.akmans.trade.core.springdata.jpa.entities.TrnStockDataMonthly;
import com.akmans.trade.standalone.config.StandaloneConfig;

public class TrnStockDataMonthlySample {
	public static void main(String[] args) {
		// Initialize application context
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(StandaloneConfig.class);
		context.refresh();
		// Get dao object
		TrnStockDataMonthlyDao dao = context.getBean(TrnStockDataMonthlyDao.class);
 
        // Print all records
        List<TrnStockDataMonthly> data = (List<TrnStockDataMonthly>) dao.findAll();
        for (TrnStockDataMonthly item : data) {
            System.out.println(item);
        }
  
        // And finally count records
        System.out.println("Count stock monthly data records: " + dao.findAll().size());
 
        context.close();
	}
}