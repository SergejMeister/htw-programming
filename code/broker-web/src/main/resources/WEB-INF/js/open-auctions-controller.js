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
        //var url = '/services/auctions';
        de.sb.util.AJAX.invoke(url, 'GET', {"Accept": 'application/json'}, null, this.sessionContext, function (request) {
            if (request.status === 200) {
                var auctions = JSON.parse(request.responseText);
                var rowTemplate = self.createRowTemplate();

                for (var auctionIndex = 0; auctionIndex < auctions.length; auctionIndex++) {
                    var row = rowTemplate.cloneNode(true);
                    var auction = auctions[auctionIndex];
                    self.initRow(row, auction);
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

        var buttons = auctionFormTemplate.querySelectorAll('section.auction-form button');
        buttons[0].onclick = function () {
            self.persistAuction(auction);
        };
        buttons[1].onclick = function () {
            var auctionForm = document.querySelector('section.auction-form');
            document.querySelector('main').removeChild(auctionForm);
        };
        document.querySelector('main').appendChild(auctionFormTemplate);
    };

    /**
     * Create table td element.
     */
    de.sb.broker.OpenAuctionsController.prototype.createRowTemplate = function () {
        var self = this;
        var rowTemplate = document.createElement('tr');
        var lastCellIndex = 6;
        for (var i = 0; i < 7; i++) {
            var td = document.createElement('td');
            if (i != lastCellIndex) {
                var output = document.createElement('output');
                td.appendChild(output);
            } else {
                var input = document.createElement('input');
                td.appendChild(input);
            }
            rowTemplate.appendChild(td);
        }

        return rowTemplate;
    };

    /**
     * Create table td element.
     */
    de.sb.broker.OpenAuctionsController.prototype.initRow = function (row, auction) {
        var self = this;
        var outputs = row.querySelectorAll('output');
        var rowData = self.initAuctionRowData(auction);
        outputs[0].value = rowData.sellerName;
        outputs[0].title = rowData.sellerTitle;
        outputs[1].value = rowData.beginn;
        outputs[2].value = rowData.end;
        outputs[3].value = rowData.title;
        outputs[3].title = rowData.titleDescription;
        outputs[4].value = rowData.unitCount;
        outputs[5].value = rowData.minPrice.toFixed(2);

        var input = row.querySelectorAll('input');
        if (auction.seller.identity == self.sessionContext.user.identity) {
            input[0].type = 'button';
            input[0].value = 'edit';
            input[0].disabled = auction.sealed;
            input[0].onclick = function () {
                self.displayAuction(auction);
            };
        } else {
            input[0].type = 'number';
            input[0].value = (auction.ownerBidPrice / 100).toFixed(2);
            input[0].step = 0.01;
            input[0].onchange = function () {
                self.updateBid(auction.identity, Math.round(this.value * 100));
            };
        }

        document.querySelector('section.open-auctions tbody').appendChild(row);
    };

    /**
     * Persist auction.
     */
    de.sb.broker.OpenAuctionsController.prototype.persistAuction = function (auction) {
        var auctionForm = document.querySelector('section.auction-form');
        var activeElements = auctionForm.querySelectorAll('input, textarea');

        if (!auction) {
            auction = {};
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
                self.display();
            }else{
                self.displayStatus(response.status, response.statusText)
            }
        });
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
        auctionRowData.id = auction.identity;
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
