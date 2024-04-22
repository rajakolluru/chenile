package org.chenile.base.exception;

import java.io.Serial;

public class NotFoundException extends ErrorNumException {
	

	@Serial
	private static final long serialVersionUID = -8109926578240270390L;

	public NotFoundException (String message, Throwable cause) {
		super(404, message, cause);
	}
	public NotFoundException (int subErrorCode, String message) {
		super(404, subErrorCode, message);
	}

	public NotFoundException (int subErrorCode, Object[] params) {
		super(404, subErrorCode, params);
	}

	public NotFoundException ( String message) {
		super(404, message);
	}

	public NotFoundException (String message, int subErrorNum,Throwable cause) {
		super(404,subErrorNum, message, cause);
	}
	public NotFoundException(int subErrorNum, String message, Throwable cause) {
		super(404, subErrorNum, message, cause);
	}

	public NotFoundException(int subErrorNum, Object[] params, Throwable cause) {
		super(404, subErrorNum, params, cause);
	}
}
