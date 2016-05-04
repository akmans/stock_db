package com.akmans.trade.core.springdata.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.Table;

@Entity
@Table(name = "mst_instrument")
//@SecondaryTables({
//		@SecondaryTable(name = "mst_sector33", pkJoinColumns = {
//				@PrimaryKeyJoinColumn(name = "sector33_code", referencedColumnName = "code") }),
//		@SecondaryTable(name = "mst_sector17", pkJoinColumns = {
//				@PrimaryKeyJoinColumn(name = "sector17_code", referencedColumnName = "code") }),
//		@SecondaryTable(name = "mst_scale", pkJoinColumns = {
//				@PrimaryKeyJoinColumn(name = "scale_code", referencedColumnName = "code") }),
//		@SecondaryTable(name = "mst_market", pkJoinColumns = {
//				@PrimaryKeyJoinColumn(name = "market_code", referencedColumnName = "code") }) })
public class MstInstrument extends AbstractEntity {

	@Id
	@Column(name = "code")
	private Integer code;

	@Column(name = "name", length = 100)
	private String name;

//	@Column(name = "sector33_code")
//	private Integer sector33Code;

//	@Column(table = "mst_sector33", name = "name")
//	private String sector33Name;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "sector33_code")
	private MstSector33 sector33;

//	@Column(name = "sector17_code")
//	private Integer sector17Code;

//	@Column(table = "mst_sector17", name = "name")
//	private String sector17Name;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "sector17_code")
	private MstSector17 sector17;


//	@Column(name = "scale_code")
//	private Integer scaleCode;

//	@Column(table = "mst_scale", name = "name")
//	private String scaleName;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "scale_code")
	private MstScale scale;

//	@Column(name = "market_code")
//	private Integer marketCode;

//	@Column(table = "mst_market", name = "name")
//	private String marketName;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "market_code")
	private MstMarket market;

	public void setCode(Integer code) {
		this.code = code;
	}

	public Integer getCode() {
		return this.code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

/*	public void setSector33Code(Integer sector33Code) {
		this.sector33Code = sector33Code;
	}

	public Integer getSector33Code() {
		return this.sector33Code;
	}

	public void setSector33Name(String sector33Name) {
		this.sector33Name = sector33Name;
	}

	public String getSector33Name() {
		return this.sector33Name;
	}
*/
	public void setSector33(MstSector33 sector33) {
		this.sector33 = sector33;
	}

	public MstSector33 getSector33() {
		return this.sector33;
	}

/*	public void setSector17Code(Integer sector17Code) {
		this.sector17Code = sector17Code;
	}

	public Integer getSector17Code() {
		return this.sector17Code;
	}

	public void setSector17Name(String sector17Name) {
		this.sector17Name = sector17Name;
	}

	public String getSector17Name() {
		return this.sector17Name;
	}
*/
	public void setSector17(MstSector17 sector17) {
		this.sector17 = sector17;
	}

	public MstSector17 getSector17() {
		return this.sector17;
	}

/*	public void setScaleCode(Integer scaleCode) {
		this.scaleCode = scaleCode;
	}

	public Integer getScaleCode() {
		return this.scaleCode;
	}

	public void setScaleName(String scaleName) {
		this.scaleName = scaleName;
	}

	public String getScaleName() {
		return this.scaleName;
	}
*/
	public void setScale(MstScale scale) {
		this.scale = scale;
	}

	public MstScale getScale() {
		return this.scale;
	}

/*	public void setMarketCode(Integer marketCode) {
		this.marketCode = marketCode;
	}

	public Integer getMarketCode() {
		return this.marketCode;
	}

	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}

	public String getMarketName() {
		return this.marketName;
	}
*/
	public void setMarket(MstMarket market) {
		this.market = market;
	}

	public MstMarket getMarket() {
		return this.market;
	}

	@Override
	public String toString() {
//		return this.getClass().getAnnotation(Table.class).name() + " [code=" + code + ", name=" + name
//				+ ", sector33_code=" + sector33Code + ", sector33_name=" + sector33Name + ", sector17_code=" + sector17Code
//				+ ", sector17_name=" + sector17Name + ", scale_code=" + scaleCode + ", scale_name=" + scaleName
//				+ ", market_code=" + marketCode + ", market_name=" + marketName + super.toString() + "]";
		return this.getClass().getAnnotation(Table.class).name() + " [code=" + code + ", name=" + name
				+ super.toString() + "] scale=[" + scale + "] market=[" + market + "] sector17=[" + sector17 + "] sector33=[" + sector33 + "]";
	}
}
