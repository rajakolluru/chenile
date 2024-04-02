/**
 * 
 */
package org.chenile.query.controller.util;

/**
 * @author venu
 *
 */
public enum GetByIdEntities {

	indent("Indent.getById"),order("Order.getById"),trip("Trip.getById"),driver("Trip.getAllDriverTrips");
	
	private String value; 
	private GetByIdEntities(String value) { this.value = value; }
	
	public String get() {
		return this.value;
	}
}
