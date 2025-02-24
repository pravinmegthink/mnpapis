package com.megthink.gateway.model;

public class NPCData {

	private MessageHeader MessageHeader;
	private NPCMessage NPCMessage;

	public MessageHeader getMessageHeader() {
		if(MessageHeader==null) {
			MessageHeader = new MessageHeader();
		}
		return MessageHeader;
	}

	public void setMessageHeader(MessageHeader messageHeader) {
		MessageHeader = messageHeader;
	}

	public NPCMessage getNPCMessage() {
		if(NPCMessage==null) {
			NPCMessage = new NPCMessage();
		}
		return NPCMessage;
	}

	public void setNPCMessage(NPCMessage nPCMessage) {
		NPCMessage = nPCMessage;
	}

}
