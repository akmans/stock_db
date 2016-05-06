package com.akmans.trade.standalone.console;

import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.akmans.trade.core.springdata.jpa.repositories.MstScaleRepository;
import com.akmans.trade.core.springdata.jpa.entities.MstScale;
import com.akmans.trade.standalone.config.StandaloneConfig;

public class MstScaleSample {
	public static void main(String[] args) {
		// Initialize application context
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(StandaloneConfig.class);
		context.refresh();
		// Get dao object
		MstScaleRepository dao = context.getBean(MstScaleRepository.class);
 
        // Print all records
        List<MstScale> scales = (List<MstScale>) dao.findAll();
        for (MstScale scale : scales) {
            System.out.println(scale);
        }
  
        // And finally count records
        System.out.println("Count scale records: " + dao.findAll().size());
 
        context.close();
	}
}