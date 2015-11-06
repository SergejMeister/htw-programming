package com.htw.master.prog.broker.model;

public final class EntityTestUtility {

    private EntityTestUtility() {
    }

    public static Person createDefaultPerson() {
        Person person = new Person();
        person.setAlias("tester");
        person.getName().setFamily("testFamily");
        person.getName().setGiven("test");
        person.getAddress().setCity("Berlin");
        person.getContact().setEmail("test@mail.de");
        return person;
    }

    public static Auction createDefaultAction() {
        Person defaultPerson = createDefaultPerson();
        return createDefaultAction(defaultPerson);
    }

    public static Auction createDefaultAction(Person person) {
        Auction auction = new Auction(person);
        auction.setTitle("testActionTitle");
        auction.setDescription("testActionDescription");
        return auction;
    }
}
