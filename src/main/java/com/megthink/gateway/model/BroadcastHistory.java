package com.megthink.gateway.model;

import java.sql.Timestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_broadcast")
public class BroadcastHistory extends AbstractPersistableEntity<String> {

	@Id
	@Column(name = "msisdn")
	private String msisdn;
	@Column(name = "action")
	private String action;
	@Column(name = "active")
	private String active;
	@Column(name = "area")
	private String area;
	@Column(name = "carrier_history")
	private String carrier_history;
	@Column(name = "first_trans_date")
	private Timestamp first_trans_date;
	@Column(name = "history_area")
	private String history_area;
	@Column(name = "hlr")
	private String hlr;
	// new column
	private String op_route;
	@Column(name = "orginal_carrier")
	private String orginal_carrier;
	@Column(name = "original_area")
	private String original_area;
	@Column(name = "present_carrier")
	private String present_carrier;
	@Column(name = "processor")
	private String processor;
	@Column(name = "processor_name")
	private String processor_name;
	@Column(name = "re_trans_date")
	private Timestamp re_trans_date;
	// new column
	private String identifier;
	@Column(name = "rn")
	private String rn;
	@Column(name = "service")
	private String service;
	@Column(name = "transaction_id")
	private String transaction_id;
	@Column(name = "type")
	private String type;
	@Column(name = "request_id")
	private String requestId;
	@Column(name = "disconnection_date")
	private Timestamp disconnection_date;
	@Column(name = "transaction_date")
	private Timestamp transaction_date;
	@Column(name = "internal_rn1")
	private String internal_rn1;
	@Column(name = "internal_rn2")
	private String internal_rn2;
	@Column(name = "remark")
	private String remark;
	@Column(name = "remark1")
	private String remark1;
	@Column(name = "mch")
	private Integer mch;

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCarrier_history() {
		return carrier_history;
	}

	public void setCarrier_history(String carrier_history) {
		this.carrier_history = carrier_history;
	}

	public Timestamp getFirst_trans_date() {
		return first_trans_date;
	}

	public void setFirst_trans_date(Timestamp first_trans_date) {
		this.first_trans_date = first_trans_date;
	}

	public String getHistory_area() {
		return history_area;
	}

	public void setHistory_area(String history_area) {
		this.history_area = history_area;
	}

	public String getHlr() {
		return hlr;
	}

	public void setHlr(String hlr) {
		this.hlr = hlr;
	}

	public String getOp_route() {
		return op_route;
	}

	public void setOp_route(String op_route) {
		this.op_route = op_route;
	}

	public String getOrginal_carrier() {
		return orginal_carrier;
	}

	public void setOrginal_carrier(String orginal_carrier) {
		this.orginal_carrier = orginal_carrier;
	}

	public String getOriginal_area() {
		return original_area;
	}

	public void setOriginal_area(String original_area) {
		this.original_area = original_area;
	}

	public String getPresent_carrier() {
		return present_carrier;
	}

	public void setPresent_carrier(String present_carrier) {
		this.present_carrier = present_carrier;
	}

	public String getProcessor() {
		return processor;
	}

	public void setProcessor(String processor) {
		this.processor = processor;
	}

	public String getProcessor_name() {
		return processor_name;
	}

	public void setProcessor_name(String processor_name) {
		this.processor_name = processor_name;
	}

	public Timestamp getRe_trans_date() {
		return re_trans_date;
	}

	public void setRe_trans_date(Timestamp re_trans_date) {
		this.re_trans_date = re_trans_date;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getRn() {
		return rn;
	}

	public void setRn(String rn) {
		this.rn = rn;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Timestamp getDisconnection_date() {
		return disconnection_date;
	}

	public void setDisconnection_date(Timestamp disconnection_date) {
		this.disconnection_date = disconnection_date;
	}

	public Timestamp getTransaction_date() {
		return transaction_date;
	}

	public void setTransaction_date(Timestamp transaction_date) {
		this.transaction_date = transaction_date;
	}

	public String getInternal_rn1() {
		return internal_rn1;
	}

	public void setInternal_rn1(String internal_rn1) {
		this.internal_rn1 = internal_rn1;
	}

	public String getInternal_rn2() {
		return internal_rn2;
	}

	public void setInternal_rn2(String internal_rn2) {
		this.internal_rn2 = internal_rn2;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRemark1() {
		return remark1;
	}

	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}

	public Integer getMch() {
		return mch;
	}

	public void setMch(Integer mch) {
		this.mch = mch;
	}

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}
}
