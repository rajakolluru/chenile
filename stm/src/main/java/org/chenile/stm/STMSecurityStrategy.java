package org.chenile.stm;

/**
 * An interface that would be used to invoke a security strategy.
 * The strategy would be used to check if a certain transition is allowed for a principal. The notion of principals is 
 * not there in STM. Instead, we assume that this strategy has access to the invoking principal (via a thread local for instance)
 * that would allow it to determine if the principal has the requisite privileges.
 * @author Raja Shankar Kolluru
 *
 */
public interface STMSecurityStrategy {
	public boolean isAllowed(String... acls);
}
