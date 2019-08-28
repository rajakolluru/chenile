package org.chenile.stm.test.basicflow;

import java.util.ArrayList;
import java.util.List;

import org.chenile.stm.model.BaseStateEntity;

public class Cart extends BaseStateEntity{

	/**
	 * 
	 */
	private List<String> log = new ArrayList<String>();
	private static final long serialVersionUID = 8695851110437663595L;
	private String userId;
	private int testObj;
	public int getTestObj() {
		return testObj;
	}

	public void setTestObj(int testObj) {
		this.testObj = testObj;
	}

	private List<Item> cartItems = new ArrayList<Item>();
	private Payment payment;
	
	public List<String> getLog(){
		return log;
	}
	
	public void log(String s){
		log.add(s);
	}
	
	public void addItem(Item item){
		cartItems.add(item);
	}
	
	public Item getItem(int index){
		return cartItems.get(index);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}
	
	
	

}
