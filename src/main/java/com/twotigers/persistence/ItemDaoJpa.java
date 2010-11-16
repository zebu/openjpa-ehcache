package com.twotigers.persistence;

import org.springframework.stereotype.Repository;

@Repository("itemDao")
public class ItemDaoJpa extends BaseDao<Item> implements ItemDao {

	@Override
	public void alwaysThrowsException() {
		
		throw new RuntimeException();
		
	}

}
