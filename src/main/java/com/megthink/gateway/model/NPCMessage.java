package com.megthink.gateway.model;

public class NPCMessage {

	private PortActivatedBroadcast portActivatedBroadcast;
	private NumReturnBroadcast numReturnBroadcast;
	private SynchronisationResponse synchronisationResponse;
	private SynchronisationRequest SynchronisationRequest;
	private SubscriberInfoQuery SubscriberInfoQuery;
	private PortRequest PortRequest;
	private PortRespWithFlag PortRespWithFlag;
	private PortExecute PortExecute;
	private PortDeactWithRte PortDeactWithRte;
	private PortCancelNotification PortCancelNotification;
	private NonpaymentDisconnReq NonpaymentDisconnReq;
	private NonpaymentDisconnResp NonpaymentDisconnResp;
	private NpdCancelRequest NpdCancelRequest;
	private NpdCancelResponse NpdCancelResponse;
	private NpdAckResponse NpdAckResponse;
	private ReconnectionNotification ReconnectionNotification;
	private ReconnectionAck ReconnectionAck;
	private NumReturnRequest NumReturnRequest;
	private NumReturnRequestWithRte NumReturnRequestWithRte;
	private NumReturnResponse NumReturnResponse;
	private PortTerminated PortTerminated;

	public PortActivatedBroadcast getPortActivatedBroadcast() {
		if (portActivatedBroadcast == null) {
			portActivatedBroadcast = new PortActivatedBroadcast();
		}
		return portActivatedBroadcast;
	}

	public void setPortActivatedBroadcast(PortActivatedBroadcast portActivatedBroadcast) {
		this.portActivatedBroadcast = portActivatedBroadcast;
	}

	public NumReturnBroadcast getNumReturnBroadcast() {
		return numReturnBroadcast;
	}

	public void setNumReturnBroadcast(NumReturnBroadcast numReturnBroadcast) {
		this.numReturnBroadcast = numReturnBroadcast;
	}

	public SynchronisationResponse getSynchronisationResponse() {
		return synchronisationResponse;
	}

	public void setSynchronisationResponse(SynchronisationResponse synchronisationResponse) {
		this.synchronisationResponse = synchronisationResponse;
	}

	public SynchronisationRequest getSynchronisationRequest() {
		return SynchronisationRequest;
	}

	public void setSynchronisationRequest(SynchronisationRequest synchronisationRequest) {
		SynchronisationRequest = synchronisationRequest;
	}

	public SubscriberInfoQuery getSubscriberInfoQuery() {
		return SubscriberInfoQuery;
	}

	public void setSubscriberInfoQuery(SubscriberInfoQuery subscriberInfoQuery) {
		SubscriberInfoQuery = subscriberInfoQuery;
	}

	public PortRequest getPortRequest() {
		return PortRequest;
	}

	public void setPortRequest(PortRequest portRequest) {
		PortRequest = portRequest;
	}

	public PortRespWithFlag getPortRespWithFlag() {
		return PortRespWithFlag;
	}

	public void setPortRespWithFlag(PortRespWithFlag portRespWithFlag) {
		PortRespWithFlag = portRespWithFlag;
	}

	public PortExecute getPortExecute() {
		return PortExecute;
	}

	public void setPortExecute(PortExecute portExecute) {
		PortExecute = portExecute;
	}

	public PortDeactWithRte getPortDeactWithRte() {
		return PortDeactWithRte;
	}

	public void setPortDeactWithRte(PortDeactWithRte portDeactWithRte) {
		PortDeactWithRte = portDeactWithRte;
	}

	public PortCancelNotification getPortCancelNotification() {
		return PortCancelNotification;
	}

	public void setPortCancelNotification(PortCancelNotification portCancelNotification) {
		PortCancelNotification = portCancelNotification;
	}

	public NonpaymentDisconnReq getNonpaymentDisconnReq() {
		return NonpaymentDisconnReq;
	}

	public void setNonpaymentDisconnReq(NonpaymentDisconnReq nonpaymentDisconnReq) {
		NonpaymentDisconnReq = nonpaymentDisconnReq;
	}

	public NonpaymentDisconnResp getNonpaymentDisconnResp() {
		return NonpaymentDisconnResp;
	}

	public void setNonpaymentDisconnResp(NonpaymentDisconnResp nonpaymentDisconnResp) {
		NonpaymentDisconnResp = nonpaymentDisconnResp;
	}

	public NpdCancelRequest getNpdCancelRequest() {
		return NpdCancelRequest;
	}

	public void setNpdCancelRequest(NpdCancelRequest npdCancelRequest) {
		NpdCancelRequest = npdCancelRequest;
	}

	public NpdCancelResponse getNpdCancelResponse() {
		return NpdCancelResponse;
	}

	public void setNpdCancelResponse(NpdCancelResponse npdCancelResponse) {
		NpdCancelResponse = npdCancelResponse;
	}

	public NpdAckResponse getNpdAckResponse() {
		return NpdAckResponse;
	}

	public void setNpdAckResponse(NpdAckResponse npdAckResponse) {
		NpdAckResponse = npdAckResponse;
	}

	public ReconnectionNotification getReconnectionNotification() {
		return ReconnectionNotification;
	}

	public void setReconnectionNotification(ReconnectionNotification reconnectionNotification) {
		ReconnectionNotification = reconnectionNotification;
	}

	public ReconnectionAck getReconnectionAck() {
		return ReconnectionAck;
	}

	public void setReconnectionAck(ReconnectionAck reconnectionAck) {
		ReconnectionAck = reconnectionAck;
	}

	public NumReturnRequest getNumReturnRequest() {
		return NumReturnRequest;
	}

	public void setNumReturnRequest(NumReturnRequest numReturnRequest) {
		NumReturnRequest = numReturnRequest;
	}

	public NumReturnRequestWithRte getNumReturnRequestWithRte() {
		return NumReturnRequestWithRte;
	}

	public void setNumReturnRequestWithRte(NumReturnRequestWithRte numReturnRequestWithRte) {
		NumReturnRequestWithRte = numReturnRequestWithRte;
	}

	public NumReturnResponse getNumReturnResponse() {
		return NumReturnResponse;
	}

	public void setNumReturnResponse(NumReturnResponse numReturnResponse) {
		NumReturnResponse = numReturnResponse;
	}

	public PortTerminated getPortTerminated() {
		return PortTerminated;
	}

	public void setPortTerminated(PortTerminated portTerminated) {
		PortTerminated = portTerminated;
	}

}
