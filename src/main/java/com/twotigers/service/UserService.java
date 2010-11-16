package com.twotigers.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.twotigers.persistence.User;
import com.twotigers.persistence.UserDao;

@Transactional
@Service("userService")
public class UserService {
	@Autowired
	private UserDao userDao;
	
	@PersistenceContext
	protected EntityManager em;
	
	public void createUser(User user) {
		userDao.add(user);
		//em.flush();
	}
	
	public User findByName(String firstName, String lastName) {
		return userDao.findByName(firstName, lastName);
	}
}
