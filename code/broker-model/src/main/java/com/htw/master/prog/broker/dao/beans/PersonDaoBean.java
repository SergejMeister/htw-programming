package com.htw.master.prog.broker.dao.beans;

import com.htw.master.prog.broker.dao.PersonDao;
import com.htw.master.prog.broker.model.Person;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

@Stateless
public class PersonDaoBean extends GenericDaoBean<Person> implements PersonDao {

    public PersonDaoBean() {
        super(Person.class);
    }

    public PersonDaoBean(EntityManager entityManager) {
        super(entityManager, Person.class);
    }
}
