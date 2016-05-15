package com.akmans.trade.standalone.console;

import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.akmans.trade.core.springdata.jpa.repositories.TrnJapanStockWeeklyRepository;
import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStockWeekly;
import com.akmans.trade.standalone.config.StandaloneConfig;

public class TrnJapanStockWeeklySample {
	public static void main(String[] args) {
		// Initialize application context
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(StandaloneConfig.class);
		context.refresh();
		// Get dao object
		TrnJapanStockWeeklyRepository dao = context.getBean(TrnJapanStockWeeklyRepository.class);
 
        // Print all records
        List<TrnJapanStockWeekly> data = (List<TrnJapanStockWeekly>) dao.findAll();
        for (TrnJapanStockWeekly item : data) {
            System.out.println(item);
        }
  
        // And finally count records
        System.out.println("Count stock weekly data records: " + dao.findAll().size());
 
        context.close();
	}
}