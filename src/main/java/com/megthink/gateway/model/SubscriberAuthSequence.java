package com.megthink.gateway.model;

import java.util.ArrayList;
import java.util.List;

public class SubscriberAuthSequence {

	private List<SubscriberAuthorization> subscriberAuthorization;

	public List<SubscriberAuthorization> getSubscriberAuthorization() {
		if(subscriberAuthorization==null) {
			subscriberAuthorization = new ArrayList<SubscriberAuthorization>();
		}
		return subscriberAuthorization;
	}

	public void setSubscriberAuthorization(List<SubscriberAuthorization> subscriberAuthorization) {
		this.subscriberAuthorization = subscriberAuthorization;
	}

}
