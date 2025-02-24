package com.megthink.gateway.model;

import jakarta.persistence.*;

@Entity
@Table(name = "localization")
public class LocalizationSupport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "constKey")
	private String constKey;

	@Column(name = "description")
	private String description;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getConstKey() {
		return constKey;
	}

	public void setConstKey(String constKey) {
		this.constKey = constKey;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}