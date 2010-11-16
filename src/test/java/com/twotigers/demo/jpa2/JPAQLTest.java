package com.twotigers.demo.jpa2;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.Query;

public class JPAQLTest extends BaseJpaTest {
	
	public void testCoalesce2() {
		setupBasicObjectHierarchy();

		String queryString = "select u from User u where coalesce(u.firstName, u.lastName) = 'admin'";

		Query query = em.createQuery(queryString);
		//queryObject.setParameter(1, "");
		List result = query.getResultList();
		System.out.println(result.get(0).getClass());
		
		assertEquals( userDao.findByEmail("admin@email.com"), result.get(0));
	}
	
	public void xtestCase() {
		setupBasicObjectHierarchy();

		String queryString = "select case u.firstName when 'admin' then 'Superuser' when 'Joe' then 'Joeseph' else ' ' end from User u where u.firstName in ('admin', 'Joe')";
		Query query = em.createQuery(queryString);
		//queryObject.setParameter(1, "");
		List result = query.getResultList();
		
		assertEquals("Superuser", result.get(0));
	}

}
