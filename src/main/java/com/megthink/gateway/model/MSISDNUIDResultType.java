package com.megthink.gateway.model;

public class MSISDNUIDResultType {

	private String msisdn;
	private String requestId;
	private int resultCode;

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

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	@Override
	public String toString() {
		return "MSISDNUIDResultType [msisdn=" + msisdn + ", requestId=" + requestId + ", resultCode=" + resultCode
				+ "]";
	}

}
