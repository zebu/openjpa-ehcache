package com.twotigers.demo.jpa2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
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
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAEntityManagerFactorySPI;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.twotigers.persistence.ExpenseEvent;
import com.twotigers.persistence.ExpenseType;
import com.twotigers.persistence.Item;
import com.twotigers.persistence.ItemDao;
import com.twotigers.persistence.PaymentType;
import com.twotigers.persistence.Report;
import com.twotigers.persistence.User;
import com.twotigers.persistence.UserDao;
import com.twotigers.service.UserService;

@ContextConfiguration(locations = { "classpath:config/ac-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class BaseJpaTest {

	private static final String TEST_REPORT = "Test Report";

	@PersistenceContext
	protected EntityManager em;

	@Autowired
	protected UserDao userDao;

	@Autowired
	@Qualifier("itemDao")
	private ItemDao dao;

	@Autowired
	private UserService userService;
	
	private long expenseTypeId;

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		CacheManager cacheManager = new CacheManager();
		cacheManager.shutdown();
	}
	
	@Test(expected = RuntimeException.class)
	@Rollback(true)
	public void testNull() {
		dao.alwaysThrowsException();
	}
	
	@Test
	@Rollback(true)
	@Transactional
	public void testObjectCreation() {

		User user = createAndSaveUser();
		assertTrue(em.contains(user));
		
		setupBasicObjectHierarchy();
		ExpenseType exType = (ExpenseType)dao.findById(ExpenseType.class, expenseTypeId);
	
		assertNotNull(exType);
		assertTrue(em.contains(exType));
		assertThat(exType.getName(), is("airfare"));
		em.clear();
		OpenJPAEntityManager session = ((OpenJPAEntityManager)em);
		
		assertEquals(0, session.getManagedObjects().size());
		Report r = (Report)dao.findSingleObject("select r from Report r where r.title = ?1", TEST_REPORT);
		assertEquals(1, session.getManagedObjects().size());
		assertEquals(3, r.getItems().size());
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
	
	protected User createAndSaveUser() {
		User user = new User();
		user.setEmail("test@test.com");
		user.setPassword("password");
		userDao.add(user);
		return user;
	}

	public void setupBasicObjectHierarchy() {
	    ExpenseType expenseType = new ExpenseType();
	    expenseType.setName("meals");
        ExpenseType expenseType2 = new ExpenseType();
        expenseType.setName("airfare");
        
        User user = new User();
		user.setUserid("testuser");
		user.setLastName("bloggs");
		user.setFirstName("Joe");
		user.setEmail("jb@email.com");
		user.setPassword("password");

		User admin = new User();
		user.setUserid("admin");
		user.setLastName("admin");
		user.setFirstName("admin");
		user.setEmail("admin@email.com");
		user.setPassword("password");		
        		
        ExpenseEvent event = new ExpenseEvent();
        event.setName("Conference");
        ExpenseEvent event2 = new ExpenseEvent();
        event2.setName("Sales Meeting1");
        
        dao.persist(event);
        dao.persist(event2);
        dao.persist(expenseType);
        dao.persist(expenseType2);
        expenseTypeId = expenseType.getId();
        userDao.add(user);
        userDao.add(admin);
        Item item = new Item();
        	item.setDesc("Item 1");
        	item.setPayee("chilis");
            item.setAmount(12.95f);
            item.setPaymentType(PaymentType.CASH);
            item.setExpenseType(expenseType);
            item.setUser(user);
            item.setEvent(event);
   
        dao.add(item);

        Item item2 = new Item();
        item2.setDesc("Item 2");
        item2.setPayee("outback");
        item2.setAmount(27.00f);
        item2.setPaymentType(PaymentType.CASH);
        item2.setExpenseType(expenseType);
        item2.setUser(user);
        item2.setEvent(event);

        dao.add(item2);

        Item item3 = new Item();
        item3.setDesc("Flight to NO");
        item3.setPayee("Delta");
        item3.setAmount(510.11f);
        item3.setPaymentType(PaymentType.CREDITCARD);
        item3.setExpenseType(expenseType2);
        item3.setUser(user);
        item3.setEvent(event);
        dao.add(item3);

        Report report = new Report();
        report.setTitle(TEST_REPORT);
        report.setCode("SOME OTHER Code");
        report.getItems().add(item);
        report.getItems().add(item2);
        report.getItems().add(item3);
        dao.persist(report);
        em.flush();
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
