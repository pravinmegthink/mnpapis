package com.megthink.gateway.model;

import java.sql.Timestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "msisdn_range")
public class NumberPlan {

	@Id
	@Column(name = "slno")
	private String slno;
	@Column(name = "area")
	private String area;
	@Column(name = "op_id")
	private String op_id;
	@Column(name = "start_range")
	private String start_range;
	@Column(name = "end_range")
	private String end_range;
	@Column(name = "technology")
	private String technology;
	@Column(name = "type")
	private String type;
	@Column(name = "update_date")
	private Timestamp update_date;
	@Column(name = "routing_info")
	private String routing_info;
	@Column(name = "remark")
	private String remark;
	@Column(name = "remark2")
	private String remark2;
	@Column(name = "changed_by")
	private String changed_by;
	@Transient
	private String op_name;

	public String getSlno() {
		return slno;
	}

	public void setSlno(String slno) {
		this.slno = slno;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getOp_id() {
		return op_id;
	}

	public void setOp_id(String op_id) {
		this.op_id = op_id;
	}

	public String getStart_range() {
		return start_range;
	}

	public void setStart_range(String start_range) {
		this.start_range = start_range;
	}

	public String getEnd_range() {
		return end_range;
	}

	public void setEnd_range(String end_range) {
		this.end_range = end_range;
	}

	public String getTechnology() {
		return technology;
	}

	public void setTechnology(String technology) {
		this.technology = technology;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Timestamp getUpdate_date() {
		return update_date;
	}

	public void setUpdate_date(Timestamp update_date) {
		this.update_date = update_date;
	}

	public String getRouting_info() {
		return routing_info;
	}

	public void setRouting_info(String routing_info) {
		this.routing_info = routing_info;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRemark2() {
		return remark2;
	}

	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}

	public String getChanged_by() {
		return changed_by;
	}

	public void setChanged_by(String changed_by) {
		this.changed_by = changed_by;
	}

	public String getOp_name() {
		return op_name;
	}

	public void setOp_name(String op_name) {
		this.op_name = op_name;
	}

	@Override
	public String toString() {
		return "NumberPlan [slno=" + slno + ", area=" + area + ", op_id=" + op_id + ", start_range=" + start_range
				+ ", end_range=" + end_range + ", technology=" + technology + ", type=" + type + ", update_date="
				+ update_date + ", routing_info=" + routing_info + ", remark=" + remark + ", remark2=" + remark2
				+ ", changed_by=" + changed_by + ", op_name=" + op_name + "]";
	}

}
