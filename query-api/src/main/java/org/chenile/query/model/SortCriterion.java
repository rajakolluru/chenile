package org.chenile.query.model;

public class SortCriterion {
	private int index;
	private String name;
	private boolean ascendingOrder;

	public int getIndex() {
		return this.index;
	}

	public void setIndex(int sortIndex) {
		this.index = sortIndex;
		
	}

	public String getName() {
		return this.name;
	}

	public void setName(String sortName) {
		this.name = sortName;
	}

	public boolean isAscendingOrder() {
		return ascendingOrder;
	}

	public void setAscendingOrder(boolean b) {
		this.ascendingOrder = b;		
	}

}
