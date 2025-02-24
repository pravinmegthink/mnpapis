package com.megthink.gateway.model;

import java.util.List;

public class NpdCancelRequest {

	private List<NumberRange> NumberRange;
	private String Route;
	private String Comments;

	public List<NumberRange> getNumberRange() {
		return NumberRange;
	}

	public void setNumberRange(List<NumberRange> numberRange) {
		NumberRange = numberRange;
	}

	public String getRoute() {
		return Route;
	}

	public void setRoute(String route) {
		Route = route;
	}

	public String getComments() {
		return Comments;
	}

	public void setComments(String comments) {
		Comments = comments;
	}
}
