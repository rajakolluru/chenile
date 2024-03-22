package org.chenile.proxy.test.service;

import java.io.Serializable;

public class FooModel implements Serializable{
	private static final long serialVersionUID = -8027625391447047491L;
	private int increment;

	public FooModel(int increment) {
		this.setIncrement(increment); 
	}
	
	public FooModel() {}

	public int getIncrement() {
		return increment;
	}

	public void setIncrement(int increment) {
		this.increment = increment;
	}

	@Override
	public String toString() {
		return "FooModel [increment=" + increment + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + increment;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FooModel other = (FooModel) obj;
		if (increment != other.increment)
			return false;
		return true;
	}
	
	
	
}
