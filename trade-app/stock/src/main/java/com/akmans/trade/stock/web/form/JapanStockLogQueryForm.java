package com.akmans.trade.stock.web.form;

import java.io.Serializable;
import java.util.Date;

import com.akmans.trade.core.web.form.AbstractQueryForm;

public class JapanStockLogQueryForm extends AbstractQueryForm implements Serializable {
	private static final long serialVersionUID = 1L;

	/** jobId. */
	private String jobId;

	/** processDate */
	private Date processDate;

	/**
	 * jobId getter.<BR>
	 * 
	 * @return jobId
	 */
	public String getJobId() {
		return this.jobId;
	}

	/**
	 * jobId setter.<BR>
	 * 
	 * @param jobId
	 */
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	/**
	 * processDate getter.<BR>
	 * 
	 * @return processDate
	 */
	public Date getProcessDate() {
		return this.processDate;
	}

	/**
	 * processDate setter.<BR>
	 * 
	 * @param processDate
	 */
	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}

	@Override
	public String toString() {
		return "[jobId=" + jobId + ", processDate=" + processDate + "]";
	}
}
