package com.megthink.gateway.model;

import java.sql.Timestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "broadcast_stats")
public class BroadcastStats {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	@Column(name = "count")
	private int count;
	@Column(name = "mch")
	private String mch;
	@Column(name = "created_date")
	private Timestamp created_date;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getMch() {
		return mch;
	}

	public void setMch(String mch) {
		this.mch = mch;
	}

	public Timestamp getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Timestamp created_date) {
		this.created_date = created_date;
	}

}
