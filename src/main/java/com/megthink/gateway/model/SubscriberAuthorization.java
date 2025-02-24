package com.megthink.gateway.model;

import jakarta.persistence.*;

@Entity
@Table(name = "port_mt")
public class SubscriberAuthorization {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	@Column(name = "msisdn")
	private String subscriberNumber;
	@Column(name = "owner_id")
	private String ownerId;
	@Column(name = "type_of_id")
	private int typeOfId;
	@Column(name = "port_id")
	private Integer portId;
	@Column(name = "request_type")
	private String request_type;
	@Column(name = "status")
	private Integer status;
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

	public String getSubscriberNumber() {
		return subscriberNumber;
	}

	public void setSubscriberNumber(String subscriberNumber) {
		this.subscriberNumber = subscriberNumber;
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
