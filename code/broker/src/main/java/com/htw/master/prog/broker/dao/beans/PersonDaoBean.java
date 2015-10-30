package com.htw.master.prog.broker.dao.beans;

import com.htw.master.prog.broker.dao.PersonDao;
import com.htw.master.prog.broker.model.Person;

import javax.ejb.Stateless;

@Stateless
public class PersonDaoBean extends GenericDaoBean<Person> implements PersonDao {

    public PersonDaoBean() {
        super(Person.class);
    }
}
