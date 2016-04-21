package com.akmans.trade.standalone.console;

import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.akmans.trade.core.springdata.jpa.dao.TrnLoadedLogDao;
import com.akmans.trade.core.springdata.jpa.entities.TrnLoadedLog;
import com.akmans.trade.standalone.config.StandaloneConfig;

public class TrnLoadedLogSample {
	public static void main(String[] args) {
		// Initialize application context
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(StandaloneConfig.class);
		context.refresh();
		// Get dao object
		TrnLoadedLogDao dao = context.getBean(TrnLoadedLogDao.class);
 
        // Print all records
        List<TrnLoadedLog> logs = (List<TrnLoadedLog>) dao.findAll();
        for (TrnLoadedLog log : logs) {
            System.out.println(log);
        }
  
        // And finally count records
        System.out.println("Count loaded log records: " + dao.findAll().size());
 
        context.close();
	}
}