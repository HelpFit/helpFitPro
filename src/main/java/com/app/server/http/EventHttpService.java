package com.app.server.http;

import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.utils.APPResponse;
import com.app.server.http.utils.PATCH;
import com.app.server.models.Event.Event;
import com.app.server.services.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("event")

public class EventHttpService {
    private EventService service;
    private ObjectWriter ow;


    public EventHttpService() {
        service = EventService.getInstance();
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    @OPTIONS
    @PermitAll
    public Response optionsById() {
        return Response.ok().build();
    }


    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll() {
        return new APPResponse(service.getAllEvents());
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getOne(@PathParam("id") String id) {
        try {
            Event d = service.getEvent(id);
            if (d == null)
                throw new APPNotFoundException(56,"Event not found");
            return new APPResponse(d);
        }
        catch(IllegalArgumentException e){
            throw new APPNotFoundException(56,"Event not found");
        }
        catch (Exception e) {
            throw new APPInternalServerException(0,"Something went wrong.");
        }

    }

    @GET
    @Path("/eventby")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getEventsByType(@Context UriInfo info) {
        return new APPResponse(service.getEventsByType(info));
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse create(Object request) {
            return new APPResponse(service.create(request));
    }

    @PATCH
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse update(@PathParam("id") String id, Object request){
        return new APPResponse(service.update(id,request));
    }

    @DELETE
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete(@PathParam("id") String id) {
        return new APPResponse(service.delete(id));
    }

    @DELETE
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete() {
        return new APPResponse(service.deleteAll());
    }
}
