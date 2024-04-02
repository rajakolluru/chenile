/**
 * 
 */
package org.chenile.query.controller.util;

/**
 * @author venu
 *
 */
public enum GetAllEntities {

	indent("Indent.getAll"),order("Order.getAll"),trip("Trip.getAll");
	
	
	private String value; 
	private GetAllEntities(String value) { this.value = value; }
	
	public String get() {
		return this.value;
	}
}
