package com.megthink.gateway.model;

public class SubscriberResultType {

	private String subscriberNumber;
	private Integer resultCode;
	private Integer resultCode2;
	private Integer resultCode3;
	private String resultText;

	public String getSubscriberNumber() {
		return subscriberNumber;
	}

	public void setSubscriberNumber(String subscriberNumber) {
		this.subscriberNumber = subscriberNumber;
	}

	public Integer getResultCode() {
		return resultCode;
	}

	public void setResultCode(Integer resultCode) {
		this.resultCode = resultCode;
	}

	public Integer getResultCode2() {
		return resultCode2;
	}

	public void setResultCode2(Integer resultCode2) {
		this.resultCode2 = resultCode2;
	}

	public Integer getResultCode3() {
		return resultCode3;
	}

	public void setResultCode3(Integer resultCode3) {
		this.resultCode3 = resultCode3;
	}

	public String getResultText() {
		return resultText;
	}

	public void setResultText(String resultText) {
		this.resultText = resultText;
	}

}
