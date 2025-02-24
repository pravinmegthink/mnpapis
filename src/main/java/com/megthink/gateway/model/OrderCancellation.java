package com.megthink.gateway.model;

import java.util.List;

public class OrderCancellation {

	private Integer source;
	private String requestId;
	private String timeStamp;
	private String service;
	private List<MSISDNUIDType> msisdnUID;
	private String comment;

	/* comming data from web services */
	private String messageSenderTelco;
	private String messageReceiverTelco;
	// private String requestId;
	private String timestamp;
	private String referenceId;
	private SubscriberSequence subscriberSequence;
	private Integer reasonCode;
	private String reasonText;

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
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

	public List<MSISDNUIDType> getMsisdnUID() {
		return msisdnUID;
	}

	public void setMsisdnUID(List<MSISDNUIDType> msisdnUID) {
		this.msisdnUID = msisdnUID;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getMessageSenderTelco() {
		return messageSenderTelco;
	}

	public void setMessageSenderTelco(String messageSenderTelco) {
		this.messageSenderTelco = messageSenderTelco;
	}

	public String getMessageReceiverTelco() {
		return messageReceiverTelco;
	}

	public void setMessageReceiverTelco(String messageReceiverTelco) {
		this.messageReceiverTelco = messageReceiverTelco;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public SubscriberSequence getSubscriberSequence() {
		return subscriberSequence;
	}

	public void setSubscriberSequence(SubscriberSequence subscriberSequence) {
		this.subscriberSequence = subscriberSequence;
	}

	public Integer getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(Integer reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getReasonText() {
		return reasonText;
	}

	public void setReasonText(String reasonText) {
		this.reasonText = reasonText;
	}

}
