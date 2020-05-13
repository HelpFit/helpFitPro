package com.app.server.services;

import com.app.server.http.exceptions.APPBadRequestException;
import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPUnauthorizedException;
import com.app.server.models.Event.Event;
import com.app.server.models.Event.EventRegistration;
import com.app.server.models.HealthRegime.Regime;
import com.app.server.models.HealthRegime.RegimeProgram;
import com.app.server.models.Payment.Transaction;
import com.app.server.models.Preferences.Ailment;
import com.app.server.models.Preferences.Habit;
import com.app.server.models.Preferences.Interest;
import com.app.server.models.User.FitnessUser;
import com.app.server.models.User.User;
import com.app.server.models.UserInfo.UserEvaluation;
import com.app.server.models.UserInfo.UserEventRegime;
import com.app.server.util.APPCrypt;
import com.app.server.util.MongoPool;
import com.app.server.util.parser.FitnessUserDocumentParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import java.util.ArrayList;
import java.util.List;

/**
 * EventOrganizerService
 *
 * <a href="mailto:sivashav@andrew.cmu.edu">Siva Shankaran Vasanth</a>
 */
public class FitnessUserService {

    private static FitnessUserService instance;
    private EventRegistrationService eventRegistrationService;
    private static UserService userServiceInstance;
    private ObjectWriter ow;
    private MongoCollection<Document> fitnessUserCollection = null;
    private TransactionService transactionService;
    private RegimeProgramService regimeProgramService;
    private EventService eventService;
    private RegimeService regimeService;

    private FitnessUserService() {
        this.fitnessUserCollection = MongoPool.getInstance().getCollection("fitnessUser");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        this.userServiceInstance = UserService.getInstance();
        this.eventRegistrationService = EventRegistrationService.getInstance();
        this.transactionService = TransactionService.getInstance();
        this.regimeProgramService = RegimeProgramService.getInstance();
        this.eventService = EventService.getInstance();
        this.regimeService = RegimeService.getInstance();
    }

    public static FitnessUserService getInstance(){
        if (instance == null) {
            synchronized (FitnessUserService.class) {
                if (instance == null) {
                    instance = new FitnessUserService();
                }
            }
        }
        return instance;
    }

    public FitnessUser getFitnessUser(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        Document item = this.fitnessUserCollection.find(query).first();
        if (item == null) {
            return null;
        }
        FitnessUser fitnessUser = FitnessUserDocumentParser.convertDocumentToFitnessUser(item);
        User user = userServiceInstance.getUser(fitnessUser.getUserId());
        fitnessUser.setUserDetails(user);
        return fitnessUser;
    }

    public FitnessUser create(Object request, String userId) {

        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            validateRequest(json);
            Document doc = FitnessUserDocumentParser.convertJsonToFitnessUserDocument(json, userId);
            fitnessUserCollection.insertOne(doc);
            //TODO: fix data inconsistency
            FitnessUser fitnessUser = FitnessUserDocumentParser.convertJsonToFitnessUser(json, userId);
            ObjectId id = (ObjectId)doc.get("_id");
            fitnessUser.setId(id.toString());
            return fitnessUser;
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

            Document set = new Document("$set", FitnessUserDocumentParser.
                    convertJsonToFitnessUserDocument(json, json.getString("userId")));
            fitnessUserCollection.updateOne(query,set);
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
        fitnessUserCollection.deleteOne(query);
        return new JSONObject();
    }

    public Object deleteAll() {
        fitnessUserCollection.deleteMany(new BasicDBObject());
        return new JSONObject();
    }

    public EventRegistration registerUserForEvent(HttpHeaders headers, String fitnessUserId, String eventId, Object request){
        EventRegistration eventRegistration;
        try{
            checkAuthentication(headers, fitnessUserId);
            eventRegistration = eventRegistrationService.registerUserForEvent(fitnessUserId,eventId,request);
        } catch(APPBadRequestException e) {
           throw new APPBadRequestException(33, e.getMessage());
        } catch(APPUnauthorizedException e) {
           throw new APPUnauthorizedException(34, e.getMessage());
        } catch(Exception e) {
           System.out.println("EXCEPTION!!!!");
           e.printStackTrace();
           throw new APPInternalServerException(99, e.getMessage());
        }
       return eventRegistration;
    }

    public Transaction userPayForEvent(HttpHeaders headers,String fitnessUserId, String eventId, Object request){
        Transaction trans = null;
        try {
            checkAuthentication(headers,fitnessUserId);
            trans = transactionService.create(request);
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
        return trans;
    }

    public ArrayList<Transaction> getUserTransactions(HttpHeaders headers, String id){
        ArrayList<Transaction> userTransactions = null;
        try {
            checkAuthentication(headers, id);
            userTransactions = transactionService.getAllUserTranscations(id);
        }catch(JsonProcessingException e) {
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
        return userTransactions;
    }

    public UserEvaluation getEvaluateAndUserProgram(HttpHeaders headers, String id){
        ArrayList<RegimeProgram> regimePrograms = null;
        ArrayList<String> regimesId = null;
        ArrayList<UserEventRegime> userEventRegimes = null;
        try{
            checkAuthentication(headers, id);
            regimePrograms = getUserRegimeProgram(headers,id);
            regimesId = regimeService.getRegimeInRegimeProgram(regimePrograms);
            userEventRegimes = eventService.getEventsByRegime(regimesId);
          return new UserEvaluation(regimePrograms.get(0),userEventRegimes);
        }catch(JsonProcessingException e) {
            System.out.println("Failed to create a document");
            return null;
        } catch(APPBadRequestException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }catch(APPUnauthorizedException e) {
            throw new APPUnauthorizedException(34, e.getMessage());
        }catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
    }
    /*public UserEvaluation evaluateUserProgram(HttpHeaders headers, String id){
        ArrayList<RegimeProgram> regimePrograms = null;
        ArrayList<Event> eventsRecommended = null;
        try {
            checkAuthentication(headers, id);
            regimePrograms = getUserRegimeProgram(headers,id);
            eventsRecommended = eventService.getEvents(regimePrograms);
           return new UserEvaluation(regimePrograms, eventsRecommended);
        }catch(JsonProcessingException e) {
            System.out.println("Failed to create a document");
           return null;
        } catch(APPBadRequestException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }catch(APPUnauthorizedException e) {
            throw new APPUnauthorizedException(34, e.getMessage());
        }catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
    }*/

    public ArrayList<RegimeProgram> getUserRegimeProgram(HttpHeaders headers, String id){
        ArrayList<RegimeProgram> regimePrograms = null;
        try {
            checkAuthentication(headers, id);
            regimePrograms = regimeProgramService.getUserRegimeProgram(id);
        }catch(JsonProcessingException e) {
            System.out.println("Failed to create a document");
            return null;
        } catch(APPBadRequestException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }catch(APPUnauthorizedException e) {
            throw new APPUnauthorizedException(34, e.getMessage());
        }catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
        return regimePrograms;
    }

    public RegimeProgram createUserRegimeProgram(HttpHeaders headers, String id, Object request){
        RegimeProgram regimeProgram = null;
        try {
            checkAuthentication(headers, id);
            regimeProgram = regimeProgramService.create(request);
        }catch(JsonProcessingException e) {
            System.out.println("Failed to create a document");
            return null;
        } catch(APPBadRequestException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }catch(APPUnauthorizedException e) {
            throw new APPUnauthorizedException(34, e.getMessage());
        }catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
        return regimeProgram;
    }

    public Object deleteUserRegimeProgram(HttpHeaders headers, String id){
        Object obj;
        try {
            checkAuthentication(headers, id);
            obj = regimeProgramService.delete(id);
        }catch(JsonProcessingException e) {
            System.out.println("Failed to create a document");
            return null;
        } catch(APPBadRequestException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }catch(APPUnauthorizedException e) {
            throw new APPUnauthorizedException(34, e.getMessage());
        }catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
        return obj;
    }


    public Boolean validateRequest(JSONObject json){
        if (!json.has("gender"))
            throw new APPBadRequestException(55, "gender Missing");
        if (!json.has("height"))
            throw new APPBadRequestException(55, "height Missing");
        if (!json.has("weight"))
            throw new APPBadRequestException(55, "weight Missing");
        if (!json.has("city"))
            throw new APPBadRequestException(55, "city Missing");
        if (!json.has("state"))
            throw new APPBadRequestException(55, "state Missing");
        if (!json.has("zipCode"))
            throw new APPBadRequestException(55, "zipCode Missing");
        if (!json.has("ailmentTags"))
            throw new APPBadRequestException(55, "ailments Missing");
        if (!json.has("interestTags"))
            throw new APPBadRequestException(55, "interests Missing");
        if (!json.has("habitTags"))
            throw new APPBadRequestException(55, "habits Missing");
        if (!json.has("termsConsent"))
            throw new APPBadRequestException(55, "terms Missing");
        if (!json.has("receiveEmailNotifications"))
            throw new APPBadRequestException(55, "receive email notif Missing");
        return true;
    }

    private void checkAuthentication(HttpHeaders headers,String id) throws Exception{
        List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null)
            throw new APPUnauthorizedException(70,"No Authorization Headers");
        String token = authHeaders.get(0);
        String clearToken = APPCrypt.decrypt(token);
        if (id.compareTo(clearToken) != 0) {
            throw new APPUnauthorizedException(71,"Invalid token. Please try getting a new token");
        }
    }
}