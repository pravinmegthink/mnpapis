package com.megthink.gateway.model;

import java.sql.Timestamp;

public class BCInfoType {

	private String subscriberNumber;
	private String bCAction;
	private Timestamp dCTime;
	private String serviceType;
	private String routeNumber;
	private String rno;
	private String dno;
	private String nrh;
	private String resultText;

	public String getSubscriberNumber() {
		return subscriberNumber;
	}

	public void setSubscriberNumber(String subscriberNumber) {
		this.subscriberNumber = subscriberNumber;
	}

	public String getbCAction() {
		return bCAction;
	}

	public void setbCAction(String bCAction) {
		this.bCAction = bCAction;
	}

	public Timestamp getdCTime() {
		return dCTime;
	}

	public void setdCTime(Timestamp dCTime) {
		this.dCTime = dCTime;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getRouteNumber() {
		return routeNumber;
	}

	public void setRouteNumber(String routeNumber) {
		this.routeNumber = routeNumber;
	}

	public String getRno() {
		return rno;
	}

	public void setRno(String rno) {
		this.rno = rno;
	}

	public String getDno() {
		return dno;
	}

	public void setDno(String dno) {
		this.dno = dno;
	}

	public String getNrh() {
		return nrh;
	}

	public void setNrh(String nrh) {
		this.nrh = nrh;
	}

	public String getResultText() {
		return resultText;
	}

	public void setResultText(String resultText) {
		this.resultText = resultText;
	}

}
