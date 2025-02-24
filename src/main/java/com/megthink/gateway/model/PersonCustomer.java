package com.megthink.gateway.model;

import jakarta.persistence.*;

@Entity
@Table(name = "person_customer")
public class PersonCustomer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	@Column(name = "owner_name")
	private String ownerName;
	@Column(name = "owner_id")
	private String ownerId;
	@Column(name = "type_of_id")
	private int typeOfId;
	@Column(name = "signature_date")
	private String signatureDate;
	@Column(name = "port_id")
	private Integer portId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public int getTypeOfId() {
		return typeOfId;
	}

	public void setTypeOfId(int typeOfId) {
		this.typeOfId = typeOfId;
	}

	public String getSignatureDate() {
		return signatureDate;
	}

	public void setSignatureDate(String signatureDate) {
		this.signatureDate = signatureDate;
	}

	public Integer getPortId() {
		return portId;
	}

	public void setPortId(Integer portId) {
		this.portId = portId;
	}

}
