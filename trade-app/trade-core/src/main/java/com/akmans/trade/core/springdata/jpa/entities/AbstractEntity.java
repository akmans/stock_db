package com.akmans.trade.core.springdata.jpa.entities;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.ZonedDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class AbstractEntity {

	@Column(name = "created_date", nullable = false)
	@Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
	@CreatedDate
	private ZonedDateTime createdDate;

	@Column(name = "updated_date")
	@Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
	@LastModifiedDate
	private ZonedDateTime updatedDate;

	@Column(name = "created_by", nullable = false)
	@CreatedBy
	private String createdBy;

	@Column(name = "updated_by", nullable = false)
	@LastModifiedBy
	private String updatedBy;

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
}
