package com.megthink.gateway.model;

import java.util.ArrayList;
import java.util.List;

public class PortActivatedBroadcast {

	private List<NumberRangeOAFlagged> numberRangeOAFlagged;
	private String donor;
	private String recipient;
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

	public String getDonor() {
		return donor;
	}

	public void setDonor(String donor) {
		this.donor = donor;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
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
