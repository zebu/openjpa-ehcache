package com.twotigers.persistence;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


public abstract class BaseDao<T> implements BaseDaoInterface<T> {

	@PersistenceContext
	protected EntityManager em;

	private Class<T> entityClass;
	
	//private static Logger log = Logger.getLogger(BaseDao.class);

	public BaseDao() {
		entityClass = getEntityClass();
	}

	/* (non-Javadoc)
	 * @see com.twotigers.persistence.BaseDaoInterface#setEntityManager(javax.persistence.EntityManager)
	 */
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}	

	@SuppressWarnings("unchecked")
	protected synchronized Class<T> getEntityClass() {

		// return Assignment.class
		if (entityClass == null) {
			Type type = getClass().getGenericSuperclass();
			loop: while (true) {
				if (type instanceof ParameterizedType) {
					Type[] arguments = ((ParameterizedType) type)
							.getActualTypeArguments();
					for (Type argument : arguments) {
						if (argument instanceof Class) {
							entityClass = (Class<T>) argument;
							break loop;
						}
					}
				}
			}
		}
		return entityClass;
	}
	

	/* (non-Javadoc)
	 * @see com.twotigers.persistence.BaseDaoInterface#persist(java.lang.Object)
	 */
	public void persist(Object object) {
		em.persist(object);
	}
	
	/* (non-Javadoc)
	 * @see com.twotigers.persistence.BaseDaoInterface#find(java.lang.Class, java.lang.Object)
	 */
	public Object find(final Class<T> entityClass, final Object id) {
		return (T)em.find(entityClass, id);
	}

	/* (non-Javadoc)
	 * @see com.twotigers.persistence.BaseDaoInterface#findById(java.lang.Class, java.lang.Object)
	 */
	public Object findById(final Class entityClass, final long id) {
		return em.find(entityClass, id);
	}

	/* (non-Javadoc)
	 * @see com.twotigers.persistence.BaseDaoInterface#findById(java.lang.Object)
	 */
	public T findById(final Object id) {
		return (T) em.find(getEntityClass(), id);
	}

	/* (non-Javadoc)
	 * @see com.twotigers.persistence.BaseDaoInterface#merge(T)
	 */
	public T merge(final T entity) {
		T t = em.merge(entity);
		em.flush();
		return t;
	}

	/* (non-Javadoc)
	 * @see com.twotigers.persistence.BaseDaoInterface#remove(T)
	 */
	public void remove(final T entity) {
		em.remove(entity);
		em.flush();
	}

	/* (non-Javadoc)
	 * @see com.twotigers.persistence.BaseDaoInterface#flush()
	 */
	public void flush() {
		em.flush();
	}

	/* (non-Javadoc)
	 * @see com.twotigers.persistence.BaseDaoInterface#add(T)
	 */
	public void add(final T t) {		 
		em.persist(t);
		em.flush();
	}

	/* (non-Javadoc)
	 * @see com.twotigers.persistence.BaseDaoInterface#update(T)
	 */
	public void update(final T entity) {
		em.merge(entity);
		em.flush();		
	}
	
	/* (non-Javadoc)
	 * @see com.twotigers.persistence.BaseDaoInterface#find(java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public List<T> find(final String queryString, final Object... values) {
		Query queryObject = em.createQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				queryObject.setParameter(i + 1, values[i]);
			}
		}
		return queryObject.getResultList();
	}

	/* (non-Javadoc)
	 * @see com.twotigers.persistence.BaseDaoInterface#findSingle(java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public T findSingle(final String queryString, final Object... values) {
		Query queryObject = em.createQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				queryObject.setParameter(i + 1, values[i]);
			}
		}
		return (T)queryObject.getSingleResult();
	}
	
	public Object findSingleObject(final String queryString, final Object... values) {
		Query queryObject = em.createQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				queryObject.setParameter(i + 1, values[i]);
			}
		}
		return queryObject.getSingleResult();
	}

	/* (non-Javadoc)
	 * @see com.twotigers.persistence.BaseDaoInterface#removeAllEntities(java.util.Collection)
	 */
	public void removeAllEntities(Collection<T> entities) {
	
	}

}
