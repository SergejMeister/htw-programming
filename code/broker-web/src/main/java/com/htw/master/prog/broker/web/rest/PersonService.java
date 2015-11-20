package com.htw.master.prog.broker.web.rest;


import com.htw.master.prog.broker.model.Auction;
import com.htw.master.prog.broker.model.Person;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

@Path("people")
public class PersonService {

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Person> getPersons() {
        return Collections.emptyList();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Long createPerson(final Person person) {
        return -1L;
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Long updatePerson(final Person person) {
        return -1L;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("{identity}")
    public Person getPerson(@QueryParam("identity") final int identity) {
        return new Person();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("{identity}/auctions")
    public List<Auction> getAuctions(@QueryParam("identity") final int identity) {
        return Collections.emptyList();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("{identity}/bids")
    public List<Auction> getBids(@QueryParam("identity") final int identity) {
        return Collections.emptyList();
    }
}
