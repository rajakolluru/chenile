package org.chenile.query.service;

import java.util.List;

import org.chenile.query.model.CannedReport;


/**
 * Retrieve and store canned reports.
 * Get a list of all the reports for a user and tenant Id combination
 * @author meratransport
 *
 */
public interface CannedReportStore {
	/**
	 * Retrieve the canned report by name. Make sure that the canned report is applicable to the current user
	 * and tenant. If the userId is null in the retrieved canned report, then the {@link CannedReport#isApplicableToAll()}
	 * must be set to true else the user Id must match the current userId. 
	 * @param cannedReportName the name of the canned report
	 * @param queryName the name of the query
	 * @return the report details or null if not found
	 */
	public CannedReport retrieve(String cannedReportName, String queryName);
	/**
	 * Store the canned report details into the DB.   
	 * @param cannedReport the canned report definition
	 */
	public void store(CannedReport cannedReport);
	/**
	 * For the current User and tenant, retrieve the following canned reports from the DB:
	 * All canned reports whose tenant Id and the user ID exactly match with the current user Id and the tenant Id 
	 * all the canned reports whose tenant ID matches the current tenant and whose User ID is null
	 * @param queryName - the name of the query
	 * @return the list of canned reports that match the query name
	 */
	public List<CannedReport> getAllCannedReportsForUserTenant(String queryName);
}
