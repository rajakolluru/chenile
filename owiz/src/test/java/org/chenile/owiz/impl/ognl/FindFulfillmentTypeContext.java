package org.chenile.owiz.impl.ognl;

import java.util.List;

import org.chenile.owiz.config.model.CommandDescriptor;

public class FindFulfillmentTypeContext {
	private String fulfillmentType;
	private String category;
	private String sku;
	private int packageWeight;
	private String warehouse ;
	private List<String> categories;

	
	public List<String> getCategories() {
		return categories;
	}
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	private CommandDescriptor<FindFulfillmentTypeContext> commandDescriptor;
	
	public String getFulfillmentType() {
		return fulfillmentType;
	}
	public void setFulfillmentType(String fulfillmentType) {
		this.fulfillmentType = fulfillmentType;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public int getPackageWeight() {
		return packageWeight;
	}
	public void setPackageWeight(int packageWeight) {
		this.packageWeight = packageWeight;
	}
	public String getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}
	public void setCommandDescriptor(
			CommandDescriptor<FindFulfillmentTypeContext> cd) {
		this.commandDescriptor = cd;
		
	}
	public CommandDescriptor<FindFulfillmentTypeContext> getCommandDescriptor() {
		return this.commandDescriptor;
	}
}
