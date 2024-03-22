package org.chenile.utils.region;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.interceptors.BaseChenileInterceptor;

/**
 * 
 * @author Raja Shankar Kolluru
 * <br/>This class accomplishes the following:<br/>
 * <ol>
 * <li>Copy the region header to the trajectory header(if trajectory ID is empty and region ID is non empty)</li>
 * <li> </li>
 * </ol>
 *
 */
public class RegionToTrajectoryConverter extends BaseChenileInterceptor {
	public static final String REGION_HEADER = "x-region";
	public static final String TRAJECTORY_HEADER = "chenile-trajectory-id";
	public static final String ACCEPT_LANG_HEADER = "accept-language";
	private Map<String,Locale> locales = new HashMap<>();
	
	public RegionToTrajectoryConverter() {
		// populate the locales from ccm2 or similar
		// locales.put("can", Locale.CANADA_FRENCH);
	}
	
	@Override
	protected void doPreProcessing(ChenileExchange exchange) {
		String regionCode = (String)exchange.getHeader(REGION_HEADER);
		String trajectoryId = (String)exchange.getHeader(TRAJECTORY_HEADER);
		if (regionCode != null && trajectoryId == null) {
			exchange.setHeader(TRAJECTORY_HEADER, regionCode);
		}
		// default to the correct language unless there is already an accept-language
		// header in the request
		String acceptLang = (String)exchange.getHeader(ACCEPT_LANG_HEADER);
		if (acceptLang != null)
			return;
		if (locales.get(regionCode) != null) {			
			// exchange.setHeader(ACCEPT_LANG_HEADER, acceptLang);
			// recompute the locale
			exchange.setLocale(locales.get(regionCode));
		}
	}

}
