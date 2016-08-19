package com.akmans.trade.stock.web.form;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.akmans.trade.core.enums.JapanHoliday;
import com.akmans.trade.core.web.form.AbstractSimpleForm;

public class InstrumentEntryForm extends AbstractSimpleForm implements Serializable {
	private static final long serialVersionUID = 1L;

	/** code. */
	@NotNull(message = "{form.instrumentform.code.notnull}")
	@Min(value = 1, message = "{form.instrumentform.code.min}")
	private Long code;

	/** name. */
	@Size(min = 1, max = 30, message = "{form.instrumentform.name.length}")
	private String name;

	/** sector17 */
	private Integer sector17;

	private String sector17Name;

	/** sector33 */
	private Integer sector33;

	private String sector33Name;

	/** scale */
	private Integer scale;

	private String scaleName;

	/** market */
	private Integer market;

	private String marketName;

	/** onboard. */
	@NotNull(message = "{form.instrumentform.onboard.notnull}")
	private String onboard;

	/**
	 * code getter.<BR>
	 * 
	 * @return code
	 */
	public Long getCode() {
		return this.code;
	}

	/**
	 * code setter.<BR>
	 * 
	 * @param code
	 */
	public void setCode(Long code) {
		this.code = code;
	}

	/**
	 * name getter.<BR>
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * name setter.<BR>
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * sector17 getter.<BR>
	 * 
	 * @return sector17
	 */
	public Integer getSector17() {
		return this.sector17;
	}

	/**
	 * sector17 setter.<BR>
	 * 
	 * @param sector17
	 */
	public void setSector17(Integer sector17) {
		this.sector17 = sector17;
	}

	public void setSector17Name(String sector17Name) {
		this.sector17Name = sector17Name;
	}

	public String getSector17Name() {
		return this.sector17Name;
	}

	/**
	 * sector33 getter.<BR>
	 * 
	 * @return sector33
	 */
	public Integer getSector33() {
		return this.sector33;
	}

	/**
	 * sector33 setter.<BR>
	 * 
	 * @param sector33
	 */
	public void setSector33(Integer sector33) {
		this.sector33 = sector33;
	}

	public void setSector33Name(String sector33Name) {
		this.sector33Name = sector33Name;
	}

	public String getSector33Name() {
		return this.sector33Name;
	}

	/**
	 * scale getter.<BR>
	 * 
	 * @return scale
	 */
	public Integer getScale() {
		return this.scale;
	}

	/**
	 * scale setter.<BR>
	 * 
	 * @param scale
	 */
	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public void setScaleName(String scaleName) {
		this.scaleName = scaleName;
	}

	public String getScaleName() {
		return this.scaleName;
	}

	/**
	 * market getter.<BR>
	 * 
	 * @return market
	 */
	public Integer getMarket() {
		return this.market;
	}

	/**
	 * market setter.<BR>
	 * 
	 * @param market
	 */
	public void setMarket(Integer market) {
		this.market = market;
	}

	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}

	public String getMarketName() {
		return this.marketName;
	}

	/**
	 * onboard getter.<BR>
	 * 
	 * @return onboard
	 */
	public String getOnboard() {
		return this.onboard;
	}

	/**
	 * onboard setter.<BR>
	 * 
	 * @param onboard
	 */
	public void setOnboard(String onboard) {
		this.onboard = onboard;
	}

	@Override
	public String toString() {
		return "[code=" + code + ", name=" + name + ", sector17=" + sector17 + ", sector17Name=" + sector17Name
				+ ", sector33=" + sector33 + ", sector33Name=" + sector33Name + ", scale=" + scale + ", scaleName="
				+ scaleName + ", market=" + market + ", marketName=" + marketName + ", onboard=" + onboard
				+ super.toString() + "]";
	}
}
