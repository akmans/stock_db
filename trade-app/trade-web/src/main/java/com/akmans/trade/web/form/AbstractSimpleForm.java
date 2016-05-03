package com.akmans.trade.web.form;

import java.time.ZonedDateTime;

import com.akmans.trade.core.enums.OperationMode;

public abstract class AbstractSimpleForm {
	private ZonedDateTime createdDate;

	private ZonedDateTime updatedDate;

	private String createdBy;

	private String updatedBy;

	private OperationMode operationMode;

	public void setCreatedDate(ZonedDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public ZonedDateTime getCreatedDate() {
		return this.createdDate;
	}

	public void setUpdatedDate(ZonedDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}

	public ZonedDateTime getUpdatedDate() {
		return this.updatedDate;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getUpdatedBy() {
		return this.updatedBy;
	}

	public void setOperationMode(OperationMode operationMode) {
		this.operationMode = operationMode;
	}

	public OperationMode getOperationMode() {
		return this.operationMode;
	}

	@Override
    public String toString() {
        return " created_by=" + createdBy
        		+ ", created_date=" + createdDate
        		+ ", updated_by=" + updatedBy
        		+ ", updated_date=" + updatedDate
        		+ ", operationMode=" + operationMode;
	}
}
