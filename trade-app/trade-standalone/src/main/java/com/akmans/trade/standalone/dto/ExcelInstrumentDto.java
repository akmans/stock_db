package com.akmans.trade.standalone.dto;

import org.javafunk.excelparser.annotations.ExcelField;
import org.javafunk.excelparser.annotations.ExcelObject;
import org.javafunk.excelparser.annotations.ParseType;

@ExcelObject(parseType = ParseType.ROW, start = 2, end = 4000)
public class ExcelInstrumentDto {
	@ExcelField(position = 1)
	private String date;

	@ExcelField(position = 2)
	private String code;

	@ExcelField(position = 3)
	private String name;

	@ExcelField(position = 4)
	private String marketName;

	@ExcelField(position = 5)
	private String sector33;

	@ExcelField(position = 6)
	private String sector33Name;

	@ExcelField(position = 7)
	private String sector17;

	@ExcelField(position = 8)
	private String sector17Name;

	@ExcelField(position = 9)
	private String scale;

	@ExcelField(position = 10)
	private String scaleName;

	public void setDate(String date) {
		this.date = date;
	}

	public String getDate() {
		return this.date;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}

	public String getMarketName() {
		return this.marketName;
	}

	public void setSector33(String sector33) {
		this.sector33 = sector33;
	}

	public String getSector33() {
		return this.sector33;
	}

	public void setSector33Name(String sector33Name) {
		this.sector33Name = sector33Name;
	}

	public String getSector33Name() {
		return this.sector33Name;
	}

	public void setSector17(String sector17) {
		this.sector17 = sector17;
	}

	public String getSector17() {
		return this.sector17;
	}

	public void setSector17Name(String sector17Name) {
		this.sector17Name = sector17Name;
	}

	public String getSector17Name() {
		return this.sector17Name;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public String getScale() {
		return this.scale;
	}

	public void setScaleName(String scaleName) {
		this.scaleName = scaleName;
	}

	public String getScaleName() {
		return this.scaleName;
	}

	@Override
	public String toString() {
		return "ExcelInstrumentDto [date=" + date + ", code=" + code + ", name=" + name + ", marketName=" + marketName
				+ ", sector33=" + sector33 + ", sector33Name=" + sector33Name + ", sector17=" + sector17
				+ ", sector17Name=" + sector17Name + ", scale=" + scale + ", scaleName=" + scaleName + "]";
	}
}
