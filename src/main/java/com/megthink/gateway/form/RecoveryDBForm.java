package com.megthink.gateway.form;

public class RecoveryDBForm {

	private String requestId;
	private String zoneType;
	private String requestType;
	private Boolean isLSA;
	private Boolean isMSISDN;
	private Boolean isTimestamp;
	private String lsa;
	private String msisdn;
	private String dateRange;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getZoneType() {
		return zoneType;
	}

	public void setZoneType(String zoneType) {
		this.zoneType = zoneType;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public Boolean getIsLSA() {
		return isLSA;
	}

	public void setIsLSA(Boolean isLSA) {
		this.isLSA = isLSA;
	}

	public Boolean getIsMSISDN() {
		return isMSISDN;
	}

	public void setIsMSISDN(Boolean isMSISDN) {
		this.isMSISDN = isMSISDN;
	}

	public Boolean getIsTimestamp() {
		return isTimestamp;
	}

	public void setIsTimestamp(Boolean isTimestamp) {
		this.isTimestamp = isTimestamp;
	}

	public String getLsa() {
		return lsa;
	}

	public void setLsa(String lsa) {
		this.lsa = lsa;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getDateRange() {
		return dateRange;
	}

	public void setDateRange(String dateRange) {
		this.dateRange = dateRange;
	}

}
