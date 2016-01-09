'use strict';

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

        var self = this;
        var semaphore = new de.sb.util.Semaphore(1 - 2);
        var statusAccumulator = new de.sb.util.StatusAccumulator();

        var sectionSellerAuctionElement = document.querySelector('#closed-seller-auctions-template').content.cloneNode(true).firstElementChild;
        this.displaySellerAuctions(semaphore, statusAccumulator);
        document.querySelector('main').appendChild(sectionSellerAuctionElement);

        var sectionBidderAuctionElement = document.querySelector('#closed-bidder-auctions-template').content.cloneNode(true).firstElementChild;
        this.displayBidderAuctions(semaphore, statusAccumulator);
        document.querySelector('main').appendChild(sectionBidderAuctionElement);
        semaphore.acquire(function () {
            self.displayStatus(statusAccumulator.status, statusAccumulator.statusText);
        })
    };

    /**
     * Returns seller auctions.
     */
    de.sb.broker.ClosedAuctionsController.prototype.displaySellerAuctions = function (semaphore, statusAccumulator) {

        var self = this;
        var url = '/services/people/' + this.sessionContext.user.identity + '/auctions?seller=true&closed=true';
        de.sb.util.AJAX.invoke(url, 'GET', {"Accept": 'application/json'}, null, this.sessionContext, function (request) {
            if (request.status === 200) {
                var auctions = JSON.parse(request.responseText);
                for (var auctionIndex = 0; auctionIndex < auctions.length; ++auctionIndex) {
                    var auction = auctions[auctionIndex];
                    var rowData = self.initAuctionRowData(auction);

                    var winnerBid = self.getWinnerBid(auction.bids);
                    if (winnerBid) {
                        rowData.winName = winnerBid.bidder.name.given;
                        rowData.winTitle = winnerBid.bidder.name.given + ' ' + winnerBid.bidder.name.family + ' (' + winnerBid.bidder.contact.email + ')';
                        var winPrice = winnerBid.price / 100;
                        rowData.winPrice = winPrice.toFixed(2);
                    }

                    var row = document.createElement('tr');
                    row.appendChild(SUPER.prototype.createValueTitleCell(rowData.winName, rowData.winTitle));
                    row.appendChild(SUPER.prototype.createCell(rowData.beginn));
                    row.appendChild(SUPER.prototype.createCell(rowData.end));
                    row.appendChild(SUPER.prototype.createCell(rowData.title));
                    row.appendChild(SUPER.prototype.createCell(rowData.unitCount));
                    row.appendChild(SUPER.prototype.createCell(rowData.minPrice.toFixed(2)));
                    row.appendChild(SUPER.prototype.createCell(rowData.winPrice));

                    document.querySelector('section.closed-seller-auctions tbody').appendChild(row);
                }
            }
            statusAccumulator.offer(request.status, request.statusText);
            semaphore.release();
        });
    };


    /**
     * Display bidder auctions.
     */
    de.sb.broker.ClosedAuctionsController.prototype.displayBidderAuctions = function (semaphore, statusAccumulator) {

        var url = '/services/people/' + this.sessionContext.user.identity + '/auctions?seller=false&closed=true';
        var self = this;
        de.sb.util.AJAX.invoke(url, 'GET', {"Accept": 'application/json'}, null, this.sessionContext, function (request) {
            if (request.status === 200) {
                var auctions = JSON.parse(request.responseText);
                for (var auctionIndex = 0; auctionIndex < auctions.length; ++auctionIndex) {
                    var auction = auctions[auctionIndex];
                    var rowData = self.initAuctionRowData(auction);

                    var winnerBid = self.getWinnerBid(auction.bids);
                    if (winnerBid) {
                        rowData.winName = winnerBid.bidder.name.given;
                        var winPrice = winnerBid.price / 100;
                        rowData.winPrice = winPrice.toFixed(2);
                        rowData.winTitle = winnerBid.bidder.name.given + ' ' + winnerBid.bidder.name.family + ' (' + winnerBid.bidder.contact.email + ')';
                    }

                    var ownerBid = self.getOwnerBid(auction.bids, self.sessionContext.user.alias);
                    rowData.bidPrice = ownerBid.price / 100;

                    var row = document.createElement('tr');
                    row.appendChild(SUPER.prototype.createValueTitleCell(rowData.sellerName, rowData.sellerTitle));
                    row.appendChild(SUPER.prototype.createValueTitleCell(rowData.winName, rowData.winTitle));
                    row.appendChild(SUPER.prototype.createCell(rowData.beginn));
                    row.appendChild(SUPER.prototype.createCell(rowData.end));
                    row.appendChild(SUPER.prototype.createCell(rowData.title));
                    row.appendChild(SUPER.prototype.createCell(rowData.unitCount));
                    row.appendChild(SUPER.prototype.createCell(rowData.minPrice.toFixed(2)));
                    row.appendChild(SUPER.prototype.createCell(rowData.bidPrice.toFixed(2)));
                    row.appendChild(SUPER.prototype.createCell(rowData.winPrice));

                    document.querySelector('section.closed-bidder-auctions tbody').appendChild(row);
                }
            }

            statusAccumulator.offer(request.status, request.statusText);
            semaphore.release();
        });
    };

    de.sb.broker.ClosedAuctionsController.prototype.getOwnerBid = function (bids, alias) {
        if (bids) {
            for (var i = 0; i < bids.length; ++i) {
                var bid = bids[i];
                if (bid.bidder.alias === alias) {
                    return bid;
                }
            }
        }
    };

    de.sb.broker.ClosedAuctionsController.prototype.getWinnerBid = function (bids) {
        if (bids && bids.length > 0) {
            var maxBid = bids[bids.length - 1];
            for (var i = 0; i < bids.length - 1; ++i) {
                var bid = bids[i];
                if (bid.price > maxBid.price) {
                    maxBid = bid;
                }
            }
            return maxBid;
        }
    };

    de.sb.broker.ClosedAuctionsController.prototype.initAuctionRowData = function (auction) {

        var auctionRowData = {};
        auctionRowData.sellerName = auction.seller.name.given;
        auctionRowData.sellerTitle = auction.seller.name.given + ' ' + auction.seller.name.family + ' (' + auction.seller.contact.email + ')';
        auctionRowData.winName = '';
        auctionRowData.winTitle = '';
        auctionRowData.winPrice = '';
        auctionRowData.beginn = new de.sb.util.Date().toGermanString(auction.creationTimestamp);
        auctionRowData.end = new de.sb.util.Date().toGermanString(auction.closureTimestamp);
        auctionRowData.title = auction.title;
        auctionRowData.unitCount = auction.unitCount;
        auctionRowData.minPrice = auction.askingPrice / 100;
        auctionRowData.bidPrice = '';

        return auctionRowData;
    };

}());
