package com.megthink.gateway.model;

import java.util.List;

public class PortRequest {

    private List<NumberRange> NumberRange;
    private String DonorLSAID;
    private String Donor;
    private String RecipientLSAID;
    private String Recipient;
    private String AccountPayType;
    private String PortingCode;
    private String CorpPortFlag;
    private String DocumentFileName;
    private String SubRequestTime;
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

    public String getAccountPayType() {
        return AccountPayType;
    }

    public void setAccountPayType(String accountPayType) {
        AccountPayType = accountPayType;
    }

    public String getPortingCode() {
        return PortingCode;
    }

    public void setPortingCode(String portingCode) {
        PortingCode = portingCode;
    }

    public String getCorpPortFlag() {
        return CorpPortFlag;
    }

    public void setCorpPortFlag(String corpPortFlag) {
        CorpPortFlag = corpPortFlag;
    }

    public String getDocumentFileName() {
        return DocumentFileName;
    }

    public void setDocumentFileName(String documentFileName) {
        DocumentFileName = documentFileName;
    }

    public String getSubRequestTime() {
        return SubRequestTime;
    }

    public void setSubRequestTime(String subRequestTime) {
        SubRequestTime = subRequestTime;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }
}
