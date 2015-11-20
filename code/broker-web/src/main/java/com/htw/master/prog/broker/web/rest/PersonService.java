package com.htw.master.prog.broker.web.rest;


import com.htw.master.prog.broker.model.Person;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("people")
public class PersonService {

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPersons() {
        return Response.ok().build();
    }


    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updatePerson(final Person person) {
        return Response.ok().build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("{identity}")
    public Response getPerson(@PathParam("identity") final int identity) {
        return Response.ok().build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("{identity}/auctions")
    public Response getAuctions(@PathParam("identity") final int identity) {
        return Response.ok().build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("{identity}/bids")
    public Response getBids(@PathParam("identity") final int identity) {
        return Response.ok().build();
    }
}
