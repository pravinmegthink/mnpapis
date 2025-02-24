package com.megthink.gateway.model;

import java.util.Date;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "sim_terminate_mt")
public class TerminateSimMT {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	@Column(name = "msisdn")
	private String subscriberNumber;
	@Column(name = "terminate_id")
	private Integer terminateId;
	@Column(name = "request_type")
	private String request_type;
	@Column(name = "status")
	private Integer status;
	@Column(name = "result_code")
	private Integer resultCode;
	@CreationTimestamp
	@Column(name = "created_date_time")
	private Date createdDateTime;
	@CreationTimestamp
	@Column(name = "updated_date_time")
	private Date updatedDateTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSubscriberNumber() {
		return subscriberNumber;
	}

	public void setSubscriberNumber(String subscriberNumber) {
		this.subscriberNumber = subscriberNumber;
	}

	public Integer getTerminateId() {
		return terminateId;
	}

	public void setTerminateId(Integer terminateId) {
		this.terminateId = terminateId;
	}

	public String getRequest_type() {
		return request_type;
	}

	public void setRequest_type(String request_type) {
		this.request_type = request_type;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getResultCode() {
		return resultCode;
	}

	public void setResultCode(Integer resultCode) {
		this.resultCode = resultCode;
	}

	public Date getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public Date getUpdatedDateTime() {
		return updatedDateTime;
	}

	public void setUpdatedDateTime(Date updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}

}
