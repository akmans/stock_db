package com.akmans.trade.stock.springdata.jpa.keys;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class JapanStockLogKey implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "job_id", nullable = false)
	private String jobId;

	@Column(name = "process_date", nullable = false)
	private Date processDate;

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getJobId() {
		return this.jobId;
	}

	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}

	public Date getProcessDate() {
		return this.processDate;
	}

	@Override
	public String toString() {
		return "[job_id=" + jobId + ", process_date=" + processDate + "]";
	}
}