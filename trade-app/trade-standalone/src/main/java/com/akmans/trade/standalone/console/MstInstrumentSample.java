package com.akmans.trade.standalone.console;

import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.akmans.trade.core.springdata.jpa.entities.MstInstrument;
import com.akmans.trade.core.springdata.jpa.dao.MstInstrumentDao;
import com.akmans.trade.standalone.config.StandaloneConfig;

public class MstInstrumentSample {
	public static void main(String[] args) {
		// Initialize application context
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(StandaloneConfig.class);
		context.refresh();
		// Get dao object
		MstInstrumentDao dao = context.getBean(MstInstrumentDao.class);
 
        // Print all records
        List<MstInstrument> instruments = (List<MstInstrument>) dao.findAll();
        for (MstInstrument instrument : instruments) {
            System.out.println(instrument);
        }
  
        // And finally count records
        System.out.println("Count instrument records: " + dao.findAll().size());
 
        context.close();
	}
}