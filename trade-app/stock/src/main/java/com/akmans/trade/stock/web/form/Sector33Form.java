package com.akmans.trade.stock.web.form;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.akmans.trade.core.web.form.AbstractSimpleForm;

public class Sector33Form extends AbstractSimpleForm implements Serializable {
	private static final long serialVersionUID = 1L;

	/** code. */
	@NotNull(message = "{form.sector33form.code.notnull}")
	@Min(value = 1, message = "{form.sector33form.code.min}")
	private Integer code;

	/** name. */
	@Size(min = 1, max = 30, message = "{form.sector33form.code.name}")
	private String name;

	/**
	 * code getter.<BR>
	 * 
	 * @return code
	 */
	public Integer getCode() {
		return this.code;
	}

	/**
	 * code setter.<BR>
	 * 
	 * @param code
	 */
	public void setCode(Integer code) {
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

	@Override
	public String toString() {
		return "[code=" + code + ", name=" + name + super.toString() + "]";
	}
}
