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

        var self = this;
        var sectionSellerAuctionElement = document.querySelector('#open-auctions-template').content.cloneNode(true).firstElementChild;
        this.displayOpenAuctions();
        document.querySelector('main').appendChild(sectionSellerAuctionElement);

        var newButton = document.querySelector('button');
        newButton.onclick = function () {
            self.displayAuction();
        };
    };

    /**
     * Returns seller auctions.
     */
    de.sb.broker.OpenAuctionsController.prototype.displayOpenAuctions = function () {
        var self = this;
        var url = '/services/auctions?closed=false';
        de.sb.util.AJAX.invoke(url, 'GET', {"Accept": 'application/json'}, null, this.sessionContext, function (request) {
            if (request.status === 200) {
                var auctions = JSON.parse(request.responseText);

                //TODO ask how it works with outputs!!!
                //var rowTemplate = document.createElement('tr');
                //for (var i = 0; i < 7; i++) {
                //    var td = document.createElement('td');
                //    var output = document.createElement('output');
                //    td.appendChild(output);
                //    rowTemplate.appendChild(td);
                //}
                //
                //for (var auctionIndex = 0; auctionIndex < auctions.length; auctionIndex++) {
                //    var outputs = rowTemplate.querySelectorAll('output');
                //    var auction = auctions[auctionIndex];
                //    var rowData = self.initAuctionRowData(auction);
                //    outputs[0].value = rowData.sellerName;
                //    outputs[0].title = rowData.sellerTitle;
                //    outputs[1].value = rowData.beginn;
                //    outputs[2].value = rowData.end;
                //    outputs[3].value = rowData.title;
                //    outputs[3].title = rowData.titleDescription;
                //    outputs[4].value = rowData.unitCount;
                //    outputs[5].value = rowData.minPrice.toFixed(2);
                //    outputs[6].value = '';
                //
                //    document.querySelector('section.open-auctions tbody').appendChild(rowTemplate);
                //}

                for (var auctionIndex = 0; auctionIndex < auctions.length; ++auctionIndex) {
                    var auction = auctions[auctionIndex];
                    var rowData = self.initAuctionRowData(auction);

                    var row = document.createElement('tr');
                    row.appendChild(SUPER.prototype.createValueTitleCell(rowData.sellerName, rowData.sellerTitle));
                    row.appendChild(SUPER.prototype.createCell(rowData.beginn));
                    row.appendChild(SUPER.prototype.createCell(rowData.end));
                    row.appendChild(SUPER.prototype.createValueTitleCell(rowData.title, rowData.titleDescription));
                    row.appendChild(SUPER.prototype.createCell(rowData.unitCount));
                    row.appendChild(SUPER.prototype.createCell(rowData.minPrice.toFixed(2)));
                    row.appendChild(self.createBidCell(auction));

                    document.querySelector('section.open-auctions tbody').appendChild(row);
                }
            }

            self.displayStatus(request.status, request.statusText)
        });
    };

    /**
     * Create table td element.
     */
    de.sb.broker.OpenAuctionsController.prototype.displayAuction = function (auction) {
        var self = this;
        var auctionFormTemplate = document.querySelector('#auction-form-template').content.cloneNode(true).firstElementChild;
        var auctionForm = document.querySelector('section.auction-form');
        if (auctionForm) {
            document.querySelector('main').removeChild(auctionForm);
        }

        var activeElements = auctionFormTemplate.querySelectorAll('section.auction-form input, textarea');
        if (auction) {
            activeElements[0].value = new de.sb.util.Date().toGermanString(auction.creationTimestamp);
            activeElements[1].value = new de.sb.util.Date().toGermanString(auction.closureTimestamp);
            activeElements[2].value = auction.title;
            activeElements[3].value = auction.description;
            activeElements[4].value = auction.unitCount;
            activeElements[5].value = (auction.askingPrice / 100).toFixed(2);
        } else {
            var now = new Date(Date.now());
            activeElements[0].value = new de.sb.util.Date().toGermanString(now.getTime());
            now.setDate(now.getDate() + 30);
            activeElements[1].value = new de.sb.util.Date().toGermanString(now.getTime());
        }

        var sendButton = auctionFormTemplate.querySelector('section.auction-form button');
        sendButton.onclick = function () {
            self.persistAuction(auction);
        };
        document.querySelector('main').appendChild(auctionFormTemplate);
    };

    /**
     * Persist auction.
     */
    de.sb.broker.OpenAuctionsController.prototype.persistAuction = function (auction) {
        var auctionForm = document.querySelector('section.auction-form');
        var activeElements = auctionForm.querySelectorAll('input,textarea');

        var persistMode = false;
        if (!auction) {
            auction = {};
            persistMode = true;
        }
        auction.creationTimestamp = new de.sb.util.Date().germanStringToTimestamp(activeElements[0].value.trim());
        auction.closureTimestamp = new de.sb.util.Date().germanStringToTimestamp(activeElements[1].value.trim());
        auction.title = activeElements[2].value.trim();
        auction.description = activeElements[3].value.trim();
        auction.unitCount = activeElements[4].value.trim();
        auction.askingPrice = Math.round(activeElements[5].value * 100);

        var self = this;
        var body = JSON.stringify(auction);
        de.sb.util.AJAX.invoke('/services/auctions', 'PUT', {"Content-Type": 'application/json'}, body, this.sessionContext, function (response) {
            if (response.status === 200) {
                if (persistMode) {
                    auction.seller = self.sessionContext.user;
                    var rowData = self.initAuctionRowData(auction);

                    var row = document.createElement('tr');
                    row.appendChild(SUPER.prototype.createValueTitleCell(rowData.sellerName, rowData.sellerTitle));
                    row.appendChild(SUPER.prototype.createCell(rowData.beginn));
                    row.appendChild(SUPER.prototype.createCell(rowData.end));
                    row.appendChild(SUPER.prototype.createValueTitleCell(rowData.title, rowData.titleDescription));
                    row.appendChild(SUPER.prototype.createCell(rowData.unitCount));
                    row.appendChild(SUPER.prototype.createCell(rowData.minPrice.toFixed(2)));
                    row.appendChild(self.createBidCell(auction));

                    document.querySelector('section.open-auctions tbody').appendChild(row);
                } else {
                    //TODO what is a good solution to replace row with new data!!!
                }
            }
            document.querySelector('main').removeChild(auctionForm);
            self.displayStatus(response.status, response.statusText);
        });
    };

    /**
     * Create table td element.
     */
    de.sb.broker.OpenAuctionsController.prototype.createBidCell = function (auction) {
        var self = this;
        var cellBid = document.createElement('td');
        var input = document.createElement('input');
        if (auction.seller.identity == this.sessionContext.user.identity) {
            input.type = 'button';
            input.value = 'edit';
            input.disabled = auction.sealed;
            input.onclick = function () {
                self.displayAuction(auction);
            };
        } else {
            input.type = 'number';
            input.value = (auction.ownerBidPrice / 100).toFixed(2);
            input.step = 0.01;
            input.onchange = function () {
                self.updateBid(auction.identity, Math.round(this.value * 100));
            };
        }
        cellBid.appendChild(input);
        return cellBid;
    };

    /**
     * Persist auction.
     */
    de.sb.broker.OpenAuctionsController.prototype.updateBid = function (auctionIdentity, price) {
        var self = this;
        var endpoint = '/services/auctions/' + auctionIdentity + '/bid';
        de.sb.util.AJAX.invoke(endpoint, 'POST', null, '' + price, this.sessionContext, function (response) {
            self.displayStatus(response.status, response.statusText);
        });
    };

    de.sb.broker.OpenAuctionsController.prototype.initAuctionRowData = function (auction) {

        var auctionRowData = {};
        auctionRowData.sellerName = auction.seller.name.given;
        auctionRowData.sellerTitle = auction.seller.name.given + ' ' + auction.seller.name.family + ' (' + auction.seller.contact.email + ')';
        //TODO: Maybe exists a better solution to handle date and currency format!!!
        auctionRowData.beginn = new de.sb.util.Date().toGermanString(auction.creationTimestamp);
        auctionRowData.end = new de.sb.util.Date().toGermanString(auction.closureTimestamp);
        auctionRowData.title = auction.title;
        auctionRowData.titleDescription = auction.description;
        auctionRowData.unitCount = auction.unitCount;
        auctionRowData.minPrice = auction.askingPrice / 100;

        return auctionRowData;
    };

}());
