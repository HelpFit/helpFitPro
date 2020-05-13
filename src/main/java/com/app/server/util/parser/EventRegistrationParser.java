package com.app.server.util.parser;

import com.app.server.models.Event.EventRegistration;
import org.bson.Document;
import org.json.JSONObject;

import java.time.Instant;

public class EventRegistrationParser {

    public static EventRegistration convertDocumentToEventRegistration(Document item) {
        EventRegistration eventRegistration = new EventRegistration(
                item.getString("eventID"),
                item.getString("userID"),
                item.getBoolean("isFavorite"),
                item.getBoolean("isRegistered"),
                item.getString("paymentStatus"),
                item.getString("paymentMode"),
                item.getString("transactionID"),
                item.getString("registrationTime"),
                item.getString("registrationDate"),
                item.getBoolean("isActive"),
                item.getString("timeCreated"),
                item.getString("timeUpdated")
        );
        return eventRegistration;
    }

    public static Document convertEventRegistrationToDocument(EventRegistration eventRegistration){
        Document doc = new Document("eventID", eventRegistration.getEventID())
                .append("userID", eventRegistration.getUserID())
                .append("isFavorite", eventRegistration.isFavorite())
                .append("isRegistered", eventRegistration.isRegistered())
                .append("paymentStatus", eventRegistration.getPaymentStatus())
                .append("paymentMode", eventRegistration.getPaymentMode())
                .append("transactionID", eventRegistration.getTransactionID())
                .append("registrationTime", eventRegistration.getRegistrationTime())
                .append("registrationDate", eventRegistration.getRegistrationDate())
                .append("isActive", eventRegistration.isActive())
                .append("timeCreated",eventRegistration.getTimeCreated())
                .append("timeUpdated", eventRegistration.getTimeUpdated());
        return doc;
    }

    public static EventRegistration convertJsonToEventRegistration(String fitnessUserId, String eventId, JSONObject json){

        EventRegistration eventRegistration = new EventRegistration(
                eventId,
                fitnessUserId,
                json.getBoolean("isFavorite"),
                json.getBoolean("isRegistered"),
                json.getString("paymentStatus"),
                json.getString("paymentMode"),
                json.getString("transactionID"),
                json.getString("registrationTime"),
                json.getString("registrationDate"),
                json.getBoolean("isActive"),
                json.has("timeCreated")?json.getString("isFulfilled"): String.valueOf(Instant.now().getEpochSecond()),
                String.valueOf(Instant.now().getEpochSecond())
        );
        return eventRegistration;
    }
}
