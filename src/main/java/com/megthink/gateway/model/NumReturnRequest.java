package com.megthink.gateway.model;

public class NumReturnRequest {
    private NumberRange NumberRange ;
    private String LastRecipientLSAID;
    private String LastRecipient ;
    private String  NPDTransactionID;
    private String Comments;

    public String getLastRecipient() {
        return LastRecipient;
    }

    public void setLastRecipient(String lastRecipient) {
        LastRecipient = lastRecipient;
    }
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


    public String getNPDTransactionID() {
        return NPDTransactionID;
    }

    public void setNPDTransactionID(String NPDTransactionID) {
        this.NPDTransactionID = NPDTransactionID;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }
}
