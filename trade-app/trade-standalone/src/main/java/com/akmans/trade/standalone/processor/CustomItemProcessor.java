package com.akmans.trade.standalone.processor;

import org.springframework.batch.item.ItemProcessor;

import com.akmans.trade.standalone.model.Report;

public class CustomItemProcessor implements ItemProcessor<Report, Report> {

	public Report process(Report item) throws Exception {
		
		System.out.println("Processing..." + item);
		return item;
	}
}
