package com.megthink.gateway.model;

import java.util.List;

public class RSP {

	private String messageSenderTelco;
	private String messageReceiverTelco;
	private String requestId;
	private String timestamp;
	private String referenceId;
	private String resultCode;
	private List<SubscriberResult> subscriberResult;
	private String orderedTransferTime;
	private String recommendTransferTime;
	private String recipientTelco;
	private String lsa;
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

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public List<SubscriberResult> getSubscriberResult() {
		return subscriberResult;
	}

	public void setSubscriberResult(List<SubscriberResult> subscriberResult) {
		this.subscriberResult = subscriberResult;
	}

	public String getOrderedTransferTime() {
		return orderedTransferTime;
	}

	public void setOrderedTransferTime(String orderedTransferTime) {
		this.orderedTransferTime = orderedTransferTime;
	}

	public String getRecommendTransferTime() {
		return recommendTransferTime;
	}

	public void setRecommendTransferTime(String recommendTransferTime) {
		this.recommendTransferTime = recommendTransferTime;
	}

	public String getRecipientTelco() {
		return recipientTelco;
	}

	public void setRecipientTelco(String recipientTelco) {
		this.recipientTelco = recipientTelco;
	}

	public String getLsa() {
		return lsa;
	}

	public void setLsa(String lsa) {
		this.lsa = lsa;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

}