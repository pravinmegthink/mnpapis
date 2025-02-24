package com.megthink.gateway.model;

public class InitPortMeResponseType {

	private String source;
	private String timeStamp;
	private String service;
	private String billingUID1;
	private String requestId;
	private String dno;
	private String approval;
	private SubscriberArrType subscriberArrType;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getBillingUID1() {
		return billingUID1;
	}

	public void setBillingUID1(String billingUID1) {
		this.billingUID1 = billingUID1;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getDno() {
		return dno;
	}

	public void setDno(String dno) {
		this.dno = dno;
	}

	public String getApproval() {
		return approval;
	}

	public void setApproval(String approval) {
		this.approval = approval;
	}

	public SubscriberArrType getSubscriberArrType() {
		if(subscriberArrType==null) {
			subscriberArrType = new SubscriberArrType();
		}
		return subscriberArrType;
	}

	public void setSubscriberArrType(SubscriberArrType subscriberArrType) {
		this.subscriberArrType = subscriberArrType;
	}

	@Override
	public String toString() {
		return "InitPortMeResponseType [source=" + source + ", timeStamp=" + timeStamp + ", service=" + service
				+ ", billingUID1=" + billingUID1 + ", requestId=" + requestId + ", dno=" + dno + ", approval="
				+ approval + ", subscriberArrType=" + subscriberArrType + "]";
	}

}
