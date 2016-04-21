package com.akmans.trade.standalone.console;

import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.akmans.trade.core.springdata.jpa.dao.MstSector33Dao;
import com.akmans.trade.core.springdata.jpa.entities.MstSector33;
import com.akmans.trade.standalone.config.StandaloneConfig;

public class MstSector33Sample {
	public static void main(String[] args) {
		// Initialize application context
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(StandaloneConfig.class);
		context.refresh();
		// Get dao object
		MstSector33Dao dao = context.getBean(MstSector33Dao.class);
 
        // Print all records
        List<MstSector33> sector33s = (List<MstSector33>) dao.findAll();
        for (MstSector33 sector33 : sector33s) {
            System.out.println(sector33);
        }
  
        // And finally count records
        System.out.println("Count sector33 records: " + dao.findAll().size());
 
        context.close();
	}
}