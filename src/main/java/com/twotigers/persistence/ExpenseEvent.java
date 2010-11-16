package com.twotigers.persistence;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class ExpenseEvent {

	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	public long getId() {
		return id;
	}
	
    String name;
    
    @OneToMany
    List<Item> items;

    public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public List<Item> getItems() {
		return items;
	}



	public void setItems(List<Item> items) {
		this.items = items;
	}



	public String toString() {
        return name;
    }
}
