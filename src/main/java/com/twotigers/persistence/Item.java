package com.twotigers.persistence;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Item {

	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	public long getId() {
		return id;
	}
	
	
	@Column(name="desc1")
    String desc;
    
    @Temporal(TemporalType.DATE)
    Date createDate = new Date();
    
    //@Column(nullable=true)
    String payee;
    @Column(nullable=true)
    Float amount;

    @ManyToOne(optional=true)
    ExpenseType expenseType;

    @ManyToOne(optional=true)
    ExpenseEvent event1;
    
    @ManyToOne(optional=true)
    User user;
    @Column(nullable=true)
    PaymentType paymentType;
    boolean submitted = false;
    boolean reimbursed = false;
    boolean approved = false;

	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getPayee() {
		return payee;
	}
	public void setPayee(String payee) {
		this.payee = payee;
	}
	public Float getAmount() {
		return amount;
	}
	public void setAmount(Float amount) {
		this.amount = amount;
	}
	public ExpenseType getExpenseType() {
		return expenseType;
	}
	public void setExpenseType(ExpenseType expenseType) {
		this.expenseType = expenseType;
	}
	public ExpenseEvent getEvent() {
		return event1;
	}
	public void setEvent(ExpenseEvent event) {
		this.event1 = event;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public PaymentType getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}
	public boolean isSubmitted() {
		return submitted;
	}
	public void setSubmitted(boolean submitted) {
		this.submitted = submitted;
	}
	public boolean isReimbursed() {
		return reimbursed;
	}
	public void setReimbursed(boolean reimbursed) {
		this.reimbursed = reimbursed;
	}
	public boolean isApproved() {
		return approved;
	}
	public void setApproved(boolean approved) {
		this.approved = approved;
	}
    
	//@Column(name="item_desc")
	//@Column(name="desc1")
}
