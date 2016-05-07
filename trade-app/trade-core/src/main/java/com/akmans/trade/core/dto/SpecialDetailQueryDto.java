package com.akmans.trade.core.dto;

import java.io.Serializable;

import org.springframework.data.domain.Pageable;

public class SpecialDetailQueryDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Pageable pageable;

	/** name. */
	private String name;

	/** itemCode */
	private Integer itemCode;

	/**
	 * pageable getter.<BR>
	 * 
	 * @return pageable
	 */
	public Pageable getPageable() {
		return this.pageable;
	}

	/**
	 * pageable setter.<BR>
	 * 
	 * @param pageable
	 */
	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
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

	@Override
    public String toString() {
        return "[name=" + name
        		+ ", itemCode=" + itemCode + "]";
    }
}
