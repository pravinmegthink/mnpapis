package com.megthink.gateway.model;

import java.sql.Timestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_billing_resolution")
public class BillingResolution extends AbstractPersistableEntity<String> {

	@Id
	@Column(name = "msisdn")
	private String msisdn;
	@Column(name = "bill_no")
	private String bill_no;
	//tbl_master_np
	//donor
	//recipient
	//nrh
	//donorlsa
	@Column(name = "acc_no")
	private String acc_no;
	@Column(name = "bill_date")
	private String bill_date;
	@Column(name = "due_date")
	private String due_date;
	@Column(name = "amount")
	private String amount;
	@Column(name = "status")
	private int status;
	@Column(name = "transaction_id")
	private String transactionId;
	@Column(name = "comments")
	private String comments;
	@Column(name = "remark1")
	private String remark1;
	@Column(name = "remark2")
	private String remark2;
	@Column(name = "remark3")
	private String remark3;
	@Column(name = "request_type")
	private String request_type;
	@Column(name = "created_date")
	private Timestamp created_date;
	@Column(name = "updated_date")
	private Timestamp updated_date;
	@Column(name = "answer_date")
	private Timestamp answer_date;
	@Column(name = "canceled_date")
	private Timestamp canceled_date;
	@Column(name = "ack_date")
	private Timestamp ack_date;
	@Column(name = "re_answer_date")
	private Timestamp re_answer_date;
	@Column(name = "re_ack_date")
	private Timestamp re_ack_date;
	@Column(name = "reason")
	private String reason;
	@Column(name = "user_id")
	private int user_id;

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getBill_no() {
		return bill_no;
	}

	public void setBill_no(String bill_no) {
		this.bill_no = bill_no;
	}

	public String getAcc_no() {
		return acc_no;
	}

	public void setAcc_no(String acc_no) {
		this.acc_no = acc_no;
	}

	public String getBill_date() {
		return bill_date;
	}

	public void setBill_date(String bill_date) {
		this.bill_date = bill_date;
	}

	public String getDue_date() {
		return due_date;
	}

	public void setDue_date(String due_date) {
		this.due_date = due_date;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getRemark1() {
		return remark1;
	}

	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}

	public String getRemark2() {
		return remark2;
	}

	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}

	public String getRemark3() {
		return remark3;
	}

	public void setRemark3(String remark3) {
		this.remark3 = remark3;
	}

	public String getRequest_type() {
		return request_type;
	}

	public void setRequest_type(String request_type) {
		this.request_type = request_type;
	}

	public Timestamp getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Timestamp created_date) {
		this.created_date = created_date;
	}

	public Timestamp getUpdated_date() {
		return updated_date;
	}

	public void setUpdated_date(Timestamp updated_date) {
		this.updated_date = updated_date;
	}

	public Timestamp getAnswer_date() {
		return answer_date;
	}

	public void setAnswer_date(Timestamp answer_date) {
		this.answer_date = answer_date;
	}

	public Timestamp getCanceled_date() {
		return canceled_date;
	}

	public void setCanceled_date(Timestamp canceled_date) {
		this.canceled_date = canceled_date;
	}

	public Timestamp getAck_date() {
		return ack_date;
	}

	public void setAck_date(Timestamp ack_date) {
		this.ack_date = ack_date;
	}

	public Timestamp getRe_answer_date() {
		return re_answer_date;
	}

	public void setRe_answer_date(Timestamp re_answer_date) {
		this.re_answer_date = re_answer_date;
	}

	public Timestamp getRe_ack_date() {
		return re_ack_date;
	}

	public void setRe_ack_date(Timestamp re_ack_date) {
		this.re_ack_date = re_ack_date;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getId() {
		return null;
	}
}
