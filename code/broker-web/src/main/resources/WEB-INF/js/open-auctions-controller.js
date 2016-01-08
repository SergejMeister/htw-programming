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
    de.sb.broker.OpenAuctionsController = function (sessionContext) {
        SUPER.call(this, 1, sessionContext);
    };
    de.sb.broker.OpenAuctionsController.prototype = Object.create(SUPER.prototype);
    de.sb.broker.OpenAuctionsController.prototype.constructor = de.sb.broker.OpenAuctionsController;

    /**
     * Displays the associated view.
     */
    de.sb.broker.OpenAuctionsController.prototype.display = function () {
        if (!this.sessionContext.user) return;
        SUPER.prototype.display.call(this);

        var sectionSellerAuctionElement = document.querySelector('#open-auctions-template').content.cloneNode(true).firstElementChild;
        this.displayOpenAuctions();
        document.querySelector('main').appendChild(sectionSellerAuctionElement);
    };

    /**
     * Returns seller auctions.
     */
    de.sb.broker.OpenAuctionsController.prototype.displayOpenAuctions = function () {

        var self = this;
        var url = '/services/auctions';
        de.sb.util.AJAX.invoke(url, 'GET', {"Accept": 'application/json'}, null, this.sessionContext, function (request) {
            if (request.status === 200) {
                var auctions = JSON.parse(request.responseText);
                for (var auctionIndex = 0; auctionIndex < auctions.length; ++auctionIndex) {
                    var auction = auctions[auctionIndex];
                    var rowData = self.initAuctionRowData(auction);

                    var row = document.createElement('tr');
                    row.appendChild(SUPER.prototype.createCell(rowData.sellerName));
                    row.appendChild(SUPER.prototype.createCell(rowData.beginn));
                    row.appendChild(SUPER.prototype.createCell(rowData.end));
                    row.appendChild(SUPER.prototype.createCell(rowData.title));
                    row.appendChild(SUPER.prototype.createCell(rowData.unitCount));
                    row.appendChild(SUPER.prototype.createCell(rowData.minPrice.toFixed(2)));
                    row.appendChild(self.createBidCell(auction));

                    document.querySelector('section.open-auctions tbody').appendChild(row);
                }
            }
        });
    };

    /**
     * Create table td element.
     */
    de.sb.broker.OpenAuctionsController.prototype.createBidCell = function (auction) {
        var cellWinner = document.createElement('td');
        var input = document.createElement('input');
        if (auction.seller.identity == this.sessionContext.user.identity) {
            input.type = 'button';
            input.value = 'edit';
            input.disabled = auction.sealed;
        } else {
            input.type = 'number';
        }
        cellWinner.appendChild(input);
        return cellWinner;
    };

    de.sb.broker.OpenAuctionsController.prototype.initAuctionRowData = function (auction) {

        var auctionRowData = {};
        auctionRowData.sellerName = auction.seller.name.given;
        auctionRowData.beginn = new de.sb.util.Date().toGermanString(auction.creationTimestamp);
        auctionRowData.end = new de.sb.util.Date().toGermanString(auction.closureTimestamp);
        auctionRowData.title = auction.title;
        auctionRowData.unitCount = auction.unitCount;
        auctionRowData.minPrice = auction.askingPrice / 100;

        return auctionRowData;
    };

}());
