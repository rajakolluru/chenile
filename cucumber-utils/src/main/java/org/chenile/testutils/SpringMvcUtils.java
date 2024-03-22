package org.chenile.testutils;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.chenile.base.response.ErrorType;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Utilities that allow testing of Aurora errors and warnings
 * @author Raja Shankar Kolluru
 *
 */
public class SpringMvcUtils {
	public static void assertErrors(ResultActions actions, int errorNum,int subErrorNum,
			String exceptionMessage) throws Exception {
		actions
		.andExpect(status().is(errorNum))
		.andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(errorNum))
        .andExpect(jsonPath("$.severity").value(ErrorType.ERROR.toString()))
        
        .andExpect(jsonPath("$.data").doesNotExist());
        if (subErrorNum != 0) {
        	actions.andExpect(jsonPath("$.subErrorCode").value(subErrorNum))
        	.andExpect(jsonPath("$.errors[0].subErrorCode").value(subErrorNum));
        }
	    actions.andExpect(jsonPath("$.errors[0].severity").value(ErrorType.ERROR.toString()));
	    
	    
		if(exceptionMessage != null) {
        	actions.andExpect(jsonPath("$.description").value(exceptionMessage))
        	.andExpect(jsonPath("$.errors[0].description").value(exceptionMessage));
        }
	}
	
	public static void assertWarnings(ResultActions actions, int errorNum,
			String warningMessage) throws Exception {
		 actions
		 .andExpect(header().exists(HttpHeaders.WARNING))
        
         .andExpect(jsonPath("$.success").value(true))
         .andExpect(jsonPath("$.severity").value(ErrorType.WARN.toString()))
         .andExpect(jsonPath("$.subErrorCode").value(errorNum))
         .andExpect(jsonPath("$.errors").isArray())
         .andExpect(jsonPath("$.errors[0].severity").value(ErrorType.WARN.toString()))      
         .andExpect(jsonPath("$.errors[0].subErrorCode").value(errorNum));
		 if(warningMessage != null) {
			 actions
			 .andExpect(jsonPath("$.errors[0].description").value(warningMessage))
			 .andExpect(header().string(HttpHeaders.WARNING,warningMessage))
			 .andExpect(jsonPath("$.description").value(warningMessage));
		 }
	}
	
}
