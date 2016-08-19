package com.akmans.trade.stock.dto;

import java.io.Serializable;
import java.util.Date;

import com.akmans.trade.core.dto.AbstractQueryDto;

public class JapanStockLogQueryDto extends AbstractQueryDto implements Serializable {
	private static final long serialVersionUID = 1L;

	/** job id. */
	private String jobId;

	/** process date. */
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
		return "[jobId=" + jobId + ", processDate=" + processDate + ", pageable=" + super.toString() + "]";
	}
}
