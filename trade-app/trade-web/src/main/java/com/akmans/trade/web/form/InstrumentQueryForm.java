package com.akmans.trade.web.form;

import java.io.Serializable;

public class InstrumentQueryForm extends AbstractQueryForm implements Serializable {
	private static final long serialVersionUID = 1L;

	/** code */
	private Long code;

	/** sector17 */
	private Integer sector17;

	/** sector33 */
	private Integer sector33;

	/** scale */
	private Integer scale;

	/** market */
	private Integer market;

	/** onboard. */
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
		return "[code=" + code + ", sector17=" + sector17 + ", sector33=" + sector33 + ", scale=" + scale + ", market="
				+ market + ", onboard=" + onboard + "]";
	}
}
