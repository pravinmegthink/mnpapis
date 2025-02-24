package com.megthink.gateway.model;

import java.util.List;

public class NPO {

	private String messageSenderTelco;
	private String messageReceiverTelco;
	private String requestId;
	private String timestamp;
	private String referenceId;
	private String recipientTelco;
	private String donorTelco;
	private String oldReferenceId;
	private String byLOA;
	private String lOAImage;
	private String undertakingAck;
	private List<SubscriberSequence> subscriberSequence;
	private SubscriberAuthSequence subscriberAuthSequence;
	private String orderedTransferTime;
	private String orderedApprovalTime;
	private String lsa;
	private String routeNumber;
	private String serviceType;
	private PersonCustomer personCustomer;
	private CorporateCustomer corporateCustomer;
	private Author author;
	private String messageType;

	public String getMessageSenderTelco() {
		return messageSenderTelco;
	}

	public void setMessageSenderTelco(String messageSenderTelco) {
		this.messageSenderTelco = messageSenderTelco;
	}

	public String getMessageReceiverTelco() {
		return messageReceiverTelco;
	}

	public void setMessageReceiverTelco(String messageReceiverTelco) {
		this.messageReceiverTelco = messageReceiverTelco;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getRecipientTelco() {
		return recipientTelco;
	}

	public void setRecipientTelco(String recipientTelco) {
		this.recipientTelco = recipientTelco;
	}

	public String getDonorTelco() {
		return donorTelco;
	}

	public void setDonorTelco(String donorTelco) {
		this.donorTelco = donorTelco;
	}

	public String getOldReferenceId() {
		return oldReferenceId;
	}

	public void setOldReferenceId(String oldReferenceId) {
		this.oldReferenceId = oldReferenceId;
	}

	public String getByLOA() {
		return byLOA;
	}

	public void setByLOA(String byLOA) {
		this.byLOA = byLOA;
	}

	public String getlOAImage() {
		return lOAImage;
	}

	public void setlOAImage(String lOAImage) {
		this.lOAImage = lOAImage;
	}

	public String getUndertakingAck() {
		return undertakingAck;
	}

	public void setUndertakingAck(String undertakingAck) {
		this.undertakingAck = undertakingAck;
	}

	public List<SubscriberSequence> getSubscriberSequence() {
		return subscriberSequence;
	}

	public void setSubscriberSequence(List<SubscriberSequence> subscriberSequence) {
		this.subscriberSequence = subscriberSequence;
	}

	public SubscriberAuthSequence getSubscriberAuthSequence() {
		return subscriberAuthSequence;
	}

	public void setSubscriberAuthSequence(SubscriberAuthSequence subscriberAuthSequence) {
		this.subscriberAuthSequence = subscriberAuthSequence;
	}

	public String getOrderedTransferTime() {
		return orderedTransferTime;
	}

	public void setOrderedTransferTime(String orderedTransferTime) {
		this.orderedTransferTime = orderedTransferTime;
	}

	public String getOrderedApprovalTime() {
		return orderedApprovalTime;
	}

	public void setOrderedApprovalTime(String orderedApprovalTime) {
		this.orderedApprovalTime = orderedApprovalTime;
	}

	public String getLsa() {
		return lsa;
	}

	public void setLsa(String lsa) {
		this.lsa = lsa;
	}

	public String getRouteNumber() {
		return routeNumber;
	}

	public void setRouteNumber(String routeNumber) {
		this.routeNumber = routeNumber;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public PersonCustomer getPersonCustomer() {
		return personCustomer;
	}

	public void setPersonCustomer(PersonCustomer personCustomer) {
		this.personCustomer = personCustomer;
	}

	public CorporateCustomer getCorporateCustomer() {
		return corporateCustomer;
	}

	public void setCorporateCustomer(CorporateCustomer corporateCustomer) {
		this.corporateCustomer = corporateCustomer;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

}
