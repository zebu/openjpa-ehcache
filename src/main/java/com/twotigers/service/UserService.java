package com.twotigers.service;

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
	
	public void createUser(User user) {
		userDao.add(user);
	}
	
	public User findByName(String firstName, String lastName) {
		return userDao.findByName(firstName, lastName);
	}
}
