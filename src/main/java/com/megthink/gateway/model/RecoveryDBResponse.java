package com.megthink.gateway.model;

public class RecoveryDBResponse {

	private String messageSenderTelco;
	private String messageReceiverTelco;
	private String requestId;
	private String timestamp;
	private int resultCode;
	private String downloadTime;
	private String dataFileName;
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

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getDownloadTime() {
		return downloadTime;
	}

	public void setDownloadTime(String downloadTime) {
		this.downloadTime = downloadTime;
	}

	public String getDataFileName() {
		return dataFileName;
	}

	public void setDataFileName(String dataFileName) {
		this.dataFileName = dataFileName;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

}
