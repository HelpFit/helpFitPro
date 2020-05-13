package com.app.server.models.Payment;

public class ThirdPartyPayment {

    String transactionId;
    String userReferenceId;
    //String userName;
    String paymentType;
    String paymentAmount;
    String paymentCurrency;
    String requestTime;
    String paymentStatus;
    String paymentTime;
    String paymentDeclineReason;

    public ThirdPartyPayment(){ }

    public ThirdPartyPayment(String userReferenceId,
                             String paymentType, String paymentAmount,
                             String paymentCurrency, String requestTime,
                             String paymentStatus) {
        this.userReferenceId = userReferenceId;
        //this.userName = userName;
        this.paymentType = paymentType;
        this.paymentAmount = paymentAmount;
        this.paymentCurrency = paymentCurrency;
        this.requestTime = requestTime;
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserReferenceId() {
        return userReferenceId;
    }

    public void setUserReferenceId(String userReferenceId) {
        this.userReferenceId = userReferenceId;
    }

    /*public String getUserName() {
        return userName;
    }*/

    /*public void setUserName(String userFirstName) {
        this.userName = userName;
    }*/

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentCurrency() {
        return paymentCurrency;
    }

    public void setPaymentCurrency(String paymentCurrency) {
        this.paymentCurrency = paymentCurrency;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getPaymentDeclineReason() {
        return paymentDeclineReason;
    }

    public void setPaymentDeclineReason(String paymentDeclineReason) {
        this.paymentDeclineReason = paymentDeclineReason;
    }
}
