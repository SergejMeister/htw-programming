/**
 * Created by sergej on 12/17/15.
 */
"use strict";

this.de = this.de || {};
this.de.sb = this.de.sb || {};
this.de.sb.broker = this.de.sb.broker || {};
(function () {
    var SUPER = de.sb.broker.Controller;

    /**
     * Creates a new preferences controller that is derived from an abstract controller.
     * @param sessionContext {de.sb.broker.SessionContext} a session context
     */
    de.sb.broker.ClosedAuctionsController = function (sessionContext) {
        SUPER.call(this, 2, sessionContext);
    };
    de.sb.broker.ClosedAuctionsController.prototype = Object.create(SUPER.prototype);
    de.sb.broker.ClosedAuctionsController.prototype.constructor = de.sb.broker.ClosedAuctionsController;

    /**
     * Displays the associated view.
     */
    de.sb.broker.ClosedAuctionsController.prototype.display = function () {
        if (!this.sessionContext.user) return;
        SUPER.prototype.display.call(this);
        this.displayStatus(200, "OK");

        var sectionSellerAuctionElement = document.querySelector("#closed-seller-auctions-template").content.cloneNode(true).firstElementChild;
        this.displaySellerAuctions();
        document.querySelector("main").appendChild(sectionSellerAuctionElement);

        var sectionBidderAuctionElement = document.querySelector("#closed-bidder-auctions-template").content.cloneNode(true).firstElementChild;
        this.displayBidderAuctions();
        document.querySelector("main").appendChild(sectionBidderAuctionElement);
    };

    /**
     * Returns seller auctions.
     */
    de.sb.broker.ClosedAuctionsController.prototype.displaySellerAuctions = function () {

        var params = {
            sellerReference: 1,
            closed: true
        };

        var self = this;
        de.sb.util.AJAX.invoke("/services/auctions", "GET", {"Accept": "application/json"}, null, params, function (request) {
            self.displayStatus(request.status, request.statusText);
            if (request.status === 200) {
                var auctions = JSON.parse(request.responseText);
                for (var auctionIndex = 0; auctionIndex < auctions.length; ++auctionIndex) {
                    var auction = auctions[auctionIndex];

                    var row = document.createElement("tr");

                    row.appendChild(SUPER.prototype.createCell("SergejTest"));
                    row.appendChild(SUPER.prototype.createCell("begin"));
                    row.appendChild(SUPER.prototype.createCell("end"));
                    row.appendChild(SUPER.prototype.createCell(auction.title));
                    row.appendChild(SUPER.prototype.createCell(auction.unitCount));
                    row.appendChild(SUPER.prototype.createCell("1"));
                    row.appendChild(SUPER.prototype.createCell("2"));

                    document.querySelector("section.closed-seller-auctions tbody").appendChild(row);
                }
            }
        });
    };


    /**
     * Display bidder auctions.
     */
    de.sb.broker.ClosedAuctionsController.prototype.displayBidderAuctions = function () {

        var params = {
            sellerReference: 1,
            closed: true
        };

        var self = this;
        de.sb.util.AJAX.invoke("/services/auctions", "GET", {"Accept": "application/json"}, null, params, function (request) {
            self.displayStatus(request.status, request.statusText);
            if (request.status === 200) {
                var auctions = JSON.parse(request.responseText);
                for (var auctionIndex = 0; auctionIndex < auctions.length; ++auctionIndex) {
                    var auction = auctions[auctionIndex];

                    var row = document.createElement("tr");

                    row.appendChild(SUPER.prototype.createCell("Seller"));
                    row.appendChild(SUPER.prototype.createCell("SergejTest"));
                    row.appendChild(SUPER.prototype.createCell("begin"));
                    row.appendChild(SUPER.prototype.createCell("end"));
                    row.appendChild(SUPER.prototype.createCell(auction.title));
                    row.appendChild(SUPER.prototype.createCell(auction.unitCount));
                    row.appendChild(SUPER.prototype.createCell("1"));
                    row.appendChild(SUPER.prototype.createCell("2"));

                    document.querySelector("section.closed-bidder-auctions tbody").appendChild(row);
                }
            }
        });
    };
}());
