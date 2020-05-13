package com.app.server.http;

import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.utils.APPResponse;
import com.app.server.http.utils.PATCH;
import com.app.server.models.Event.Event;
import com.app.server.models.User.EventOrganizer;
import com.app.server.services.EventOrganizerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("organizer")

public class EventOrganizerHttpService {
    private EventOrganizerService organizerService;
    private ObjectWriter ow;

    public EventOrganizerHttpService() {
        organizerService = EventOrganizerService.getInstance();
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
        return new APPResponse(organizerService.getAllEventOrganizers());
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getOne(@PathParam("id") String id) {
        try {
            EventOrganizer d = organizerService.getEventOrganizer(id);
            if (d == null)
                throw new APPNotFoundException(56,"EventOrganizer not found");
            return new APPResponse(d);
        }
        catch(IllegalArgumentException e){
            throw new APPNotFoundException(56,"EventOrganizer not found");
        }
        catch (Exception e) {
            throw new APPInternalServerException(0,"Something went wrong.");
        }
    }

    @GET
    @Path("{id}/events")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getEventsByEventOrganizer(@PathParam("id") String id) {
        try {
            List<Event> d = organizerService.getEventsByOrganizer(id);
            if (d == null)
                throw new APPNotFoundException(56,"Event not found");
            return new APPResponse(d);
        }catch(IllegalArgumentException e){
            throw new APPNotFoundException(56,"Event not found");
        }catch (Exception e) {
            throw new APPInternalServerException(0,"Something went wrong.");
        }
    }

    @PATCH
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse update(@PathParam("id") String id, Object request){
        return new APPResponse(organizerService.update(id,request));
    }

    @DELETE
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete(@PathParam("id") String id) {
        return new APPResponse(organizerService.delete(id));
    }

    @DELETE
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete() {
        return new APPResponse(organizerService.deleteAll());
    }
}
