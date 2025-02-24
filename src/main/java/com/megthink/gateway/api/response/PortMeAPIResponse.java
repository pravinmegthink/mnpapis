package com.megthink.gateway.api.response;

import com.megthink.gateway.model.ConnectionAnswerCon;
import com.megthink.gateway.model.ConnectionAnswerType;
import com.megthink.gateway.model.ConnectionOrderType;

public class PortMeAPIResponse {

	private int id;
	private int responseCode;
	private String responseMessage;
	private ConnectionOrderType connectionOrderType;
	private ConnectionAnswerType connectionAnswerType;
	private ConnectionAnswerCon connectionAnswerCon;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public ConnectionOrderType getConnectionOrderType() {
		if (connectionOrderType == null) {
			connectionOrderType = new ConnectionOrderType();
		}
		return connectionOrderType;
	}

	public void setConnectionOrderType(ConnectionOrderType connectionOrderType) {
		this.connectionOrderType = connectionOrderType;
	}

	public ConnectionAnswerType getConnectionAnswerType() {
		if (connectionAnswerType == null) {
			connectionAnswerType = new ConnectionAnswerType();
		}
		return connectionAnswerType;
	}

	public void setConnectionAnswerType(ConnectionAnswerType connectionAnswerType) {
		this.connectionAnswerType = connectionAnswerType;
	}

	public ConnectionAnswerCon getConnectionAnswerCon() {
		if (connectionAnswerCon == null) {
			connectionAnswerCon = new ConnectionAnswerCon();
		}
		return connectionAnswerCon;
	}

	public void setConnectionAnswerCon(ConnectionAnswerCon connectionAnswerCon) {
		this.connectionAnswerCon = connectionAnswerCon;
	}

	@Override
	public String toString() {
		return "PortMeAPIResponse [id=" + id + ", responseCode=" + responseCode + ", responseMessage=" + responseMessage
				+ ", connectionOrderType=" + connectionOrderType + ", connectionAnswerType=" + connectionAnswerType
				+ ", connectionAnswerCon=" + connectionAnswerCon + "]";
	}

}
