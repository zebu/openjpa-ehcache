package com.twotigers.persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.openjpa.persistence.DataCache;

@DataCache(name = "userCache")
@Entity
@NamedQueries({
@NamedQuery(name="findByEmail", query="select u from User u where u.email = :email"),
@NamedQuery(name="findByName", query="select u from User u where u.firstName = :firstName and u.lastName = :lastName")
})
@Table(name="baseuser")
public class User {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Version
	private long version;
	
	private String lastName;
    private String firstName;
    private String email;
    private String userid;
    private String password;
    
    public User() {}
    public User(String firstName, String lastName, String email, String userid, String password) {
    	this.firstName	= firstName;
    	this.lastName	= lastName;
    	this.email		= email;
    	this.userid		= userid;
    	this.password	= password;
    }
    
	public long getId() {
		return id;
	}

    public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		return toString(sb).toString();
    }

	public StringBuffer toString(StringBuffer sb) {
		sb.append("id:"); sb.append(id);
		sb.append(" lastName:"); sb.append(lastName);
		sb.append(" firstName:"); sb.append(firstName);
		sb.append(" email:"); sb.append(email);
		sb.append(" userid:"); sb.append(userid);
		sb.append(" password:"); sb.append(password);
		sb.append(" version:"); sb.append(version);
		return sb;
	}
}
