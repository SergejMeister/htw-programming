package com.htw.master.prog.broker.rest;


import com.htw.master.prog.broker.model.Person;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.RollbackException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Path("people")
public class PersonService {

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPersons() {
        return Response.ok().build();
    }


    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response savePerson(@NotNull @Valid final Person template) {
        boolean persistMode = template.getIdentity() == null;
        final EntityManager em = LifeCycleProvider.brokerManager();
        Long identity = null;
        if (persistMode) {
            Person personToPersist;
        } else {
            em.getTransaction().begin();

            //update
        }

        return Response.ok().entity(identity).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("{identity}")
    public Response getPerson(@PathParam("identity") final int identity) {
        final EntityManager em = LifeCycleProvider.brokerManager();
        Person person = null;
        em.getEntityManagerFactory().getCache().evict(Person.class, identity);
        try {
            person = em.getReference(Person.class, identity);
            try {
                em.getTransaction().commit();
            } finally {
                em.getTransaction().begin();
            }
        } catch (final EntityNotFoundException exception) {
            throw new ClientErrorException(NOT_FOUND);
        } catch (final RollbackException exception) {
            throw new ClientErrorException(CONFLICT);
        } finally {
            Entity<Person> entity = Entity.entity(person, MediaType.APPLICATION_JSON);
            return Response.ok().entity(entity).build();
        }
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
