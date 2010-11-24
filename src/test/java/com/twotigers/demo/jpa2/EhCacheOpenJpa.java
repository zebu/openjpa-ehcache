package com.twotigers.demo.jpa2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import net.sf.ehcache.CacheManager;

import org.apache.openjpa.conf.OpenJPAConfiguration;
import org.apache.openjpa.datacache.DataCache;
import org.apache.openjpa.meta.ClassMetaData;
import org.apache.openjpa.meta.MetaDataRepository;
import org.apache.openjpa.persistence.JPAFacadeHelper;
import org.apache.openjpa.persistence.OpenJPAEntityManagerFactorySPI;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
	protected EntityManager em;
	
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
		
		String cacheName = getConfiguredDataCacheName(User.class);
		assertEquals("userCache", cacheName);
	}
	
	@Test
	public void CacheHit() {
		// verify a stored object is pulled from the cache
		User user = new User("cacheHit", "lastName", null, null, null);
		assertFalse(em.contains(user));
		userService.create(user);
		Object oid = user.getId();
		
		// The transaction is committed so the em is empty.
        assertFalse(em.contains(user));
        
        // verify object is in L2 cache
        assertTrue(getCache(user.getClass()).contains(getOpenJPAId(user, oid)));
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
	
	/**
     * Gets the data cache for the given class.
     */
    DataCache getCache(Class<?> cls) {
    	EntityManagerFactory emf = em.getEntityManagerFactory();
    	OpenJPAConfiguration conf = ((OpenJPAEntityManagerFactorySPI) emf).getConfiguration();
    	
        String name = getConfiguredDataCacheName(cls);
        return conf.getDataCacheManagerInstance().getDataCache(name);
    }

    /**
     * Gest the configured name of the cache for the given class.
     */
    String getConfiguredDataCacheName(Class<?> cls) {
    	EntityManagerFactory emf = em.getEntityManagerFactory();
    	OpenJPAConfiguration conf = ((OpenJPAEntityManagerFactorySPI) emf).getConfiguration();
    	
        MetaDataRepository mdr = conf.getMetaDataRepositoryInstance();
        ClassMetaData meta = mdr.getMetaData(cls, null, true);
        return meta.getDataCacheName();
    }

    Object getOpenJPAId(Object pc, Object oid) {
    	EntityManagerFactory emf = em.getEntityManagerFactory();
    	OpenJPAConfiguration conf = ((OpenJPAEntityManagerFactorySPI) emf).getConfiguration();
    	
        ClassMetaData meta = conf.getMetaDataRepositoryInstance()
                .getCachedMetaData(pc.getClass());
        assertNotNull(meta);
        Object ooid = JPAFacadeHelper.toOpenJPAObjectId(meta, oid);
        assertNotNull(oid);
        return ooid;
    }
}
