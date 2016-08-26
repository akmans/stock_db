package com.akmans.trade.cashing.web.form;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.akmans.trade.core.web.form.AbstractSimpleForm;

public class SpecialItemForm extends AbstractSimpleForm implements Serializable {
	private static final long serialVersionUID = 1L;

	/** code. */
	private Integer code;

	/** name. */
	@NotEmpty
	@Size(min = 1, max = 30)
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
