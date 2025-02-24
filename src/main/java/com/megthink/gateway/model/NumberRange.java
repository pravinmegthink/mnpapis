package com.megthink.gateway.model;

public class NumberRange {
	private String NumberFrom;
	private String NumberTo;
	private String	PortingCode;
	private String	ReasonCode;

	public String getNumberFrom() {
		return NumberFrom;
	}

	public void setNumberFrom(String numberFrom) {
		NumberFrom = numberFrom;
	}

	public String getNumberTo() {
		return NumberTo;
	}

	public void setNumberTo(String numberTo) {
		NumberTo = numberTo;
	}

	public String getPortingCode() {
		return PortingCode;
	}

	public void setPortingCode(String portingCode) {
		PortingCode = portingCode;
	}

	public String getReasonCode() {
		return ReasonCode;
	}

	public void setReasonCode(String reasonCode) {
		ReasonCode = reasonCode;
	}
}
