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
	
	public void create(User user) { userDao.add(user); }
	public void update(User user) { userDao.update(user); }
	public void deleteByName(String firstName, String lastName) { userDao.deleteByName(firstName, lastName); }
	
	public User findByName(String firstName, String lastName) {
		return userDao.findByName(firstName, lastName);
	}
}
