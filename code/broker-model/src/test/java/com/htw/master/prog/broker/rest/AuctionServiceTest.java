package com.htw.master.prog.broker.rest;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;


public class AuctionServiceTest extends ServiceTest {

    private static final String AUCTION_URL = "auctions";

    @Test
    @Ignore(value = "not implemented")
    public void testGetAuctions() {
        WebTarget webTarget = newWebTarget("ines", "ines");
        Response response = webTarget.path(AUCTION_URL).queryParam("titleFragment", "Rennrad wie neu").request().get();
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
    }

    @Test
    @Ignore(value = "not implemented")
    public void testGetAuction() {
        WebTarget webTarget = newWebTarget("ines", "ines");
        Response response = webTarget.path(AUCTION_URL + "/7").request().get();
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
    }

    @Test
    @Ignore(value = "not implemented")
    public void testGetBidOfRequester() {
        WebTarget webTarget = newWebTarget("ines", "ines");
        Response response = webTarget.path(AUCTION_URL + "/7/bid").request().get();
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
    }
}