package com.twotigers.demo.jpa2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.sf.ehcache.CacheManager;

import org.apache.openjpa.datacache.DataCachePCData;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.att.bbnms.lightspeed.CacheUtils;
import com.twotigers.persistence.User;
import com.twotigers.service.UserService;

/**
 * 
 * Current Issues
 * EhCache w/ Terracotta w/ OpenJpa
 * -jpa query cache has to be disabled or casting exception is thrown
 *  org.springframework.orm.jpa.JpaSystemException: java.lang.String cannot be cast to org.apache.openjpa.datacache.QueryKey; nested exception is <openjpa-2.0.1-r422266:989424 nonfatal general error> org.apache.openjpa.persistence.PersistenceException: java.lang.String cannot be cast to org.apache.openjpa.datacache.QueryKey
 *  
 * -when in eclipse, junit process will not exit and has to be manually killed
 * 
 * -the persist test cases are failing in both EhCacheOpenJpa and TestEhCache. This may be an issue with how the L2 cache is accessed
 *
 */

@ContextConfiguration(locations = { "classpath:config/ac-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class EhCacheOpenJpa {

	@PersistenceContext
	protected EntityManager entityManager;
	
	@Autowired
	private UserService userService;
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		CacheManager cacheManager = new CacheManager();
		cacheManager.shutdown();
	}
	
	@Test
	public void DataCacheNameSpecified() {
		// verify an entity specifying a dataCache name uses the named dataCache
		User user = new User("dataCacheNameSpecified", "lastName", null, null, null);
		userService.create(user);
		
		String cacheName = CacheUtils.getConfiguredDataCacheName(entityManager, User.class);
		assertEquals("userCache", cacheName);
	}
	
	@Test
	public void CacheHit() {
		// verify a stored object is pulled from the cache
		User user = new User("cacheHit", "lastName", null, null, null);
		assertFalse(entityManager.contains(user));
		userService.create(user);
		Object oid = user.getId();
		
		// The transaction is committed so the em is empty.
        assertFalse(entityManager.contains(user));
        
        // verify object is in L2 cache
        assertTrue("Object does not exist in cache", CacheUtils.objectExistsInL2Cache(entityManager, user, oid));
        
//        User user2 = (User)CacheUtils.getObjectFromCache(entityManager, user, oid);
//		assertNotNull(user2);
//		assertEquals("cacheHit", user2.getFirstName());
	}
	
	// verify a stale object throws ole when stored
	@Test(expected = JpaOptimisticLockingFailureException.class)
	public void storeStaleObject() {
		// create user and save a copy for stale
		User user = new User("ole", "lastName", null, null, null);
		userService.create(user);
		User staleUser = userService.findByName("ole", "lastName");
		
		// update user
		user.setEmail("email");
		userService.update(user);
		
		// update stale user
		staleUser.setEmail("emailStale");
		userService.update(staleUser);
	}
	
	// verify a delete, removes object from cache
	@Test
	public void deleteObject() {
		// create and delete user
		User user = new User("delete", "lastName", null, null, null);
		userService.create(user);
		Object oid = user.getId();
		
		assertTrue("Object does not exist in cache", CacheUtils.objectExistsInL2Cache(entityManager, user, oid));
		
		userService.deleteByName("delete", "lastName");
		assertNull(userService.findByName("delete", "lastName"));
		assertFalse("Object should not exist in cache", CacheUtils.objectExistsInL2Cache(entityManager, user, oid));
	}
	
	// verify an update, updates object in cache
	@Test
	public void updateObject() {
		// create user
		User user = new User("update", "lastName", null, null, null);
		userService.create(user);
		Object oid = user.getId();
		
		assertTrue("Object does not exist in cache", CacheUtils.objectExistsInL2Cache(entityManager, user, oid));
		
		user.setFirstName("update2");
		userService.update(user);
		
		// TODO
		// I can't figure out how to build an object out of a DataCachePCData
		// Just checking a value for now
		DataCachePCData dataCachePCData = CacheUtils.getObjectFromCache(entityManager, user, oid);
		assertEquals("update2", dataCachePCData.getData(1));
	}
	
	// verify objects timeout from cache
}
