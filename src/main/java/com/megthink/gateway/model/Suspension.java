package com.megthink.gateway.model;

public class Suspension {

	private String messageSenderTelco;
	private String messageReceiverTelco;
	private String requestId;
	private String timestamp;
	private String referenceId;
	private String subscriberNumber;
	private String billDate;
	private String dueDate;
	private String amount;
	private String remark;

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

	public String getSubscriberNumber() {
		return subscriberNumber;
	}

	public void setSubscriberNumber(String subscriberNumber) {
		this.subscriberNumber = subscriberNumber;
	}

	public String getBillDate() {
		return billDate;
	}

	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
