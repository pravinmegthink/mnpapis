package com.megthink.gateway.model;

public class NumReturnRequestWithRte {
            private NumberRange NumberRange;
			private String LastRecipientLSAID;
		    private String LastRecipient;
			private String Route;
			private String Comments;

    public NumberRange getNumberRange() {
        return NumberRange;
    }

    public void setNumberRange(NumberRange numberRange) {
        NumberRange = numberRange;
    }

    public String getLastRecipientLSAID() {
        return LastRecipientLSAID;
    }

    public void setLastRecipientLSAID(String lastRecipientLSAID) {
        LastRecipientLSAID = lastRecipientLSAID;
    }

    public String getLastRecipient() {
        return LastRecipient;
    }

    public void setLastRecipient(String lastRecipient) {
        LastRecipient = lastRecipient;
    }

    public String getRoute() {
        return Route;
    }

    public void setRoute(String route) {
        Route = route;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }
}





