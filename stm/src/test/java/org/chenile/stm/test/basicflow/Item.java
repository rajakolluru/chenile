package org.chenile.stm.test.basicflow;

public class Item {
	private String sku;
	private int quantity;
	private double price;

	public Item (String sku,int quantity, double price){
		this.setSku(sku);
		this.setQuantity(quantity);
		this.setPrice(price);
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}
