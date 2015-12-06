package com.htw.master.prog.broker.rest;


import com.htw.master.prog.broker.model.Auction;
import com.htw.master.prog.broker.model.Bid;
import com.htw.master.prog.broker.model.Person;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class PersonServiceTest extends ServiceTest {

    public static final String PEOPLE_URL = "people";

    @Test
    public void testHealthCheck() {
        WebTarget webTarget = newWebTarget("ines", "ines");
        Response response = webTarget.path(PEOPLE_URL + "/healthcheck").request().get();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetPersons() {
        WebTarget webTarget = newWebTarget("ines", "ines");

        //TODO: Fragen - setze ich request type zu APPLICATION_JSON, dann bekomme ich immer identity = null
        Response response =
                webTarget.path(PEOPLE_URL).queryParam("alias", "ines").request(MediaType.APPLICATION_XML).get();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<Person> persons = response.readEntity(new GenericType<List<Person>>() {
        });
        Assert.assertEquals("List size should be exact 1", 1, persons.size());
        Long expectedIdentity = 1L;
        Assert.assertEquals(expectedIdentity, persons.get(0).getIdentity());
    }

    @Test
    public void testGetPerson() {
        WebTarget webTarget = newWebTarget("ines", "ines");
        Response response = webTarget.path(PEOPLE_URL + "/2").request(MediaType.APPLICATION_XML).get();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Person person = response.readEntity(Person.class);
        Assert.assertNotNull(person);
        Long expectedIdentity = 2L;
        Assert.assertEquals(expectedIdentity, person.getIdentity());
    }

    @Test
    public void testGetAuctions() {
        WebTarget webTarget = newWebTarget("ines", "ines");
        Response response = webTarget.path(PEOPLE_URL + "/2/auctions").request(MediaType.APPLICATION_XML).get();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<Auction> auctions = response.readEntity(new GenericType<List<Auction>>() {
        });
        Assert.assertEquals("List size should be exact 3", 3, auctions.size());

        Set<Long> resultIdentities = auctions.stream().map(Auction::getIdentity).collect(Collectors.toSet());
        Assert.assertEquals("Set size should be exact 3", 3, resultIdentities.size());
        Assert.assertTrue(resultIdentities.contains(5L));
        Assert.assertTrue(resultIdentities.contains(6L));
        Assert.assertTrue(resultIdentities.contains(7L));
    }

    @Test
    public void testGetBids() {
        WebTarget webTarget = newWebTarget("ines", "ines");
        Response response = webTarget.path(PEOPLE_URL + "/2/bids").request(MediaType.APPLICATION_XML).get();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<Bid> bids = response.readEntity(new GenericType<List<Bid>>() {
        });
        Assert.assertEquals("List size should be exact 1", 1, bids.size());
        Long expectedIdentity = 8L;
        Assert.assertEquals(expectedIdentity, bids.get(0).getIdentity());
    }

    @Test
    public void testLifeCycle() {
        Assert.assertTrue(Boolean.TRUE);
    }

    //TODO PUT tests


    //bei post und put durch id object finden und werte vergleichen
}