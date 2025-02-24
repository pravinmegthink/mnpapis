package com.megthink.gateway.model;

public class ConnectionOrderType {

	private String Source;
	private String TimeStamp;
	private String Service;
	private MSISDNUIDType mSISDNUIDType;

	public String getSource() {
		return Source;
	}

	public void setSource(String source) {
		Source = source;
	}

	public String getTimeStamp() {
		return TimeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		TimeStamp = timeStamp;
	}

	public String getService() {
		return Service;
	}

	public void setService(String service) {
		Service = service;
	}

	public MSISDNUIDType getmSISDNUIDType() {
		if(mSISDNUIDType == null) {
			mSISDNUIDType = new MSISDNUIDType();
		}
		return mSISDNUIDType;
	}

	public void setmSISDNUIDType(MSISDNUIDType mSISDNUIDType) {
		this.mSISDNUIDType = mSISDNUIDType;
	}

	@Override
	public String toString() {
		return "ConnectionOrderType [Source=" + Source + ", TimeStamp=" + TimeStamp + ", Service=" + Service
				+ ", mSISDNUIDType=" + mSISDNUIDType + "]";
	}

}
