package com.megthink.gateway.model;

public class NPOSAACK {

	private String MessageSenderTelco;
	private String MessageReceiverTelco;
	private String RequestId;
	private String Timestamp;
	private String ReferenceId;
	private String ResultCode;

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

	public String getResultCode() {
		return ResultCode;
	}

	public void setResultCode(String resultCode) {
		ResultCode = resultCode;
	}

}
