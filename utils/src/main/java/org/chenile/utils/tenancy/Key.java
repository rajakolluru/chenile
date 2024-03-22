package org.chenile.utils.tenancy;

import java.io.Serializable;

/**
 * Key for the tenant specific cache. 
 * @author r0k02sw
 *
 */
public class Key  implements Serializable{		
		
		private static final long serialVersionUID = -114138098511468329L;
		public String name;
		public String tenantId;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
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
			Key other = (Key) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (tenantId == null) {
				if (other.tenantId != null)
					return false;
			} else if (!tenantId.equals(other.tenantId))
				return false;
			return true;
		}
		
	}

