package com.megthink.gateway.model;

import java.util.Date;
import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "port_mt")
public class SubscriberArrType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	@Column(name = "msisdn")
	private String msisdn;
	@Column(name = "dummymsisdn")
	private String dummyMSISDN;
	@Column(name = "sim")
	private String sim;
	@Column(name = "imsi")
	private String imsi;
	@Column(name = "hlr")
	private String hlr;
	@Column(name = "pin_code")
	private String pinCode;
	@Column(name = "port_id")
	private Integer portId;
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
	@Column(name = "activation_date_time")
	private String activationDateTime;
	@Column(name = "disconnection_date_time")
	private String disconnectionDateTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getDummyMSISDN() {
		return dummyMSISDN;
	}

	public void setDummyMSISDN(String dummyMSISDN) {
		this.dummyMSISDN = dummyMSISDN;
	}

	public String getSim() {
		return sim;
	}

	public void setSim(String sim) {
		this.sim = sim;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getHlr() {
		return hlr;
	}

	public void setHlr(String hlr) {
		this.hlr = hlr;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public Integer getPortId() {
		return portId;
	}

	public void setPortId(Integer portId) {
		this.portId = portId;
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

	public String getActivationDateTime() {
		return activationDateTime;
	}

	public void setActivationDateTime(String activationDateTime) {
		this.activationDateTime = activationDateTime;
	}

	public String getDisconnectionDateTime() {
		return disconnectionDateTime;
	}

	public void setDisconnectionDateTime(String disconnectionDateTime) {
		this.disconnectionDateTime = disconnectionDateTime;
	}

}
