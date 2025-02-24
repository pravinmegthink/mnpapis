package com.megthink.gateway.model;

public class PortingConfirmationType {

	private String source;
	private String timeStamp;
	private String service;
	private String billingUID1;
	private String requestId;
	private String portingTime;
	private String approval;
	private MSISDNUIDResultType mSISDNUIDResultType;
	private String comment;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getBillingUID1() {
		return billingUID1;
	}

	public void setBillingUID1(String billingUID1) {
		this.billingUID1 = billingUID1;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getPortingTime() {
		return portingTime;
	}

	public void setPortingTime(String portingTime) {
		this.portingTime = portingTime;
	}

	public String getApproval() {
		return approval;
	}

	public void setApproval(String approval) {
		this.approval = approval;
	}

	public MSISDNUIDResultType getmSISDNUIDResultType() {
		if(mSISDNUIDResultType==null) {
			mSISDNUIDResultType = new MSISDNUIDResultType();
		}
		return mSISDNUIDResultType;
	}

	public void setmSISDNUIDResultType(MSISDNUIDResultType mSISDNUIDResultType) {
		this.mSISDNUIDResultType = mSISDNUIDResultType;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "PortingConfirmationType [source=" + source + ", timeStamp=" + timeStamp + ", service=" + service
				+ ", billingUID1=" + billingUID1 + ", requestId=" + requestId + ", portingTime=" + portingTime
				+ ", approval=" + approval + ", mSISDNUIDResultType=" + mSISDNUIDResultType + ", comment=" + comment
				+ "]";
	}

}