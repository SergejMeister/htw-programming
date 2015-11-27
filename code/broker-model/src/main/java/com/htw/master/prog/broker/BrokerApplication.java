//package com.htw.master.prog.broker;
//
//import com.htw.master.prog.broker.rest.AuctionService;
//import com.htw.master.prog.broker.rest.PersonService;
//import io.dropwizard.Application;
//import io.dropwizard.setup.Bootstrap;
//import io.dropwizard.setup.Environment;
//
///**
// * Broker Application Start class.
// */
//public class BrokerApplication extends Application<BrokerConfiguration> {
//
//    public static void main(String[] args) throws Exception {
//        new BrokerApplication().run(args);
//    }
//
//    @Override
//    public String getName() {
//        return "services";
//    }
//
//    @Override
//    public void initialize(Bootstrap<BrokerConfiguration> bootstrap) {
//        // nothing to do yet
//    }
//
//    @Override
//    public void run(BrokerConfiguration brokerConfiguration, Environment environment) throws Exception {
//        final PersonService personService = new PersonService();
//        environment.jersey().register(personService);
//        final AuctionService auctionService = new AuctionService();
//        environment.jersey().register(auctionService);
//    }
//}
