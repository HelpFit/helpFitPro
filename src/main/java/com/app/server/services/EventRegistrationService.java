package com.app.server.services;

import com.app.server.http.exceptions.APPBadRequestException;
import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPUnauthorizedException;
import com.app.server.models.Event.Event;
import com.app.server.models.Event.EventRegistration;
import com.app.server.models.HealthRegime.Regime;
import com.app.server.util.MongoPool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;


import javax.json.Json;
import java.time.Instant;
import java.util.List;

import static com.app.server.util.parser.EventRegistrationParser.convertEventRegistrationToDocument;
import static com.app.server.util.parser.EventRegistrationParser.convertJsonToEventRegistration;
import static com.app.server.util.parser.RegimeParser.convertDocumentToRegime;

public class EventRegistrationService {

    private static EventRegistrationService self;
    private ObjectWriter ow;
    private MongoCollection<Document> eventRegistrationCollection = null;

    private EventRegistrationService(){
        this.eventRegistrationCollection = MongoPool.getInstance().getCollection("eventRegistration");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    public static EventRegistrationService getInstance(){
        if (self == null)
            self = new EventRegistrationService();
        return self;
    }

    public EventRegistration registerUserForEvent(String fitnessUserId, String eventId, Object request){
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            EventRegistration eventRegistration = convertJsonToEventRegistration(fitnessUserId,eventId,json);
            Document doc = convertEventRegistrationToDocument(eventRegistration);
            eventRegistrationCollection.insertOne(doc);
            ObjectId id = (ObjectId)doc.get( "_id" );
            eventRegistration.setId(id.toString());
            return eventRegistration;
        } catch(JsonProcessingException e) {
            System.out.println("Failed to create a document");
            return null;
        } catch(APPBadRequestException e) {
            throw new APPBadRequestException(33, e.getMessage());
        } catch(APPUnauthorizedException e) {
            throw new APPUnauthorizedException(34, e.getMessage());
        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
    }
}
