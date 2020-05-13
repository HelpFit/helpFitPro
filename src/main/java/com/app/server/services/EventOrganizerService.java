package com.app.server.services;

import com.app.server.http.exceptions.APPBadRequestException;
import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPUnauthorizedException;
import com.app.server.models.Event.Event;
import com.app.server.models.Preferences.RegionsOfService;
import com.app.server.models.User.EventOrganizer;
import com.app.server.models.User.User;
import com.app.server.util.MongoPool;
import com.app.server.util.parser.EventOrganizerDocumentParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * EventOrganizerService
 *
 * <a href="mailto:sivashav@andrew.cmu.edu">Siva Shankaran Vasanth</a>
 */
public class EventOrganizerService {

    private static EventOrganizerService instance;
    private static EventService eventServiceInstance;
    private static UserService userServiceInstance;
    private ObjectWriter ow;
    private MongoCollection<Document> organizerCollection = null;

    private EventOrganizerService() {
        this.eventServiceInstance = EventService.getInstance();
        this.userServiceInstance = UserService.getInstance();
        this.organizerCollection = MongoPool.getInstance().getCollection("organizer");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    public static EventOrganizerService getInstance(){
        if (instance == null) {
            synchronized (EventOrganizerService.class) {
                if (instance == null) {
                    instance = new EventOrganizerService();
                }
            }
        }
        return instance;
    }

    public ArrayList<EventOrganizer> getAllEventOrganizers() {
        ArrayList<EventOrganizer> organizerList = new ArrayList<EventOrganizer>();

        FindIterable<Document> results = this.organizerCollection.find();
        if (results == null) {
            return organizerList;
        }
        for (Document item : results) {
            EventOrganizer organizer = EventOrganizerDocumentParser.convertDocumentToEventOrganizer(item);
            User user = userServiceInstance.getUser(organizer.getUserId());
            List<Event> events = eventServiceInstance.getEventsByOrganizer(organizer.getId());
            organizer.setUserDetails(user);
            organizer.setEvents(events);
            organizerList.add(organizer);
        }
        return organizerList;
    }

    public EventOrganizer getEventOrganizer(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        Document item = this.organizerCollection.find(query).first();
        if (item == null) {
            return null;
        }
        EventOrganizer organizer = EventOrganizerDocumentParser.convertDocumentToEventOrganizer(item);
        User user = userServiceInstance.getUser(organizer.getUserId());
        List<Event> events = eventServiceInstance.getEventsByOrganizer(organizer.getId());
        organizer.setUserDetails(user);
        organizer.setEvents(events);
        return organizer;
    }

    public List<Event> getEventsByOrganizer(String id){
        return eventServiceInstance.getEventsByOrganizer(id);
    }

    public EventOrganizer create(Object request, String userId) {

        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            validateRequest(json);
            Document doc = EventOrganizerDocumentParser.convertJsonToEventOrganizerDocument(json, userId);
            organizerCollection.insertOne(doc);
            EventOrganizer eventOrganizer = EventOrganizerDocumentParser.convertJsonToEventOrganizer(json, userId);
            ObjectId id = (ObjectId)doc.get("_id");
            eventOrganizer.setId(id.toString());
            return eventOrganizer;
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

    public Object update(String id, Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));
            Document set = new Document("$set",
                    EventOrganizerDocumentParser.convertJsonToEventOrganizerDocument(json, json.getString("userId")));
            organizerCollection.updateOne(query,set);
            return request;

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

    public Object delete(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        organizerCollection.deleteOne(query);
        return new JSONObject();
    }


    public Object deleteAll() {
        organizerCollection.deleteMany(new BasicDBObject());
        return new JSONObject();
    }

    public Boolean validateRequest(JSONObject json){
        if (!json.has("regionsOfService"))
            throw new APPBadRequestException(55, "regions Of Service Missing");
        return true;
    }
}