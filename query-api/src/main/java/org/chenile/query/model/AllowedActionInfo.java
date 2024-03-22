package org.chenile.query.model;

public class AllowedActionInfo {
	public String label;
	public String link;
	public String name;
	public boolean isCombinable = false;
	public AllowedActionInfo(String name, String link, String label, boolean isCombinable) {
		this.name = name;
		this.link = link;
		this.label = label;
		this.isCombinable = isCombinable;
	}
	public AllowedActionInfo() {}
}
