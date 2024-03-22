package org.chenile.base.exception;

public class ServerException extends ErrorNumException{

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
    * @param message
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
