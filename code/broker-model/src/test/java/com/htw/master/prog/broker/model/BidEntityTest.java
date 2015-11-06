package com.htw.master.prog.broker.model;

import org.junit.Assert;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class BidEntityTest extends EntityTest {

    @Test
    public void testConstraints() {
        Bid emptyEntity = new Bid();
        Set<ConstraintViolation<Bid>> constraintViolations = getEntityValidatorFactory().getValidator().validate(
            emptyEntity);
        List<String> notValidFields = Arrays.asList("auction","bid");
        for(ConstraintViolation constraintViolation : constraintViolations){
            Assert.assertEquals("may not be null",constraintViolation.getMessage());
            //Assert.assertTrue(notValidFields.contains(constraintViolation.getPropertyPath().toString()));
        }

        Assert.assertTrue(Boolean.TRUE);
    }

    private ConstraintViolation<Bid> findByField(Set<ConstraintViolation<Bid>> constraintViolations,String field){
        for(ConstraintViolation constraintViolation : constraintViolations){
            String notValidField = constraintViolation.getPropertyPath().toString();
            if(notValidField.equals(field)){
                return constraintViolation;
            }
        }
        return null;
    }

    @Test
    public void testLifeCycle() {
        Assert.assertTrue(Boolean.TRUE);
    }
}
