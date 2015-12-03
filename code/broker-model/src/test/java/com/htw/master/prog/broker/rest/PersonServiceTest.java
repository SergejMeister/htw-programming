package com.htw.master.prog.broker.rest;

import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;


public class PersonServiceTest extends ServiceTest {

    public static final String PEOPLE_URL = "people";


    @Test
    @Ignore(value = "not implemented")
    public void testGetPersons() {
        WebTarget webTarget = newWebTarget("ines", "ines");
        Response response = webTarget.path(PEOPLE_URL).queryParam("alias", "ines").request().get();
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
    }

    @Test
    @Ignore(value = "not implemented")
    public void testGetPerson() {
        WebTarget webTarget = newWebTarget("ines", "ines");
        Response response = webTarget.path(PEOPLE_URL + "/2").request().get();
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
    }

    @Test
    @Ignore(value = "not implemented")
    public void testGetAuctions() {
        WebTarget webTarget = newWebTarget("ines", "ines");
        Response response = webTarget.path(PEOPLE_URL + "/2/auctions").request().get();
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
    }

    @Test
    @Ignore(value = "not implemented")
    public void testGetBids() {
        WebTarget webTarget = newWebTarget("ines", "ines");
        Response response = webTarget.path(PEOPLE_URL + "/2/bids").request().get();
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
    }

    @Test
    @Ignore(value = "not implemented")
    public void testLifeCycle() {
        Assert.assertTrue(Boolean.TRUE);
    }
}