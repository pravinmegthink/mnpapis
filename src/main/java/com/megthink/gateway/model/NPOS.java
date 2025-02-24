package com.megthink.gateway.model;

public class NPOS {

	private String MessageSenderTelco;
	private String MessageReceiverTelco;
	private String RequestId;
	private String Timestamp;
	private String ReferenceId;
	private String SubscriberNumber;
	private String BillDate;
	private String DueDate;
	private String Amount;
	private String Remark;
	private String messageType;

	public String getMessageSenderTelco() {
		return MessageSenderTelco;
	}

	public void setMessageSenderTelco(String messageSenderTelco) {
		MessageSenderTelco = messageSenderTelco;
	}

	public String getMessageReceiverTelco() {
		return MessageReceiverTelco;
	}

	public void setMessageReceiverTelco(String messageReceiverTelco) {
		MessageReceiverTelco = messageReceiverTelco;
	}

	public String getRequestId() {
		return RequestId;
	}

	public void setRequestId(String requestId) {
		RequestId = requestId;
	}

	public String getTimestamp() {
		return Timestamp;
	}

	public void setTimestamp(String timestamp) {
		Timestamp = timestamp;
	}

	public String getReferenceId() {
		return ReferenceId;
	}

	public void setReferenceId(String referenceId) {
		ReferenceId = referenceId;
	}

	public String getSubscriberNumber() {
		return SubscriberNumber;
	}

	public void setSubscriberNumber(String subscriberNumber) {
		SubscriberNumber = subscriberNumber;
	}

	public String getBillDate() {
		return BillDate;
	}

	public void setBillDate(String billDate) {
		BillDate = billDate;
	}

	public String getDueDate() {
		return DueDate;
	}

	public void setDueDate(String dueDate) {
		DueDate = dueDate;
	}

	public String getAmount() {
		return Amount;
	}

	public void setAmount(String amount) {
		Amount = amount;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

}
