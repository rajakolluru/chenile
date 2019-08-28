package org.chenile.http.test;

public class B {
	private Parent parent;
	
	public B(Parent p) { this.parent = p;}
	public String getString() {
		return parent.getString();
	}
}
