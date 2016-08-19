package com.akmans.trade.core.springdata.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mst_calendar")
public class MstCalendar extends AbstractEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long code;

	@Column(name = "calendar", length = 2)
	private String calendar;

	@Column(name = "holiday")
	private Integer holiday;

	@Column(name = "regist_at")
	private Date registAt;

	@Column(name = "description", length = 200)
	private String description;

	public void setCode(Long code) {
		this.code = code;
	}

	public Long getCode() {
		return this.code;
	}

	public void setCalendar(String calendar) {
		this.calendar = calendar;
	}

	public String getCalendar() {
		return this.calendar;
	}

	public void setHoliday(Integer holiday) {
		this.holiday = holiday;
	}

	public Integer getHoliday() {
		return this.holiday;
	}

	public void setRegistAt(Date registAt) {
		this.registAt = registAt;
	}

	public Date getRegistAt() {
		return this.registAt;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	@Override
	public String toString() {
		return this.getClass().getAnnotation(Table.class).name() + " [code=" + code + ", calendar=" + calendar
				+ ", holiday=" + holiday + ", registAt=" + registAt + ", description=" + description + super.toString()
				+ "]";
	}
}
