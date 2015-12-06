package com.htw.master.prog.broker.rest;

import com.htw.master.prog.broker.model.Auction;
import com.htw.master.prog.broker.model.Bid;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;


public class AuctionServiceTest extends ServiceTest {

    private static final String AUCTION_URL = "auctions";

    @Test
    public void testHealthCheck() {
        WebTarget webTarget = newWebTarget("ines", "ines");
        Response response = webTarget.path(AUCTION_URL + "/healthcheck").request().get();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetAuctions() {
        WebTarget webTarget = newWebTarget("ines", "ines");
        Response response = webTarget.path(AUCTION_URL).queryParam("titleFragment", "Rennrad wie neu").request().get();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<Auction> auctions = response.readEntity(new GenericType<List<Auction>>() {
        });
        Assert.assertEquals("List size should be exact 1", 1, auctions.size());
        Long expectedIdentity = 3L;
        Assert.assertEquals(expectedIdentity, auctions.get(0).getIdentity());
    }

    @Test
    public void testGetAuction() {
        WebTarget webTarget = newWebTarget("ines", "ines");
        Response response = webTarget.path(AUCTION_URL + "/7").request().get();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Auction auction = response.readEntity(Auction.class);
        Assert.assertNotNull(auction);
        Long expectedIdentity = 7L;
        Assert.assertEquals(expectedIdentity, auction.getIdentity());
    }


    @Test
    public void testGetBidOfRequester() {
        WebTarget webTarget = newWebTarget("ines", "ines");
        Response response = webTarget.path(AUCTION_URL + "/6/bid").request().get();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Bid bid = response.readEntity(Bid.class);
        Assert.assertNotNull(bid);
        Long expectedIdentity = 9L;
        Assert.assertEquals(expectedIdentity, bid.getIdentity());
    }

    @Test
    public void testGetBidOfRequesterNotFound() {
        WebTarget webTarget = newWebTarget("ines", "ines");
        Response response = webTarget.path(AUCTION_URL + "/7/bid").request().get();
        Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }
}