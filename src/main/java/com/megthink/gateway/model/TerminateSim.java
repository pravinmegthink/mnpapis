package com.megthink.gateway.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "sim_terminate_tx")
public class TerminateSim {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "terminate_id")
	private Integer terminateId;
	@NotNull
	@Column(name = "source")
	private Integer source;
	@NotNull
	@Column(name = "request_id")
	private String requestId;
	@NotNull
	@Column(name = "time_stamp")
	private String timeStamp;
	@NotNull
	@Column(name = "service")
	private String service;
	@NotNull
	@Column(name = "rno")
	private String rno;
	@NotNull
	@Column(name = "dno")
	private String dno;
	@NotNull
	@Column(name = "area")
	private String area;
	@NotNull
	@Column(name = "rn")
	private String rn;
	@NotNull
	@Column(name = "status")
	private Integer status;
	@NotNull
	@Column(name = "user_id")
	private int userId;
	@Column(name = "mch")
	private int mch;
	@NotNull
	@CreationTimestamp
	@Column(name = "created_tate_time")
	private Date createdDateTime;
	@NotNull
	@CreationTimestamp
	@Column(name = "updated_date_time")
	private Date updatedDateTime;
	@Transient
	private List<MSISDNUIDType> msisdnUID;
	@Column(name = "comment")
	private String comment;
	@Column(name = "termination_time")
	private String terminationTime;
	@Column(name = "approval")
	private String approval;
	@Column(name = "original_carrier")
	private String originalCarrier;

	/* define for show data in UI page */
	@Transient
	private String msisdn;

	public Integer getTerminateId() {
		return terminateId;
	}

	public void setTerminateId(Integer terminateId) {
		this.terminateId = terminateId;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getRno() {
		return rno;
	}

	public void setRno(String rno) {
		this.rno = rno;
	}

	public String getDno() {
		return dno;
	}

	public void setDno(String dno) {
		this.dno = dno;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getRn() {
		return rn;
	}

	public void setRn(String rn) {
		this.rn = rn;
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

	public List<MSISDNUIDType> getMsisdnUID() {
		if (msisdnUID == null) {
			msisdnUID = new ArrayList<MSISDNUIDType>();
		}
		return msisdnUID;
	}

	public void setMsisdnUID(List<MSISDNUIDType> msisdnUID) {
		this.msisdnUID = msisdnUID;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getMch() {
		return mch;
	}

	public void setMch(int mch) {
		this.mch = mch;
	}

	public String getTerminationTime() {
		return terminationTime;
	}

	public void setTerminationTime(String terminationTime) {
		this.terminationTime = terminationTime;
	}

	public String getApproval() {
		return approval;
	}

	public void setApproval(String approval) {
		this.approval = approval;
	}

	public String getOriginalCarrier() {
		return originalCarrier;
	}

	public void setOriginalCarrier(String originalCarrier) {
		this.originalCarrier = originalCarrier;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

}
