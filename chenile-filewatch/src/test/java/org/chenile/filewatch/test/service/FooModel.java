package org.chenile.filewatch.test.service;

public class FooModel {
	private String bar;
	public FooModel(String bar, String baz) {
		this.bar = bar; this.baz = baz;
	}
	
	public FooModel() {}
	public String getBar() {
		return bar;
	}
	public void setBar(String bar) {
		this.bar = bar;
	}
	public String getBaz() {
		return baz;
	}
	public void setBaz(String baz) {
		this.baz = baz;
	}
	private String baz;
	@Override
	public String toString() {
		return "FooModel [bar=" + bar + ", baz=" + baz + "]";
	}
	
	public String toCsvString() {
		return  bar + "," + baz;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bar == null) ? 0 : bar.hashCode());
		result = prime * result + ((baz == null) ? 0 : baz.hashCode());
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
		if (bar == null) {
			if (other.bar != null)
				return false;
		} else if (!bar.equals(other.bar))
			return false;
		if (baz == null) {
			if (other.baz != null)
				return false;
		} else if (!baz.equals(other.baz))
			return false;
		return true;
	}
	
}
