package org.chenile.cache.model;

import java.io.Serializable;
import java.util.List;


public class CacheKey implements Serializable{
	private static final long serialVersionUID = 2219928603176575494L;
	public List<Object> apiInvocation;
	public String serviceName;
	public String opName;
	
	@Override
	public String toString() {
		return "CacheKey [apiInvocation=" + apiInvocation + ", serviceName=" + serviceName + ", opName=" + opName + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((apiInvocation == null) ? 0 : apiInvocation.hashCode());
		result = prime * result + ((opName == null) ? 0 : opName.hashCode());
		result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
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
		CacheKey other = (CacheKey) obj;
		if (apiInvocation == null) {
			if (other.apiInvocation != null)
				return false;
		} else if (!apiInvocation.equals(other.apiInvocation))
			return false;
		if (opName == null) {
			if (other.opName != null)
				return false;
		} else if (!opName.equals(other.opName))
			return false;
		if (serviceName == null) {
			if (other.serviceName != null)
				return false;
		} else if (!serviceName.equals(other.serviceName))
			return false;
		return true;
	}
}