package com.app.server.services;

import com.app.server.http.exceptions.APPBadRequestException;
import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPUnauthorizedException;
import com.app.server.models.Event.Event;
import com.app.server.models.HealthRegime.Regime;
import com.app.server.models.HealthRegime.RegimeProgram;
import com.app.server.models.Preferences.Ailment;
import com.app.server.models.Preferences.Habit;
import com.app.server.models.Preferences.Interest;
import com.app.server.models.UserInfo.UserEventRegime;
import com.app.server.util.MongoPool;
import com.app.server.util.parser.EventDocumentParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * EventOrganizerService
 *
 * <a href="mailto:sivashav@andrew.cmu.edu">Siva Shankaran Vasanth</a>
 */
public class EventService {

    private static EventService instance;
    private ObjectWriter ow;
    private MongoCollection<Document> eventCollection = null;
    private RegimeService regimeService;

    private EventService() {
        this.eventCollection = MongoPool.getInstance().getCollection("event");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        regimeService = RegimeService.getInstance();
    }

    public static EventService getInstance(){
        if (instance == null) {
            synchronized (EventService .class) {
                if (instance == null) {
                    instance = new EventService();
                }
            }
        }
        return instance;
    }

    public ArrayList<Event> getEventsByType(UriInfo info){
        ArrayList<Event> eventList = new ArrayList<Event>();
        String eventVal = info.getQueryParameters().getFirst("eventFilter");
        int val = Integer.parseInt(info.getQueryParameters().getFirst("value"));
        FindIterable<Document> results = this.eventCollection.find();
        if (results == null) {
            return eventList;
        }
        for (Document item : results) {
            Event event = EventDocumentParser.convertDocumentToEvent(item);
            if(eventVal.equalsIgnoreCase("ailments")){
                List<Ailment> ailmentsVal = event.getAilmentTags();
                for(Ailment ail:ailmentsVal){
                    if(ail.getId() == val){
                        eventList.add(event);
                    }
                }
            }else if(eventVal.equalsIgnoreCase("interest")){
                List<Interest> interestsVal = event.getInterestTags();
                for(Interest interest:interestsVal){
                    if(interest.getId() == val){
                        eventList.add(event);
                    }
                }
            }else {
                List<Habit> habitsVal = event.getHabitTags();
                for(Habit habit:habitsVal){
                    if(habit.getId() == val){
                        eventList.add(event);
                    }
                }
            }
        }
        return eventList;
    }

    public ArrayList<Event> getAllEvents() {
        ArrayList<Event> eventList = new ArrayList<Event>();

        FindIterable<Document> results = this.eventCollection.find();
        if (results == null) {
            return eventList;
        }
        for (Document item : results) {
            Event event = EventDocumentParser.convertDocumentToEvent(item);
            eventList.add(event);
        }
        return eventList;
    }

    public ArrayList<UserEventRegime> getEventsByRegime(List<String> regimesId){
        ArrayList<UserEventRegime> userEventRegimes = new ArrayList<UserEventRegime>();
        ArrayList<Event> eventsAll = getAllEvents();
        List<Event> eventsRecomended = new ArrayList<Event>();
        for(String regId: regimesId){
            Regime regime = regimeService.getOne(regId);
            List<String> ailmentRegime = regime.getAilmentTags();
            for(Event event: eventsAll){
                List<Ailment> ailmentEvent = event.getAilmentTags();
                boolean isRecommended = true;
                for(int i=0;i<ailmentEvent.size();i++){
                    if(ailmentEvent.get(0).getId() != Integer.parseInt(ailmentRegime.get(0))){
                        isRecommended = false;
                        break;
                    }
                }
                if(isRecommended){
                    eventsRecomended.add(event);
                }
            }
            if(!eventsRecomended.isEmpty()){
                UserEventRegime userEventRegime = new UserEventRegime(regime,eventsRecomended);
                userEventRegimes.add(userEventRegime);
            }
        }
        return userEventRegimes;
    }


    public ArrayList<Event> getEvents(ArrayList<RegimeProgram> RegimePrograms){
        ArrayList<Event> eventsRecommended = new ArrayList<Event>();
        ArrayList<Event> eventsAll = getAllEvents();
        for(RegimeProgram regimeProgram: RegimePrograms){
           List<String> ailmentRegime = regimeProgram.getAilmentsAddressed();
           for(Event event: eventsAll){
            List<Ailment> ailmentEvent = event.getAilmentTags();
             boolean isRecommended = true;
              for(int i=0;i<ailmentEvent.size();i++){
                   if(ailmentEvent.get(0).getId() != Integer.parseInt(ailmentRegime.get(0))){
                     isRecommended = false;
                     break;
                   }
               }
               if(isRecommended) eventsRecommended.add(event);

           }
        }
        return eventsRecommended;
    }



    public Event getEvent(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        Document item = this.eventCollection.find(query).first();
        if (item == null) {
            return null;
        }
        return EventDocumentParser.convertDocumentToEvent(item);
    }

    public List<Event> getEventsByOrganizer(String id){
        ArrayList<Event> eventList = new ArrayList<Event>();
        BasicDBObject query = new BasicDBObject();
        query.put("organizerId", id);
        FindIterable<Document> results = this.eventCollection.find(query);
        if (results == null) {
            return eventList;
        }
        for (Document item : results) {
            Event event = EventDocumentParser.convertDocumentToEvent(item);
            eventList.add(event);
        }
        return eventList;
    }

    public Event create(Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            Event event = EventDocumentParser.convertJsonToEvent(json);
            Document doc = EventDocumentParser.convertEventToDocument(event);
            eventCollection.insertOne(doc);
            ObjectId id = (ObjectId)doc.get( "_id" );
            event.setId(id.toString());
            return event;
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
            Document set = new Document("$set", EventDocumentParser.convertJsonToEventDocument(json));
            eventCollection.updateOne(query,set);
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
        eventCollection.deleteOne(query);
        return new JSONObject();
    }

    public Object deleteAll() {
        eventCollection.deleteMany(new BasicDBObject());
        return new JSONObject();
    }
}