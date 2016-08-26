package com.akmans.trade.cashing.web.form;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.akmans.trade.core.web.form.AbstractSimpleForm;

public class SpecialDetailEntryForm extends AbstractSimpleForm implements Serializable {
	private static final long serialVersionUID = 1L;

	/** code. */
	private Long code;

	@NotNull
	private Date registDate;

	/** name. */
	@NotEmpty
	@Size(min = 1, max = 30)
	private String name;

	@Size(min = 0, max = 30)
	private String detail;

	@NotNull
	private Long amount;

	@NotNull
	private Integer itemCode;

	private String itemName;

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
	 * registDate getter.<BR>
	 * 
	 * @return registDate
	 */
	public Date getRegistDate() {
		return this.registDate;
	}

	/**
	 * registDate setter.<BR>
	 * 
	 * @param registDate
	 */
	public void setRegistDate(Date registDate) {
		this.registDate = registDate;
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

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getDetail() {
		return this.detail;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Long getAmount() {
		return this.amount;
	}

	/**
	 * itemCode getter.<BR>
	 * 
	 * @return itemCode
	 */
	public Integer getItemCode() {
		return this.itemCode;
	}

	/**
	 * itemCode setter.<BR>
	 * 
	 * @param itemCode
	 */
	public void setItemCode(Integer itemCode) {
		this.itemCode = itemCode;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemName() {
		return this.itemName;
	}

	@Override
	public String toString() {
		return "[code=" + code + ", name=" + name + ", itemCode=" + itemCode + ", itemName=" + itemName + ", detail="
				+ detail + ", amount=" + amount + super.toString() + "]";
	}
}
