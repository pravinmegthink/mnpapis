package com.megthink.gateway.model;

import java.util.List;

public class NPOT {
	private String messageSenderTelco;
	private String messageReceiverTelco;
	private String requestId;
	private String timestamp;
	private String referenceId;
	private List<SubscriberSequence> subscriberSequence;
	private String lsa;
	private String orderedTransferTime;
	private String orderedApprovalTime;
	private String messageType;

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

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
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

	public List<SubscriberSequence> getSubscriberSequence() {
		return subscriberSequence;
	}

	public void setSubscriberSequence(List<SubscriberSequence> subscriberSequence) {
		this.subscriberSequence = subscriberSequence;
	}

	public String getLsa() {
		return lsa;
	}

	public void setLsa(String lsa) {
		this.lsa = lsa;
	}

	public String getOrderedTransferTime() {
		return orderedTransferTime;
	}

	public void setOrderedTransferTime(String orderedTransferTime) {
		this.orderedTransferTime = orderedTransferTime;
	}

	public String getOrderedApprovalTime() {
		return orderedApprovalTime;
	}

	public void setOrderedApprovalTime(String orderedApprovalTime) {
		this.orderedApprovalTime = orderedApprovalTime;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

}
