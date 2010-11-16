package com.twotigers.demo.jpa2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

	@Test
	@Rollback(false)
	public void testNull() {
		
		try { 
			
			dao.alwaysThrowsException() ;
			
			fail();
		
		} catch (Exception e) {
			
		}
		
	}
	
	@Test
	@Rollback(false)
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
	@Rollback(false)
	public void CacheHit() {
		// create user
		User user = new User("firstName", "lastName", null, null, null);
		userService.createUser(user);
		
//		for (int i=0; i<100; i++) {
//			userService.findByName(user.getFirstName(), user.getLastName());
//		}
		
		try { Thread.sleep(10000); } catch (InterruptedException e) { }
	}
	
	@Test
	@Rollback(false)
	public void testCreateBatchOfObjects() {

		// create batch of users
		User user = new User();
		user.setFirstName("firstName");
		user.setLastName("lastName");
		user.setEmail("test@test.com");
		user.setPassword("password");
		
		userService.createUser(user);
		System.out.println(user);
		
		user = new User();
		user.setFirstName("firstName1");
		user.setLastName("lastName");
		user.setEmail("test@test.com");
		user.setPassword("password");
		userService.createUser(user);
		System.out.println(user);

		user = userService.findByName("firstName", "lastName");
		user = userService.findByName("firstName", "lastName");
		user = userService.findByName("firstName", "lastName");
		user = userService.findByName("firstName", "lastName");
		user = userService.findByName("firstName", "lastName");
		//System.out.println(user);
		
//		int maxObjects = 300;
//		long objectCreatePause	= 1000;
//		
//		for (int objectCount=1; objectCount<maxObjects; objectCount++) {
//			User user = new User();
//			user.setFirstName("firstName");
//			user.setLastName("lastName");
//			user.setEmail("test@test.com");
//			user.setPassword("password");
//			
//			userService.createUser(user);
//			System.out.println(user);
//			
//			if ((objectCount % 10)==0) System.out.println("testCreateBatchOfObjects: created "+objectCount);
//			
//			if (objectCreatePause>0) try { Thread.sleep(objectCreatePause); }
//			catch (InterruptedException ie) {}
//		}
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
}
