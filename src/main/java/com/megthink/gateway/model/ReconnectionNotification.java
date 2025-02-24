package com.megthink.gateway.model;

import java.util.List;

public class ReconnectionNotification {
	private List<NumberRange> NumberRange;
	private String Comments;

	public List<NumberRange> getNumberRange() {
		return NumberRange;
	}

	public void setNumberRange(List<NumberRange> numberRange) {
		NumberRange = numberRange;
	}

	public String getComments() {
		return Comments;
	}

	public void setComments(String comments) {
		Comments = comments;
	}
}
