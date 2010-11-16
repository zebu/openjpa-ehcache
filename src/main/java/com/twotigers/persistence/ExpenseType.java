package com.twotigers.persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ExpenseType {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// ! nullable
    String name;

    public String toString() {
        return name;
    }

}
