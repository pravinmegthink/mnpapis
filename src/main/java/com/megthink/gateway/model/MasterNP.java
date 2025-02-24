package com.megthink.gateway.model;

import java.sql.Timestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_master_np")
public class MasterNP extends AbstractPersistableEntity<String> {

	@Id
	@Column(name = "msisdn")
	private String msisdn;
	// @Column(name = "request_id")
	// private String requestId;
	@Column(name = "type")
	private String type;
	@Column(name = "hlr")
	private String hlr;
	@Column(name = "sub_route")
	private String sub_route;
	@Column(name = "sub_route1")
	private String sub_route1;
	@Column(name = "remark1")
	private String remark1;
	@Column(name = "area")
	private String area;
	@Column(name = "service")
	private String service;
	@Column(name = "rn")
	private String rn;
	@Column(name = "present_carrier")
	private String present_carrier;
	@Column(name = "carrier_history")
	private String carrier_history;
	@Column(name = "orginal_carrier")
	private String orginal_carrier;
	@Column(name = "active")
	private String active;
	@Column(name = "history_area")
	private String history_area;
	@Column(name = "transaction_date")
	private Timestamp transaction_date;
	@Column(name = "disconnection_date")
	private Timestamp disconnection_date;
	@Column(name = "processor")
	private String processor;
	@Column(name = "first_trans_date")
	private Timestamp first_trans_date;
	@Column(name = "re_trans_date")
	private Timestamp re_trans_date;
	@Column(name = "processor_name")
	private String processor_name;
	@Column(name = "remark")
	private String remark;
	@Column(name = "original_area")
	private String original_area;
	@Column(name = "action")
	private String action;
	@Column(name = "nqms_flag")
	private int nqms_flag;
	

	// public String getRequestId() {
	// return requestId;
	// }
	//
	// public void setRequestId(String requestId) {
	// this.requestId = requestId;
	// }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHlr() {
		return hlr;
	}

	public void setHlr(String hlr) {
		this.hlr = hlr;
	}

	public String getSub_route() {
		return sub_route;
	}

	public void setSub_route(String sub_route) {
		this.sub_route = sub_route;
	}

	public String getSub_route1() {
		return sub_route1;
	}

	public void setSub_route1(String sub_route1) {
		this.sub_route1 = sub_route1;
	}

	public String getRemark1() {
		return remark1;
	}

	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getRn() {
		return rn;
	}

	public void setRn(String rn) {
		this.rn = rn;
	}

	public String getPresent_carrier() {
		return present_carrier;
	}

	public void setPresent_carrier(String present_carrier) {
		this.present_carrier = present_carrier;
	}

	public String getCarrier_history() {
		return carrier_history;
	}

	public void setCarrier_history(String carrier_history) {
		this.carrier_history = carrier_history;
	}

	public String getOrginal_carrier() {
		return orginal_carrier;
	}

	public void setOrginal_carrier(String orginal_carrier) {
		this.orginal_carrier = orginal_carrier;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getHistory_area() {
		return history_area;
	}

	public void setHistory_area(String history_area) {
		this.history_area = history_area;
	}

	public String getProcessor() {
		return processor;
	}

	public void setProcessor(String processor) {
		this.processor = processor;
	}

	public Timestamp getTransaction_date() {
		return transaction_date;
	}

	public void setTransaction_date(Timestamp transaction_date) {
		this.transaction_date = transaction_date;
	}

	public Timestamp getDisconnection_date() {
		return disconnection_date;
	}

	public void setDisconnection_date(Timestamp disconnection_date) {
		this.disconnection_date = disconnection_date;
	}

	public Timestamp getFirst_trans_date() {
		return first_trans_date;
	}

	public void setFirst_trans_date(Timestamp first_trans_date) {
		this.first_trans_date = first_trans_date;
	}

	public Timestamp getRe_trans_date() {
		return re_trans_date;
	}

	public void setRe_trans_date(Timestamp re_trans_date) {
		this.re_trans_date = re_trans_date;
	}

	public String getProcessor_name() {
		return processor_name;
	}

	public void setProcessor_name(String processor_name) {
		this.processor_name = processor_name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOriginal_area() {
		return original_area;
	}

	public void setOriginal_area(String original_area) {
		this.original_area = original_area;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	public int getNqms_flag() {
		return nqms_flag;
	}

	public void setNqms_flag(int nqms_flag) {
		this.nqms_flag = nqms_flag;
	}

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}
}
