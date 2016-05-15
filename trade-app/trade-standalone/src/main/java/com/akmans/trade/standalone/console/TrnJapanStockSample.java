package com.akmans.trade.standalone.console;

import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.akmans.trade.core.springdata.jpa.repositories.TrnJapanStockRepository;
import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStock;
import com.akmans.trade.standalone.config.StandaloneConfig;

public class TrnJapanStockSample {
	public static void main(String[] args) {
		// Initialize application context
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(StandaloneConfig.class);
		context.refresh();
		// Get dao object
		TrnJapanStockRepository dao = context.getBean(TrnJapanStockRepository.class);
 
        // Print all records
        List<TrnJapanStock> data = (List<TrnJapanStock>) dao.findAll();
        for (TrnJapanStock item : data) {
            System.out.println(item);
        }
  
        // And finally count records
        System.out.println("Count stock data records: " + dao.findAll().size());
 
        context.close();
	}
}