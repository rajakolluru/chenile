package org.chenile.utils.tenancy;

import java.net.URL;

public class CachedValue {
	public URL url;
	public String fileName;
	public CachedValue(URL url, String filename) {
		this.url = url;
		this.fileName = filename;
	}
}
