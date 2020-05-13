package com.app.server.services;

import com.app.server.http.exceptions.APPBadRequestException;
import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPUnauthorizedException;
import com.app.server.models.HealthRegime.Regime;
import com.app.server.models.HealthRegime.RegimeProgram;
import com.app.server.util.MongoPool;
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

import javax.ws.rs.core.HttpHeaders;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.app.server.util.parser.RegimeParser.convertDocumentToRegime;
import static com.app.server.util.parser.RegimeParser.convertJsonToRegime;
import static com.app.server.util.parser.RegimeParser.convertRegimeToDocument;

public class RegimeService {

    private static RegimeService self;
    private ObjectWriter ow;
    private MongoCollection<Document> regimeCollection = null;

    private RegimeService() {
        this.regimeCollection = MongoPool.getInstance().getCollection("regime");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    public static RegimeService getInstance(){
        if (self == null)
            self = new RegimeService();
        return self;
    }

    public ArrayList<Regime> getAll() {
        ArrayList<Regime> regimeList = new ArrayList<Regime>();

            FindIterable<Document> results = this.regimeCollection.find();
            if (results == null) {
                return regimeList;
            }
            for (Document item : results) {
                Regime regime = convertDocumentToRegime(item);
                regimeList.add(regime);
            }
        return regimeList;
    }

    private void checkAuthentication(HttpHeaders headers,String id) throws Exception{
        List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null)
            throw new APPUnauthorizedException(70,"No Authorization Headers");
        String token = authHeaders.get(0);
        //String clearToken = APPCrypt.decrypt(token);
        if (id.compareTo(token) != 0) {
            throw new APPUnauthorizedException(71,"Invalid token. Please try getting a new token");
        }
    }

    public List<Regime> getRegimeForOneRegimeProgram(List<String> regimesID){
        List<Regime> regimeList = new ArrayList<Regime>();
        FindIterable<Document> results = this.regimeCollection.find();
        if (results == null) {
            return regimeList;
        }
        for (Document item : results) {
            Regime regime = convertDocumentToRegime(item);
            if(regimesID.contains(regime.getHelpFitID())){
                regimeList.add(regime);
            }
        }
        return regimeList;
    }

    public ArrayList<String> getRegimeInRegimeProgram(List<RegimeProgram> regimePrograms){
           ArrayList<String> regimes = new ArrayList<String>();
           regimes = (ArrayList<String>) regimePrograms.get(0).getRegimesID();
       return regimes;
    }


    public Object getRegimeForOneRegimeProgramWhenSubresource(List<String> regimesID, String idTwo, Object request){
        List<Regime> regimeList = new ArrayList<Regime>();
        FindIterable<Document> results = this.regimeCollection.find();
        Regime regUpdate; Object obj = null;
        if (results == null) {
            return regimeList;
        }
        for (Document item : results) {
            Regime regime = convertDocumentToRegime(item);
            if(regimesID.contains(regime.getHelpFitID())){
                regimeList.add(regime);
            }
        }
        for (Regime reg: regimeList) {
            if(reg.getId().contains(idTwo)) {
                regUpdate = reg;
                obj = update(idTwo, request);
                break;
            }
        }
        return obj;
    }

    public Regime getOne(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        Document item = regimeCollection.find(query).first();
        if (item == null) {
            return  null;
        }
        return convertDocumentToRegime(item);
    }

    public Regime create(Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            Regime regime = convertJsonToRegime(json);
            Document doc = convertRegimeToDocument(regime);
            regimeCollection.insertOne(doc);
            ObjectId id = (ObjectId)doc.get( "_id" );
            regime.setId(id.toString());
            return regime;
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

            Document doc = new Document();
            if (json.has("name"))
                doc.append("name",json.getString("name"));
            if (json.has("description"))
                doc.append("description",json.getString("description"));
            if (json.has("regimeType"))
                doc.append("regimeType",json.getString("regimeType"));
            if (json.has("timeRequiredWeeks"))
                doc.append("timeRequiredWeeks",json.getString("timeRequiredWeeks"));

            Document set = new Document("$set", doc);
            regimeCollection.updateOne(query,set);
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
        regimeCollection.deleteOne(query);
        return new JSONObject();
    }
}
