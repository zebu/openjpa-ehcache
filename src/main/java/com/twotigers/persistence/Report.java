package com.twotigers.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;

@Entity
public class Report {

	@Version
	private long version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	String title;
	String code;
	
	//@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	List<Item> items = new ArrayList<Item>();
	
	//@OneToMany
	@Transient
	List<String> notes = new ArrayList<String>();

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

}
