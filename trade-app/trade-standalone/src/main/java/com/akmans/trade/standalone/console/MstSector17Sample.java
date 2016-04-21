package com.akmans.trade.standalone.console;

import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.akmans.trade.core.springdata.jpa.dao.MstSector17Dao;
import com.akmans.trade.core.springdata.jpa.entities.MstSector17;
import com.akmans.trade.standalone.config.StandaloneConfig;

public class MstSector17Sample {
	public static void main(String[] args) {
		// Initialize application context
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(StandaloneConfig.class);
		context.refresh();
		// Get dao object
		MstSector17Dao dao = context.getBean(MstSector17Dao.class);
 
        // Print all records
        List<MstSector17> sector17s = (List<MstSector17>) dao.findAll();
        for (MstSector17 sector17 : sector17s) {
            System.out.println(sector17);
        }
  
        // And finally count records
        System.out.println("Count sector17 records: " + dao.findAll().size());
 
        context.close();
	}
}