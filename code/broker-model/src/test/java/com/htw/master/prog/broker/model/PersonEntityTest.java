package com.htw.master.prog.broker.model;

import com.htw.master.prog.broker.dao.PersonDao;
import com.htw.master.prog.broker.dao.beans.PersonDaoBean;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import java.util.Set;

@Ignore
public class PersonEntityTest extends EntityTest {

    public static ConstraintViolation<Person> findByField(Set<ConstraintViolation<Person>> constraintViolations,
        String field) {
        for (ConstraintViolation constraintViolation : constraintViolations) {
            String notValidField = constraintViolation.getPropertyPath().toString();
            if (notValidField.equals(field)) {
                return constraintViolation;
            }
        }
        return null;
    }

    @Test
    public void testNullConstraints() {
        Person emptyPerson = new Person();

        Set<ConstraintViolation<Person>> constraintViolations = getEntityValidatorFactory().getValidator()
            .validate(emptyPerson);

        Assert.assertEquals(5, constraintViolations.size());

        ConstraintViolation<Person> aliasNullConstraint = findByField(constraintViolations, "alias");
        Assert.assertNotNull(aliasNullConstraint);
        Assert.assertEquals("may not be null", aliasNullConstraint.getMessage());

        ConstraintViolation<Person> familyNameNullConstraint = findByField(constraintViolations, "name.family");
        Assert.assertNotNull(familyNameNullConstraint);
        Assert.assertEquals("may not be null", familyNameNullConstraint.getMessage());

        ConstraintViolation<Person> givenNameNullConstraint = findByField(constraintViolations, "name.given");
        Assert.assertNotNull(givenNameNullConstraint);
        Assert.assertEquals("may not be null", givenNameNullConstraint.getMessage());

        ConstraintViolation<Person> cityNullConstraint = findByField(constraintViolations, "address.city");
        Assert.assertNotNull(givenNameNullConstraint);
        Assert.assertEquals("may not be null", cityNullConstraint.getMessage());

        ConstraintViolation<Person> emailNullConstraint = findByField(constraintViolations, "contact.email");
        Assert.assertNotNull(emailNullConstraint);
        Assert.assertEquals("may not be null", emailNullConstraint.getMessage());

        emptyPerson = EntityTestUtility.createDefaultPerson();
        constraintViolations.clear();
        constraintViolations = getEntityValidatorFactory().getValidator().validate(emptyPerson);
        Assert.assertEquals(0, constraintViolations.size());
    }

    @Test
    public void testLifeCycle() {
        EntityManager entityManager = getEntityManagerFactory().createEntityManager();
        PersonDao personDao = new PersonDaoBean(entityManager);

        //create person
        Person testPerson = EntityTestUtility.createDefaultPerson();
        personDao.create(testPerson);
        Assert.assertNotNull("Identity should not be null!", testPerson.getIdentity());
        getWasteBasket().add(testPerson.getIdentity());

        Assert.assertNotNull(testPerson.getPasswordHash());

        //Find created person
        Person findPerson = personDao.findOne(testPerson.getIdentity());
        Assert.assertNotNull(findPerson);
        Assert.assertTrue(testPerson.compareTo(findPerson) == 0);
        Assert.assertEquals(testPerson.getAlias(), findPerson.getAlias());
        Assert.assertEquals(testPerson.getName().toString(), findPerson.getName().toString());
        Assert.assertEquals(testPerson.getContact().getEmail(),findPerson.getContact().getEmail());

        //update Person
        testPerson.setAlias("new Alias");
        personDao.update(testPerson);
        Person findPersonWithNewAlias = personDao.findOne(testPerson.getIdentity());
        Assert.assertEquals("new Alias", findPersonWithNewAlias.getAlias());
    }
}
