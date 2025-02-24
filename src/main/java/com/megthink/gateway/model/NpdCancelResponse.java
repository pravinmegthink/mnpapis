package com.megthink.gateway.model;

import java.util.List;

public class NpdCancelResponse {
	private List<NumberRange> NumberRange;
	private String ReasonCode;
	private String Comments;

	public List<NumberRange> getNumberRange() {
		return NumberRange;
	}

	public void setNumberRange(List<NumberRange> numberRange) {
		NumberRange = numberRange;
	}

	public String getReasonCode() {
		return ReasonCode;
	}

	public void setReasonCode(String reasonCode) {
		ReasonCode = reasonCode;
	}

	public String getComments() {
		return Comments;
	}

	public void setComments(String comments) {
		Comments = comments;
	}
}
