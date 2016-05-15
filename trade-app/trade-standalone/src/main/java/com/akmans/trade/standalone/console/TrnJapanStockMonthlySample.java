package com.akmans.trade.standalone.console;

import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.akmans.trade.core.springdata.jpa.repositories.TrnJapanStockMonthlyRepository;
import com.akmans.trade.core.springdata.jpa.entities.TrnJapanStockMonthly;
import com.akmans.trade.standalone.config.StandaloneConfig;

public class TrnJapanStockMonthlySample {
	public static void main(String[] args) {
		// Initialize application context
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(StandaloneConfig.class);
		context.refresh();
		// Get dao object
		TrnJapanStockMonthlyRepository dao = context.getBean(TrnJapanStockMonthlyRepository.class);
 
        // Print all records
        List<TrnJapanStockMonthly> data = (List<TrnJapanStockMonthly>) dao.findAll();
        for (TrnJapanStockMonthly item : data) {
            System.out.println(item);
        }
  
        // And finally count records
        System.out.println("Count stock monthly data records: " + dao.findAll().size());
 
        context.close();
	}
}