package org.chenile.stm.test.orderapproval;

import java.util.ArrayList;
import java.util.List;

import org.chenile.stm.State;
import org.chenile.stm.model.BaseStateEntity;

public class Order extends BaseStateEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4821145400886714596L;
	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getNumItems() {
		return numItems;
	}
	public void setNumItems(int numItems) {
		this.numItems = numItems;
	}
	public List<String> getItemNames() {
		return itemNames;
	}
	public void setItemNames(List<String> itemNames) {
		this.itemNames = itemNames;
	}
	public double getOrderTotal() {
		return orderTotal;
	}
	public void setOrderTotal(double orderTotal) {
		this.orderTotal = orderTotal;
	}
	
	public List<State> getEntryStates(){
		return entryStates;
	}
	
	public void addEntryTransition(State state){
		entryStates.add(state);
	}
	
	public List<State> getExitStates(){
		return exitStates;
	}
	
	public void addExitTransition(State state){
		exitStates.add(state);
	}
	private String status;
	private int numItems ;
	private List<String> itemNames;
	private double orderTotal;
	private List<State> entryStates = new ArrayList<State>(); // test variable used to verify if flow transitions are correct.
	private List<State> exitStates = new ArrayList<State>(); // test variable used to verify if flow transitions are correct.
	@Override
	public String toString() {
		return "Order [id=" + id + ", status=" + status + ", numItems="
				+ numItems + ", itemNames=" + itemNames + ", orderTotal="
				+ orderTotal + ", entryStates=" + entryStates + ", exitStates="
				+ exitStates + "]";
	}
	
	
	
}
