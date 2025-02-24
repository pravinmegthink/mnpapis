package com.megthink.gateway.model;

public class MessageHeader {

	private String lSAID;
	private String portType;
	private String transactionID;
	private int messageID;
	private String msgCreateTimeStamp;
	private String sender;

	public String getlSAID() {
		return lSAID;
	}

	public void setlSAID(String lSAID) {
		this.lSAID = lSAID;
	}

	public String getPortType() {
		return portType;
	}

	public void setPortType(String portType) {
		this.portType = portType;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public int getMessageID() {
		return messageID;
	}

	public void setMessageID(int messageID) {
		this.messageID = messageID;
	}

	public String getMsgCreateTimeStamp() {
		return msgCreateTimeStamp;
	}

	public void setMsgCreateTimeStamp(String msgCreateTimeStamp) {
		this.msgCreateTimeStamp = msgCreateTimeStamp;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

}
