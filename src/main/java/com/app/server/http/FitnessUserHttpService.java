package com.app.server.http;

import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.utils.APPResponse;
import com.app.server.http.utils.PATCH;
import com.app.server.models.User.FitnessUser;
import com.app.server.services.FitnessUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("fitnessUser")

public class FitnessUserHttpService {
    private FitnessUserService fitnessUserService;
    private ObjectWriter ow;

    public FitnessUserHttpService() {
        fitnessUserService = FitnessUserService.getInstance();
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @OPTIONS
    @PermitAll
    public Response optionsById() {
        return Response.ok().build();
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getOne(@PathParam("id") String id) {
        try {
            FitnessUser d = fitnessUserService.getFitnessUser(id);
            if (d == null)
                throw new APPNotFoundException(56,"FitnessUser not found");
            return new APPResponse(d);
        }
        catch(IllegalArgumentException e){
            throw new APPNotFoundException(56,"FitnessUser not found");
        }
        catch (Exception e) {
            throw new APPInternalServerException(0,"Something went wrong.");
        }
    }

    @PATCH
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse update(@PathParam("id") String id, Object request){

        return new APPResponse(fitnessUserService.update(id,request));

    }

    @DELETE
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete(@PathParam("id") String id) {

        return new APPResponse(fitnessUserService.delete(id));
    }

    @DELETE
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete() {

        return new APPResponse(fitnessUserService.deleteAll());
    }

    @POST
    @Path("{id}/event/{id2}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse registerUserForEvent(@Context HttpHeaders headers, @PathParam("id") String id, @PathParam("id2") String id2, Object request) {
        return new APPResponse(fitnessUserService.registerUserForEvent(headers, id, id2, request));
    }

    @POST
    @Path("{id}/event/{id2}/payment")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse userPayForEvent(@Context HttpHeaders headers, @PathParam("id") String id, @PathParam("id2") String id2, Object request) {
        return new APPResponse(fitnessUserService.userPayForEvent(headers, id, id2, request));
    }

    @GET
    @Path("{id}/transactions")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getUserTransactions(@Context HttpHeaders headers,@PathParam("id") String id) {
        return new APPResponse(fitnessUserService.getUserTransactions(headers, id));
    }

    @GET
    @Path("{id}/evaluateProgram")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse evaluateUserProgram(@Context HttpHeaders headers,@PathParam("id") String id) {
        return new APPResponse(fitnessUserService.getEvaluateAndUserProgram(headers, id));
    }


    @GET
    @Path("{id}/regimeProgram")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getUserRegimeProgram(@Context HttpHeaders headers,@PathParam("id") String id) {
        return new APPResponse(fitnessUserService.getUserRegimeProgram(headers, id));
    }

    @POST
    @Path("{id}/regimeProgram")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse createUserRegimeProgram(@Context HttpHeaders headers, @PathParam("id") String id, Object request) {
        return new APPResponse(fitnessUserService.createUserRegimeProgram(headers, id, request));
    }

    @DELETE
    @Path("{id}/regimeProgram")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete(@Context HttpHeaders headers, @PathParam("id") String id) {
        return new APPResponse(fitnessUserService.deleteUserRegimeProgram(headers, id));
    }
}