package com.twotigers.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.dao.DataAccessException;

public class DaoTemplate {
	
	protected EntityManager em;
	
	Class entityClass;
	
	public DaoTemplate(final Class entityClass) {
		this.entityClass = entityClass;
	}

	public List find(final String queryString, final Object... values) {
		Query queryObject = em.createQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				queryObject.setParameter(i + 1, values[i]);
			}
		}
		return queryObject.getResultList();
	}

	public void persist(Object object) {
		em.persist(object);
	}
	
	public <T> T find(final Class<T> entityClass, final Object id) throws DataAccessException {
		return (T)em.find(entityClass, id);
	}

	public void remove(Object entity) {
		em.remove(entity);
	}

	public void flush() {
		em.flush();
	}

	public <T> T merge(final T entity) {
				return em.merge(entity);
	}

	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

}
