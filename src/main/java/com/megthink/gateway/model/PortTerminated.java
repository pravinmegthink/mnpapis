package com.megthink.gateway.model;

import java.util.List;

public class PortTerminated {

    private List<NumberRange> NumberRange;
    private String DonorLSAID;
    private String Donor;
    private String RecipientLSAID;
    private String Recipient;
    private String Comments;

    public List<NumberRange> getNumberRange() {
        return NumberRange;
    }

    public void setNumberRange(List<NumberRange> numberRange) {
        NumberRange = numberRange;
    }

    public String getDonorLSAID() {
        return DonorLSAID;
    }

    public void setDonorLSAID(String donorLSAID) {
        DonorLSAID = donorLSAID;
    }

    public String getDonor() {
        return Donor;
    }

    public void setDonor(String donor) {
        Donor = donor;
    }

    public String getRecipientLSAID() {
        return RecipientLSAID;
    }

    public void setRecipientLSAID(String recipientLSAID) {
        RecipientLSAID = recipientLSAID;
    }

    public String getRecipient() {
        return Recipient;
    }

    public void setRecipient(String recipient) {
        Recipient = recipient;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }
}
