package com.htw.master.prog.broker.dao.beans;

import com.htw.master.prog.broker.dao.GenericDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

public abstract class GenericDaoBean<T extends Serializable> implements GenericDao<T> {

    private final Logger LOG = LoggerFactory.getLogger(GenericDaoBean.class);
    private Class<T> clazz;
    //@PersistenceContext(name = "broker")
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public GenericDaoBean() {
    }

    public GenericDaoBean(Class<T> clazz) {
        setClazz(clazz);
    }

    public GenericDaoBean(EntityManager entityManager, Class<T> clazz) {
        this.entityManager = entityManager;
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
    @SuppressWarnings("unchecked")
    @Override
    public List<T> findAll() {
        return entityManager.createQuery("Select t from " + clazz.getSimpleName() + " t").getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(final T entity) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(entity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            LOG.error("GenericDaoBean.create: ", e);
            entityManager.getTransaction().rollback();
            throw new PersistenceException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final T entity) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(entity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            LOG.error("GenericDaoBean.update: ", e);
            entityManager.getTransaction().rollback();
            throw new PersistenceException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(final T entity) {
        try {
            entityManager.getTransaction().begin();
            getEntityManager().remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            LOG.error("GenericDaoBean.delete: ", e);
            entityManager.getTransaction().rollback();
            throw new PersistenceException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().createQuery("delete from " + clazz.getName()).executeUpdate();
            getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            LOG.error("GenericDaoBean.deleteAll: ", e);
            entityManager.getTransaction().rollback();
            throw new PersistenceException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(final long entityId) {

        try {
            final T entity = findOne(entityId);
            getEntityManager().getTransaction().begin();
            delete(entity);
            getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            LOG.error("GenericDaoBean.deleteById: ", e);
            entityManager.getTransaction().rollback();
            throw new PersistenceException(e);
        }
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
