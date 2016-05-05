package com.akmans.trade.web.form;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Min;
//import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SpecialDetailEntryForm extends AbstractSimpleForm implements Serializable {
	private static final long serialVersionUID = 1L;

	/** code. */
//	@NotNull(message="{form.specialdetailform.code.notnull}")
//	@Min(value = 1, message="{form.specialdetailform.code.min}")
	private Long code;

	/** name. */
	@Size(min=1, max=30, message="{form.specialdetailform.code.name}")
	private String name;

	@Size(min=1, max=100, message="{form.specialdetailform.code.detail}")
	private String detail;

	@NotNull(message="{form.specialdetailform.amount.notnull}")
    private Long amount;

	@NotNull(message="{form.specialdetailform.itemcode.notnull}")
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
        return "[code=" + code
        		+ ", name=" + name
        		+ ", itemCode=" + itemCode
        		+ ", itemName=" + itemName
        		+ ", detail=" + detail
        		+ ", amount=" + amount
        		+ super.toString() + "]";
    }
}
