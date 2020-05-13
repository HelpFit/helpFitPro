package com.app.server.models.Event;

public class EventRegistration {

    String id;
    String eventID;
    String userID;
    boolean isFavorite;
    boolean isRegistered;
    String paymentStatus;
    String paymentMode;
    String transactionID;
    String registrationTime;
    String registrationDate;
    boolean isActive;
    String timeCreated;
    String timeUpdated;

    public EventRegistration(String eventID, String userID, boolean isFavorite, boolean isRegistered, String paymentStatus, String paymentMode, String transactionID, String registrationTime, String registrationDate, boolean isActive, String timeCreated, String timeUpdated) {
        this.eventID = eventID;
        this.userID = userID;
        this.isFavorite = isFavorite;
        this.isRegistered = isRegistered;
        this.paymentStatus = paymentStatus;
        this.paymentMode = paymentMode;
        this.transactionID = transactionID;
        this.registrationTime = registrationTime;
        this.registrationDate = registrationDate;
        this.isActive = isActive;
        this.timeCreated = timeCreated;
        this.timeUpdated = timeUpdated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(String registrationTime) {
        this.registrationTime = registrationTime;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(String timeUpdated) {
        this.timeUpdated = timeUpdated;
    }
}
