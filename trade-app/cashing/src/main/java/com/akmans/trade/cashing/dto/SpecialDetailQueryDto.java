package com.akmans.trade.cashing.dto;

import java.io.Serializable;

import com.akmans.trade.core.dto.AbstractQueryDto;

public class SpecialDetailQueryDto extends AbstractQueryDto implements Serializable {
	private static final long serialVersionUID = 1L;

	/** name. */
	private String name;

	/** itemCode */
	private Integer itemCode;

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
		return "[name=" + name + ", itemCode=" + itemCode + ", pageable=" + super.toString() + "]";
	}
}
