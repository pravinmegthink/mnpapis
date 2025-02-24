package com.megthink.gateway.model;

import java.util.List;

public class PortRespWithFlag {

     private List<NumberRangeFlagged> NumberRangeFlagged;
     private String DonorLSAID;
     private String Donor;
     private String RecipientLSAID;
     private String Recipient;
     private String CorpPortFlag;
     private String PortAccepted;
     private String PortTime;
     private String Comments;

     public List<NumberRangeFlagged> getNumberRangeFlagged() {
          return NumberRangeFlagged;
     }

     public void setNumberRangeFlagged(List<NumberRangeFlagged> numberRangeFlagged) {
          NumberRangeFlagged = numberRangeFlagged;
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

     public String getCorpPortFlag() {
          return CorpPortFlag;
     }

     public void setCorpPortFlag(String corpPortFlag) {
          CorpPortFlag = corpPortFlag;
     }

     public String getPortAccepted() {
          return PortAccepted;
     }

     public void setPortAccepted(String portAccepted) {
          PortAccepted = portAccepted;
     }

     public String getPortTime() {
          return PortTime;
     }

     public void setPortTime(String portTime) {
          PortTime = portTime;
     }

     public String getComments() {
          return Comments;
     }

     public void setComments(String comments) {
          Comments = comments;
     }
}
