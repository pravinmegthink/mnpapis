package com.megthink.gateway.model;

public class NumberRangeFlagged {

    private String NumberFrom;
    private String NumberTo;
    private String RangeAccepted;
    private String ReasonCode;

    public String getNumberFrom() {
        return NumberFrom;
    }

    public void setNumberFrom(String numberFrom) {
        NumberFrom = numberFrom;
    }

    public String getNumberTo() {
        return NumberTo;
    }

    public void setNumberTo(String numberTo) {
        NumberTo = numberTo;
    }

    public String getRangeAccepted() {
        return RangeAccepted;
    }

    public void setRangeAccepted(String rangeAccepted) {
        RangeAccepted = rangeAccepted;
    }

    public String getReasonCode() {
        return ReasonCode;
    }

    public void setReasonCode(String reasonCode) {
        ReasonCode = reasonCode;
    }
}
