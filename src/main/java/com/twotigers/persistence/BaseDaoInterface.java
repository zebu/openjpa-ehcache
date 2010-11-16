package com.twotigers.persistence;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

public interface BaseDaoInterface<T> {

	public abstract void setEntityManager(EntityManager em);

	public abstract void persist(Object object);

	public abstract Object find(final Class<T> entityClass, final Object id);

	public abstract Object findById(final Class entityClass, final long id);

	public abstract T findById(final Object id);

	public abstract T merge(final T entity);

	public abstract void remove(final T entity);

	public abstract void flush();

	public abstract void add(final T t);

	public abstract void update(final T entity);

	public abstract List<T> find(final String queryString, final Object... values);

	/**
	 * return a single object
	 * @param queryString
	 * @param values
	 * @return
	 */
	public abstract T findSingle(final String queryString,
			final Object... values);

	public abstract void removeAllEntities(Collection<T> entities);
	
	public Object findSingleObject(final String queryString, final Object... values) ;

}