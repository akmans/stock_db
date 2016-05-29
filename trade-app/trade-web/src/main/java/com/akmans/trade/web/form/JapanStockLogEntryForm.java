package com.akmans.trade.web.form;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class JapanStockLogEntryForm extends AbstractSimpleForm implements Serializable {
	private static final long serialVersionUID = 1L;

	/** jobId. */
	@Size(min = 1, message = "{form.japanstocklogform.jobid.notnull}")
	private String jobId;

	/** processDate */
	@NotNull(message = "{form.japanstocklogform.processdate.notnull}")
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
