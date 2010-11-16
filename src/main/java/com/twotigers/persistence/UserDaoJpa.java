package com.twotigers.persistence;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

@Repository("userDao")
public class UserDaoJpa implements UserDao {

	@PersistenceContext
	EntityManager em;

	
	/* (non-Javadoc)
	 * @see com.twotigers.demo.jpa2.UserDao#add(java.lang.Object)
	 */
	public void add(Object object) {
		em.persist(object);		
	}
	
	/* (non-Javadoc)
	 * @see com.twotigers.demo.jpa2.UserDao#update(java.lang.Object)
	 */
	public void update(Object object) {
		em.merge(object);		
	}
	
	/* (non-Javadoc)
	 * @see com.twotigers.demo.jpa2.UserDao#findById(long)
	 */
	public User findById(long id) {
		return em.find(User.class, id);
	}
	
	/* (non-Javadoc)
	 * @see com.twotigers.demo.jpa2.UserDao#findByEmail(java.lang.String)
	 */
	public User findByEmail(String email){
		Query q = em.createNamedQuery("findByEmail");
		q.setParameter("email", email);
		return (User) q.getSingleResult();
	}
	
	public User findByName(String firstName, String lastName) {
		Query q = em.createNamedQuery("findByName");
		q.setParameter("firstName", firstName);
		q.setParameter("lastName", lastName);
		try { return (User) q.getSingleResult(); }
		catch (NoResultException e) { return null; }
	}
}
