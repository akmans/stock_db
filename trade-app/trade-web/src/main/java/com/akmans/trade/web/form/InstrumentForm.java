package com.akmans.trade.web.form;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class InstrumentForm extends AbstractSimpleForm implements Serializable {
	private static final long serialVersionUID = 1L;

	/** code. */
	@NotNull(message = "{form.instrumentform.code.notnull}")
	@Min(value = 1, message = "{form.instrumentform.code.min}")
	private Integer code;

	/** name. */
	@Size(min = 1, max = 30, message = "{form.instrumentform.code.name}")
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
