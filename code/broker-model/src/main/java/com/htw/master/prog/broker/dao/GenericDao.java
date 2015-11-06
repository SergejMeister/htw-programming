package com.htw.master.prog.broker.dao;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

/**
 * Generic abstract data access interface defining the most important functions.
 *
 * @param <T> the corresponding entity class
 */
public interface GenericDao<T extends Serializable> {

    /**
     * set entity class to be used.
     *
     * @param clazzToSet entity class to use
     */
    void setClazz(final Class<T> clazzToSet);

    /**
     * Save entity.
     *
     * @param entity entity
     */
    void create(final T entity);

    /**
     * find all entities.
     *
     * @return a list of all entities
     */
    List<T> findAll();

    /**
     * update an entity.
     *
     * @param entity the entity you want to update
     */
    void update(final T entity);

    /**
     * find an entity by its id.
     *
     * @param id entity id
     * @return entity class
     */
    T findOne(final long id);

    /**
     * delete all entities.
     */
    void deleteAll();

    /**
     * delete an entity identified by its id.
     *
     * @param entityId id of the entity you want to delete
     */
    void deleteById(final long entityId);

    /**
     * delete an entity.
     *
     * @param entity to be deleted
     */
    void delete(final T entity);

    /**
     * creates a JPA query out of a given query string without exposing the
     * entity manager.
     *
     * @param queryString your query string
     * @return a {@link javax.persistence.Query} object
     */
    Query createQuery(String queryString);
}
