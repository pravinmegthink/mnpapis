package com.megthink.gateway.model;

public class ConnectionAnswerCon {

	private String source;
	private String timeStamp;
	private String service;
	private MSISDNUIDType mSISDNUIDType;

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

	public MSISDNUIDType getmSISDNUIDType() {
		if (mSISDNUIDType == null) {
			mSISDNUIDType = new MSISDNUIDType();
		}
		return mSISDNUIDType;
	}

	public void setmSISDNUIDType(MSISDNUIDType mSISDNUIDType) {
		this.mSISDNUIDType = mSISDNUIDType;
	}

	@Override
	public String toString() {
		return "ConnectionAnswerCon [source=" + source + ", timeStamp=" + timeStamp + ", service=" + service
				+ ", mSISDNUIDType=" + mSISDNUIDType + "]";
	}

}
