package com.megthink.gateway.model;

import java.util.ArrayList;
import java.util.List;

public class NumReturnBroadcast {

	private List<NumberRangeOAFlagged> numberRangeOAFlagged;
	private String originalAssignee;
	private String lastRecipient;
	private String route;
	private String comments;

	public List<NumberRangeOAFlagged> getNumberRangeOAFlagged() {
		if (numberRangeOAFlagged == null) {
			numberRangeOAFlagged = new ArrayList<NumberRangeOAFlagged>();
		}
		return numberRangeOAFlagged;
	}

	public void setNumberRangeOAFlagged(List<NumberRangeOAFlagged> numberRangeOAFlagged) {
		this.numberRangeOAFlagged = numberRangeOAFlagged;
	}

	public String getOriginalAssignee() {
		return originalAssignee;
	}

	public void setOriginalAssignee(String originalAssignee) {
		this.originalAssignee = originalAssignee;
	}

	public String getLastRecipient() {
		return lastRecipient;
	}

	public void setLastRecipient(String lastRecipient) {
		this.lastRecipient = lastRecipient;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}
