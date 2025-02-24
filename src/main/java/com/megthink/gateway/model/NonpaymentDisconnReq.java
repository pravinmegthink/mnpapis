package com.megthink.gateway.model;

import java.util.List;

public class NonpaymentDisconnReq {
    private List<NumberRange> NumberRange;
   private  String BillAmount;
   private String BillDate ;
   private String BillDueDate;
   private String Comments;

    public List<NumberRange> getNumberRange() {
        return NumberRange;
    }

    public void setNumberRange(List<NumberRange> numberRange) {
        NumberRange = numberRange;
    }

    public String getBillAmount() {
        return BillAmount;
    }

    public void setBillAmount(String billAmount) {
        BillAmount = billAmount;
    }

    public String getBillDate() {
        return BillDate;
    }

    public void setBillDate(String billDate) {
        BillDate = billDate;
    }

    public String getBillDueDate() {
        return BillDueDate;
    }

    public void setBillDueDate(String billDueDate) {
        BillDueDate = billDueDate;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }
}
