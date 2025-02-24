package com.megthink.gateway.model;

public class DashboardChart {

	private int count;
	private String dates;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getDates() {
		return dates;
	}

	public void setDates(String dates) {
		this.dates = dates;
	}

	@Override
	public String toString() {
		return "DashboardChart [count=" + count + ", dates=" + dates + "]";
	}

}
