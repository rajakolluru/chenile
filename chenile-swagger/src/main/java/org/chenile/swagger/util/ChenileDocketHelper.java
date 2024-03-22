/**
 * 
 */
package org.chenile.swagger.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import springfox.documentation.builders.PathSelectors;

/**
 * @author Deepak N
 *
 */
public final class ChenileDocketHelper {
	
	private ChenileDocketHelper() {}
	
	/**
	 * To remove the error mappings
	 * @return
	 */
	public static Predicate<String> otherPaths() {
		return Predicates.not(PathSelectors.regex("/error.*"));
	}

	/**
	 * Generate the paths based on the Set provided.
	 * @param urlSet
	 * @return
	 */
	public static Predicate<String> generatePaths(final Set<String> urlSet) {
		List<Predicate<String>> predicateList = new ArrayList<>(urlSet.size() + 1);
		predicateList.add(otherPaths());
		if (!urlSet.isEmpty()) {
			for (final String url : urlSet) {
				predicateList.add(PathSelectors.regex(url));
			}
		}
		return Predicates.or(predicateList);
	}

}
