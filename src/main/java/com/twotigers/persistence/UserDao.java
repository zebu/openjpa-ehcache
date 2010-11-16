package com.twotigers.persistence;

public interface UserDao {
	public abstract void add(Object object);

	public abstract void update(Object object);

	public abstract User findById(long id);

	public abstract User findByEmail(String email);
	
	public abstract User findByName(String firstName, String lastName);
}