package com.app.server.services;

import com.app.server.http.exceptions.APPBadRequestException;
import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPUnauthorizedException;
import com.app.server.models.Payment.ThirdPartyPayment;
import com.app.server.models.Payment.Transaction;
import com.app.server.models.User.User;
import com.app.server.util.MongoPool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import static com.app.server.util.parser.TransactionParser.convertDocumentToTransaction;
import static com.app.server.util.parser.TransactionParser.convertJsonToTransaction;
import static com.app.server.util.parser.TransactionParser.convertTransactionToDocument;
import static javax.ws.rs.core.HttpHeaders.USER_AGENT;

public class TransactionService {

    private static TransactionService self;
    private ObjectWriter ow;
    private MongoCollection<Document> transactionCollection = null;
    private ThirdPartyPaymentService thirdPartyService;

    private TransactionService() {
        this.transactionCollection = MongoPool.getInstance().getCollection("transaction");
        thirdPartyService = ThirdPartyPaymentService.getInstance();
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    public static TransactionService getInstance(){
        if (self == null)
            self = new TransactionService();
        return self;
    }

    public Transaction getOne(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        Document item = transactionCollection.find(query).first();
        if (item == null) {
            return  null;
        }
        return convertDocumentToTransaction(item);
    }

    public Transaction create(Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            Transaction transaction = convertJsonToTransaction(json);

            ThirdPartyPayment thirdPartyPayment = new ThirdPartyPayment(
                    transaction.getFitnessUserId(),
                    transaction.getPaymentMode(),
                    transaction.getAmount().toString(),
                    "USD",
                    String.valueOf(Instant.now().getEpochSecond()),
                    "REQUESTED");
            try {
                thirdPartyPayment = makeTransaction(thirdPartyPayment);
                //TO-DO: Change to HTTP Post request
                //thirdPartyPayment = thirdPartyService.makePayment(thirdPartyPayment);
                transaction.setTransactionState("SUCCESSFUL");
                transaction.setTransactionTime(thirdPartyPayment.getPaymentTime());
            } catch (APPInternalServerException e){
                transaction.setAmount(0.0);
                transaction.setTransactionState("FAILED");
            }
            Document doc = convertTransactionToDocument(transaction);
            transactionCollection.insertOne(doc);
            ObjectId id = (ObjectId)doc.get( "_id" );
            transaction.setId(id.toString());
            return transaction;
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

    private ThirdPartyPayment makeTransaction(ThirdPartyPayment thirdPartyPayment) throws Exception{
        //String target = "http://" + service.getHost() + ":" + service.getPort() + PROFILES_URI;
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(thirdPartyPayment);

        StringEntity entity = new StringEntity(json,
                ContentType.APPLICATION_JSON);

        HttpClient httpClient = HttpClientBuilder.create().build();
        //TO-DO: URL hardcoded, make it dynamic
        HttpPost request = new HttpPost("http://localhost:8080/api/payment");
        request.setEntity(entity);

        HttpResponse response = httpClient.execute(request);
        HttpEntity entityVal = response.getEntity();
        String content = EntityUtils.toString(entityVal);

        ObjectMapper mapper = new ObjectMapper();
        ThirdPartyPayment thirdParty = mapper.readValue(content, ThirdPartyPayment.class);
      return  thirdParty;
    }

    public ArrayList<Transaction> getAllUserTranscations(String id){
        ArrayList<Transaction> transactionsList = new ArrayList<Transaction>();

        FindIterable<Document> results = this.transactionCollection.find();
        if (results == null) {
            return transactionsList;
        }
        for (Document item : results) {
            Transaction transaction = convertDocumentToTransaction(item);
            if(transaction.getFitnessUserId().equals(id)){
                transactionsList.add(transaction);}
        }
        return transactionsList;
    }


    public Object update(String id, Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            Document doc = new Document();
            if (json.has("eventId"))
                doc.append("eventId",json.getString("eventId"));
            if (json.has("organizerId"))
                doc.append("organizerId",json.getString("organizerId"));
            if (json.has("fitnessUserId"))
                doc.append("fitnessUserId",json.getString("fitnessUserId"));
            if (json.has("amount"))
                doc.append("amount",json.getDouble("amount"));
            if (json.has("currency"))
                doc.append("currency",json.getString("currency"));
            if (json.has("paymentMode"))
                doc.append("paymentMode",json.getString("paymentMode"));
            if (json.has("paymentGateway"))
                doc.append("paymentGateway",json.getString("paymentGateway"));
            if (json.has("paymentAttemptId"))
                doc.append("paymentAttemptId",json.getString("paymentAttemptId"));
            if (json.has("transactionState"))
                doc.append("transactionState",json.getString("transactionState"));

            Document set = new Document("$set", doc);
            transactionCollection.updateOne(query,set);
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
        transactionCollection.deleteOne(query);
      return new JSONObject();
    }
}
