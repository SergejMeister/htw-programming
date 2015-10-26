package com.htw.master.prog.broker.dao.impl;

import com.htw.master.prog.broker.dao.GenericDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

public abstract class GenericDaoImpl<T extends Serializable> implements GenericDao<T> {

    private Class<T> clazz;

    @PersistenceContext
    private EntityManager entityManager;

    public GenericDaoImpl() {
    }

    public GenericDaoImpl(Class<T> clazz) {
        setClazz(clazz);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setClazz(final Class<T> clazzToSet) {
        this.clazz = clazzToSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T findOne(final long id) {
        return entityManager.find(clazz, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        return getEntityManager().createQuery("from " + clazz.getName()).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(final T entity) {
        entityManager.persist(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T update(final T entity) {
        return entityManager.merge(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(final T entity) {
        getEntityManager().remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        getEntityManager().createQuery("delete from " + clazz.getName()).executeUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(final long entityId) {
        final T entity = findOne(entityId);
        delete(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Query createQuery(String queryString) {
        return entityManager.createQuery(queryString);
    }

    /**
     * @return the entity manager
     */
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
