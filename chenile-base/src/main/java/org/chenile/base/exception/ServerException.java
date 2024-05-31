package org.chenile.base.exception;

import java.io.Serial;

/**
 * {@link ErrorNumException} with HTTP status code of 500 for runtime errors.
 */
public class ServerException extends ErrorNumException{

    @Serial
	private static final long serialVersionUID = 6520135959170422755L;

    /**
     *
     * @param message
     * @param cause
     */
    public ServerException(String message, Throwable cause) {
        super(500, message, cause);
    }
    
	/**
	*
	* @param message
	*/
	   public ServerException(  String message) {
	       super(500, message);
	   }

    /**
     *
     * @param message
     */
    public ServerException( int subErrorNum, String message) {
        super(500, subErrorNum,message);
    }

	/**
	 *
	 * @param subErrorNum
	 * @param params
	 */
	public ServerException( int subErrorNum, Object[] params) {
       super(500, subErrorNum,params);
   }
    
    /**
	 *
	 * @param subErrorNum
	 * @param message
	 * @param cause
	 */
	public ServerException(int subErrorNum, String message, Throwable cause) {
		super(500, subErrorNum, message, cause);
	}
	
	public ServerException(int subErrorNum, Object[] params, Throwable cause) {
		super(500, subErrorNum, params, cause);
	}
	
}
