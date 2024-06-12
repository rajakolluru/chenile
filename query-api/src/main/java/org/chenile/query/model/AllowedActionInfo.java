package org.chenile.query.model;

/**
 * Specifies the metadata for the actions that are allowed for this entity. <br/>
 * For example, label represents the english label for the action that can be displayed in the front end<br/>
 * The link provides the link to the URL that needs to be called if the action is clicked
 * name is the internal name of the action<br/>
 * isCombinable indicates if this action can be invoked in combination for multiple entities.<br/>
 * For example, an Order entity may have a "ship" action that allows multiple orders to be
 * shipped together. <br/>
 * But the Order entity may have an edit action that is not combinable. It wont be possible to
 * edit multiple orders at the same time. <br/>
 */
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
