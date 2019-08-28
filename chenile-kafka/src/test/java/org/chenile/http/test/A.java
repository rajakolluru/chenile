package org.chenile.http.test;

public class A {
	private Parent parent;
	private String suffix;
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public A(Parent p) { this.parent = p;}
	
	public String getString() {
		return parent.getString() + "." + suffix;
	}
}
