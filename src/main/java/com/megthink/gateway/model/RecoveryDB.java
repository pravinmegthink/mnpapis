package com.megthink.gateway.model;

import java.sql.Timestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "recovery_info")
public class RecoveryDB extends AbstractPersistableEntity<String> {

	@Id
	@NotNull(message = "Request Id cann't be empty")
	@Column(name = "request_id", length = 50)
	private String request_id;
	@Column(name = "msisdn")
	private String msisdn;
	@NotNull(message = "Zone type cann't be empty")
	@Column(name = "zone", length = 10)
	private String zone;
	@NotNull(message = "Request type cann't be empty")
	@Column(name = "request_type", length = 15)
	private String request_type;
	@Column(name = "lsa")
	private String lsa;
	@Column(name = "result_code")
	private int result_code;
	@Column(name = "path")
	private String path;
	@Column(name = "file_name")
	private String file_name;
	@Column(name = "status")
	private int status;
	@Column(name = "start_date")
	private Timestamp start_date;
	@Column(name = "end_date")
	private Timestamp end_date;
	@Column(name = "submit_date")
	private Timestamp submit_date;
	@Column(name = "update_date")
	private Timestamp update_date;
	@Column(name = "userId")
	private int userId;

	@Transient
	String o_id;
	@Transient
	int o_result;

	public String getRequest_id() {
		return request_id;
	}

	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getRequest_type() {
		return request_type;
	}

	public void setRequest_type(String request_type) {
		this.request_type = request_type;
	}

	public String getLsa() {
		return lsa;
	}

	public void setLsa(String lsa) {
		this.lsa = lsa;
	}

	public int getResult_code() {
		return result_code;
	}

	public void setResult_code(int result_code) {
		this.result_code = result_code;
	}

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Timestamp getStart_date() {
		return start_date;
	}

	public void setStart_date(Timestamp start_date) {
		this.start_date = start_date;
	}

	public Timestamp getEnd_date() {
		return end_date;
	}

	public void setEnd_date(Timestamp end_date) {
		this.end_date = end_date;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Timestamp getSubmit_date() {
		return submit_date;
	}

	public void setSubmit_date(Timestamp submit_date) {
		this.submit_date = submit_date;
	}

	public Timestamp getUpdate_date() {
		return update_date;
	}

	public void setUpdate_date(Timestamp update_date) {
		this.update_date = update_date;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getO_id() {
		return o_id;
	}

	public void setO_id(String o_id) {
		this.o_id = o_id;
	}

	public int getO_result() {
		return o_result;
	}

	public void setO_result(int o_result) {
		this.o_result = o_result;
	}

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}
}
