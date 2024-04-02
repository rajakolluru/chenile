/**
 * 
 */
package org.chenile.security.service;

import org.chenile.security.model.EmployeeDetails;
import org.chenile.security.model.EmployeeRoles;

/**
 *
 *
 */
public interface ProfileServiceContextUtil {

	public EmployeeRoles getEmployeeRoles();
	public EmployeeDetails getEmployeeDetails();
	
}
