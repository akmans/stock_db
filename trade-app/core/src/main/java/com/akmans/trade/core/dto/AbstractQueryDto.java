package com.akmans.trade.core.dto;

import org.springframework.data.domain.Pageable;

public abstract class AbstractQueryDto {

	private Pageable pageable;

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

	@Override
	public String toString() {
		return "[pageable=" + pageable.toString() + "]";
	}
}
