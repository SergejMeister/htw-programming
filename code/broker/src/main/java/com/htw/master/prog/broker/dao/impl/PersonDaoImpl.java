package com.htw.master.prog.broker.dao.impl;

import com.htw.master.prog.broker.dao.PersonDao;
import com.htw.master.prog.broker.model.Person;

public class PersonDaoImpl extends GenericDaoImpl<Person> implements PersonDao {

    public PersonDaoImpl() {
        super(Person.class);
    }
}
