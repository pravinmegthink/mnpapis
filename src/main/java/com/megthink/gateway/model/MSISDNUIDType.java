package com.megthink.gateway.model;

public class MSISDNUIDType {

	private String msisdn;
	private String requestId;

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	@Override
	public String toString() {
		return "MSISDNUIDType [msisdn=" + msisdn + ", requestId=" + requestId + "]";
	}

}
